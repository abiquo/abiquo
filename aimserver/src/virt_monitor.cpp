#include <virt_monitor.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/poll.h>
#include <libvirt/libvirt.h>
#include <Debug.h>

# ifndef ATTRIBUTE_UNUSED
#  define ATTRIBUTE_UNUSED __attribute__((__unused__))
# endif

int h_fd = 0;
virEventHandleType h_event = (virEventHandleType)0;
virEventHandleCallback h_cb = NULL;
virFreeCallback h_ff = NULL;
void *h_opaque = NULL;

int t_active = 0;
int t_timeout = -1;
virEventTimeoutCallback t_cb = NULL;
virFreeCallback t_ff = NULL;
void *t_opaque = NULL;

const char* eventToString(int event, int detail);
int eventAddHandleFunc(int fd, int event, virEventHandleCallback cb, void *opaque, virFreeCallback ff);
void eventUpdateHandleFunc(int watch, int event);
int  eventRemoveHandleFunc(int watch);
int eventAddTimeoutFunc(int timeout, virEventTimeoutCallback cb, void *opaque, virFreeCallback ff);
void eventUpdateTimeoutFunc(int timer, int timout);
int eventRemoveTimeoutFunc(int timer);
int eventHandleTypeToPollEvent(virEventHandleType events);
virEventHandleType pollEventToEventHandleType(int events);

int run = 0;
virConnectPtr dconn = NULL;
void (*notify_routine)(const char*, const char*) = NULL;

/* Callback functions */

const char *eventToString(int event, int detail) {
    const char *ret = "\0";

    switch(event)
    {
        case VIR_DOMAIN_EVENT_DEFINED:
            ret = CREATED;
            break;

        case VIR_DOMAIN_EVENT_UNDEFINED:
            ret = DESTROYED;
            break;

        case VIR_DOMAIN_EVENT_STARTED:
            ret = POWEREDON;
            break;

        case VIR_DOMAIN_EVENT_SUSPENDED:
            ret = SUSPENDED;
            break;

        case VIR_DOMAIN_EVENT_RESUMED:
            ret = RESUMED;
            break;

        case VIR_DOMAIN_EVENT_STOPPED:
            if (detail == VIR_DOMAIN_EVENT_STOPPED_SAVED)
            {
                ret = SAVED;
            }
            else
            {
                ret = POWEREDOFF;
            }

            break;
    }

    return ret;
}

static int domainEventCallback(virConnectPtr conn ATTRIBUTE_UNUSED, virDomainPtr dom, int event, int detail, void *opaque ATTRIBUTE_UNUSED)
{
    char* domainName = (char*)virDomainGetName(dom);
    int domainId = virDomainGetID(dom);
    const char* eventString = eventToString(event, detail);

    if (strlen(eventString) > 0)
    {
        LOG("%s EVENT: Domain %s(%d) %s", __func__, domainName, domainId, eventString);
        notify_routine(domainName, eventString);
    }

    return 0;
}

static void freeFunc(void *opaque)
{
    char *str = (char*)opaque;
    LOG("%s: Freeing [%s]", __func__, str);
    free(str);
}


/* EventImpl Functions */
int eventHandleTypeToPollEvent(virEventHandleType events)
{
    int ret = 0;
    if(events & VIR_EVENT_HANDLE_READABLE)
        ret |= POLLIN;
    if(events & VIR_EVENT_HANDLE_WRITABLE)
        ret |= POLLOUT;
    if(events & VIR_EVENT_HANDLE_ERROR)
        ret |= POLLERR;
    if(events & VIR_EVENT_HANDLE_HANGUP)
        ret |= POLLHUP;
    return ret;
}

virEventHandleType pollEventToEventHandleType(int events)
{
    int ret = 0;
    if(events & POLLIN)
        ret |= VIR_EVENT_HANDLE_READABLE;
    if(events & POLLOUT)
        ret |= VIR_EVENT_HANDLE_WRITABLE;
    if(events & POLLERR)
        ret |= VIR_EVENT_HANDLE_ERROR;
    if(events & POLLHUP)
        ret |= VIR_EVENT_HANDLE_HANGUP;
    return (virEventHandleType)ret;
}

int eventAddHandleFunc(int fd, int event, virEventHandleCallback cb, void *opaque, virFreeCallback ff)
{
    LOG("Add handle %d %d %p %p", fd, event, cb, opaque);
    h_fd = fd;
    h_event = (virEventHandleType)eventHandleTypeToPollEvent((virEventHandleType)event);
    h_cb = cb;
    h_ff = ff;
    h_opaque = opaque;
    return 0;
}

void eventUpdateHandleFunc(int fd, int event)
{
    LOG("Updated Handle %d %d", fd, event);
    h_event = (virEventHandleType)eventHandleTypeToPollEvent((virEventHandleType)event);
    return;
}

int  eventRemoveHandleFunc(int fd)
{
    LOG("Removed Handle %d", fd);
    h_fd = 0;
    if (h_ff)
        (h_ff)(h_opaque);
    return 0;
}

int eventAddTimeoutFunc(int timeout, virEventTimeoutCallback cb, void *opaque, virFreeCallback ff)
{
    LOG("Adding Timeout %d %p %p", timeout, cb, opaque);
    t_active = 1;
    t_timeout = timeout;
    t_cb = cb;
    t_ff = ff;
    t_opaque = opaque;
    return 0;
}

void eventUpdateTimeoutFunc(int timer ATTRIBUTE_UNUSED, int timeout)
{
    t_timeout = timeout;
}

int eventRemoveTimeoutFunc(int timer)
{
    LOG("Timeout removed %d", timer);
    t_active = 0;
    if (t_ff)
        (t_ff)(t_opaque);
    return 0;
}

int connect(const char * url, void (*callback_routine)(const char*, const char*))
{
    int callbackret = -1;

    virEventRegisterImpl(
            eventAddHandleFunc,
            eventUpdateHandleFunc,
            eventRemoveHandleFunc,
            eventAddTimeoutFunc,
            eventUpdateTimeoutFunc,
            eventRemoveTimeoutFunc );

    dconn = virConnectOpenReadOnly(url);

    if (!dconn)
    {
        return 0;
    }

    callbackret = virConnectDomainEventRegister(dconn, domainEventCallback, strdup("CALLBACK"), freeFunc);

    if (callbackret == -1)
    {
        return 0;
    }

    notify_routine = callback_routine;
    run = 1;

    return 1;
}

void cancel()
{
    run = 0;
}

void * listen(void* opaque)
{
    int sts;

    while(run)
    {
        struct pollfd pfd;
        pfd.fd = h_fd;
        pfd.events = h_event;
        pfd.revents = 0;

        sts = poll(&pfd, 1, TIMEOUT_MS);

        /* if t_timeout < 0 then t_cb must not be called */
        if (t_cb && t_active && t_timeout >= 0)
        {
            t_cb(t_timeout,t_opaque);
        }

        if (sts == 0)
        {
            continue;
        }

        if (sts < 0 )
        {
            //LOG("Poll failed");
            continue;
        }
        
        if ( pfd.revents & POLLHUP )
        {
            LOG("Reset by peer. Stopping listener.");
            // return -1; TODO
            run = 0;
        }

        if(h_cb)
        {
            h_cb(0, h_fd, pollEventToEventHandleType(pfd.revents & h_event), h_opaque);
        }
    }

    LOG("Deregistering event handlers");
    virConnectDomainEventDeregister(dconn, domainEventCallback);

    LOG("Closing connection");
    
    if( dconn && virConnectClose(dconn)<0 )
    {
        LOG("error closing");
    }

    return 0;
}
