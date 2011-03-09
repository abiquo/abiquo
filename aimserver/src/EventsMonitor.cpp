#include <EventsMonitor.h>
#include <virt_monitor.h>
#include <iniparser.h>
#include <Macros.h>
#include <ConfigConstants.h>
#include <Debug.h>
#include <sstream>
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <netdb.h>

EventsMonitor::EventsMonitor() : Service("EventsMonitor")
{
}

EventsMonitor::~EventsMonitor()
{
}

EventsMonitor* EventsMonitor::instance = NULL;
string EventsMonitor::host = "";
string EventsMonitor::machineAddress = "";
int EventsMonitor::port = -1;
string EventsMonitor::machinePort = "";

EventsMonitor* EventsMonitor::getInstance()
{
    if (instance == NULL)
    {
        instance = new EventsMonitor();
    }

    return instance;
}

void EventsMonitor::callback(const char* uuid, const char* event)
{
    LOG("Publish '%s' event on machine '%s' to redis.", event, uuid);

    redisContext *c = redisConnect(EventsMonitor::host.c_str(), EventsMonitor::port);

    if (c->err)
    {
            LOG("Unable to connect to redis %s:%d. %s", EventsMonitor::host.c_str(), EventsMonitor::port, c->errstr);
    }   

    void* reply = redisCommand(c, "PUBLISH EventingChannel %s|%s|http://%s:%s/", uuid, event, EventsMonitor::machineAddress.c_str(), EventsMonitor::machinePort.c_str());

    if (reply != NULL)
    {
        freeReplyObject(reply);
        redisFree(c);
    }
    else
    {
        LOG("Unable to notify event. %s", c->errstr);
    }   
}

bool EventsMonitor::initialize(dictionary * configuration)
{
    const char* uri = getStringProperty(configuration, monitorUri);
    EventsMonitor::host = getStringProperty(configuration, redisHost);
    EventsMonitor::port = getIntProperty(configuration, redisPort);
    EventsMonitor::machinePort = getStringProperty(configuration, serverPort);

    bool initialized = true;

    // connect to libvirt
    if (!connect(uri, callback))
    {
        LOG("Unable to connect to hypervisor uri '%s'", uri);
        initialized = false;
    }

    if (initialized)
    {
        EventsMonitor::machineAddress = getIP(EventsMonitor::host, EventsMonitor::port);
        initialized = !EventsMonitor::machineAddress.empty();
    }

    if (initialized)
    {
        LOG("Physical machine address is http://%s:%s", EventsMonitor::machineAddress.c_str(), EventsMonitor::machinePort.c_str());
    }

    return initialized;
}

string EventsMonitor::getIP(string& address, int port)
{
    struct sockaddr_in to, my;
    int sd, rc;

    struct hostent *hp = gethostbyname(address.c_str());

    if (hp == NULL)
    {
        LOG("Unable to resolve hostname %s", address.c_str());
        return string("");
    }

    sd = socket(AF_INET, SOCK_STREAM, 0);

    to.sin_family = AF_INET;
    bcopy ( hp->h_addr, &(to.sin_addr.s_addr), hp->h_length);
    to.sin_port = htons(port);
    memset(&(to.sin_zero), 0, sizeof(to.sin_zero));

    rc = connect(sd, (struct sockaddr *)&to, sizeof(struct sockaddr_in));

    if (rc < 0)
    {
        LOG("Unable to connect to %s:%d", address.c_str(), port);
        return string("");
    }

    socklen_t len = sizeof(struct sockaddr);
    rc = getsockname(sd, (struct sockaddr *)&my, &len);

    if (rc < 0)
    {
        LOG("Unable to get the sock name %s:%d", address.c_str(), port);
        return string("");
    }

    string ip = string(inet_ntoa(my.sin_addr));

    close(sd);

    return ip;
}

bool EventsMonitor::start()
{
    pthread_attr_t attrJoinable;

    pthread_attr_init(&attrJoinable);
    pthread_attr_setdetachstate(&attrJoinable, PTHREAD_CREATE_JOINABLE);

    int ret = pthread_create(&threadId, &attrJoinable, listen, NULL);
    return (ret != 1);
}

bool EventsMonitor::stop()
{
    cancel();
    pthread_join(threadId, NULL); 

    return true;
}

bool EventsMonitor::cleanup()
{
    return true;
}

