#include <VLan.h>
#include <ConfigConstants.h>
#include <Debug.h>
#include <Macros.h>
#include <sstream>
#include <sys/wait.h>
#include <aim_types.h>

VLan::VLan() : Service("VLan")
{
}

VLan::~VLan()
{
}

bool VLan::initialize(dictionary * configuration)
{
    ifconfig = getStringProperty(configuration, vlanIfConfigCmd);
    vconfig = getStringProperty(configuration, vlanVconfigCmd);
    brctl = getStringProperty(configuration, vlanBrctlCmd);

    bool ok = true;
    
    ok &= commandExist(ifconfig);
    ok &= commandExist(vconfig);
    ok &= commandExist(brctl);

    if (!ok)
    {
        LOG("Some required command is missing, check:\n\t%s\n\t%s\n\t%s", 
                ifconfig.c_str(), vconfig.c_str(), brctl.c_str());
    }

    return ok;
}

bool VLan::start()
{
    return true;
}

bool VLan::stop()
{
    return true;
}

bool VLan::cleanup()
{
    return true;
}

void VLan::throwError(const string& message)
{
    VLanException exception;
    exception.description = message;

    throw exception;
}

void VLan::createVLAN(int vlan, const string& vlanInterface, const string& bridgeInterface)
{
    ostringstream oss;
    int status;

    // Check the existence of the vlan tag and interface
    if (existsVlan(vlan, vlanInterface))
    {
        LOG("VLAN with tag %d and interface %s already exists.", vlan, vlanInterface.c_str());
    }
    else
    {
        // Create the VLAN and interface
        oss.str("");
        oss.flush();
        oss << ifconfig << " " << vlanInterface << " up; " << vconfig << " add " << vlanInterface << " " << vlan <<  " >/dev/null 2>/dev/null ; " << ifconfig << " " << vlanInterface << "." << vlan << " up"; 
        oss.flush();

        status = executeCommand(oss.str());
        oss.str("");
        oss.flush();

        switch (status)
        {
            case 127:
                oss << "Error creating VLAN with tag " << vlan << " and interface " << vlanInterface << ".";
                oss.flush();
  
                LOG("%s", oss.str().c_str());
                throwError(oss.str());
                break;

        case 0:
            LOG("VLAN created with tag %d and interface %s", vlan, vlanInterface.c_str());
            break;

        case 1:
            LOG("VLAN interface %s probably already exists. We return OK", vlanInterface.c_str());
            break;

        default:
            oss << "Error creating VLAN with tag " << vlan << " and interface " << vlanInterface << ".";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;
        }
    }

    // Check the existence of the bridge interface
    if (existsBridge(bridgeInterface))
    {
        LOG("Bridge interface %s already exists.", bridgeInterface.c_str());
    }
    else
    {
    // Create the bridge interface
    oss.str("");
    oss.flush();

    oss << brctl << " addbr " << bridgeInterface << " >/dev/null 2>/dev/null ; " << ifconfig << " " << bridgeInterface << " up";
    oss.flush();

    status = executeCommand(oss.str());
    oss.str("");
    oss.flush();

    switch (status)
    {
        case 127:
            oss << "Error creating bridge interface " << bridgeInterface  << ". Unable to create.";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;

        case 0:
            LOG("Bridge interface %s created.", bridgeInterface.c_str());
            break;

        case 1:
            LOG("Bridge interface %s probably already exists. We return OK.", bridgeInterface.c_str());
            break;

        default:
            oss << "Error creating bridge interface " << bridgeInterface  << ". Unable to create.";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;
        }
    }

    // Add interface
    oss.str("");
    oss.flush();

    oss << brctl << " addif " << bridgeInterface << " " << vlanInterface << "." << vlan;
    oss.flush();

    status = executeCommand(oss.str());
    oss.str();
    oss.flush();

    switch (status)
    {
        case 127:
            oss << "Error adding interface (" << bridgeInterface << ", " << vlanInterface << "." << vlan << "). Unable to create.";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;

        case 0:
            LOG("Interface added (%s, %s.%d)", bridgeInterface.c_str(), vlanInterface.c_str(), vlan);
            break;

        case 1:
            LOG("Interface is already added (%s, %s.%d)", bridgeInterface.c_str(), vlanInterface.c_str(), vlan);
            break;

        default:
            oss << "Error adding interface (" << bridgeInterface << ", " << vlanInterface << "." << vlan << "). Unable to create.";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;
    }

    LOG("VLan created, tag=%d, interface=%s, bridge=%s", vlan, vlanInterface.c_str(), bridgeInterface.c_str());
}

void VLan::deleteVLAN(int vlan, const string& vlanInterface, const string& bridgeInterface)
{
    ostringstream oss;
    int status;

    if (!existsBridge(bridgeInterface))
    {
        LOG("Bridge interface %s does not exist.", bridgeInterface.c_str());
    }
    else
    {
    int numInterfaces = countBridgeInterfaces(bridgeInterface);

    if (numInterfaces < 1)
    {
        oss.str("");
        oss.flush();
        oss << "Unable to get the number of bridge interfaces (" << bridgeInterface << ").";
        oss.flush();

        LOG("%s", oss.str().c_str());
        throwError(oss.str());
    }

    if (numInterfaces != 1)
    {
        oss.str("");
        oss.flush();
        oss << "There are more than one interface for " << bridgeInterface << ". The vlan will not be deleted.";
        oss.flush();

        LOG("%s", oss.str().c_str());
        throwError(oss.str());
    }

    oss.str("");
    oss.flush();
    oss << ifconfig << " " << bridgeInterface << " down ; " << brctl << " delbr " << bridgeInterface << " >/dev/null 2>/dev/null";
    oss.flush();

    status = executeCommand(oss.str());
    oss.str("");
    oss.flush();

    switch (status)
    {
        case 127:
            oss << "Error deleting bridge interface " << bridgeInterface << ".";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;

        case 0:
            LOG("Bridge interface %s deleted.", bridgeInterface.c_str());
            break;

        case 1:
            LOG("Could not delete bridge interface %s. It probably does not exist.", bridgeInterface.c_str());
            break;

        default:
            oss << "Error deleting bridge interface " << bridgeInterface << ".";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;
        }
    }

    if (!existsVlan(vlan, vlanInterface))
    {
        LOG("VLAN with tag %d and interface %s does not exist.", vlan, vlanInterface.c_str());
    }
    else
    {
    oss.str("");
    oss.flush();

    oss << ifconfig << " " << vlanInterface << "." << vlan << "; " << vconfig << " rem " << vlanInterface << "." << vlan << " >/dev/null 2>/dev/null";
    oss.flush();

    status = executeCommand(oss.str());
    oss.str("");
    oss.flush();

    switch (status)
    {
        case 127:
            oss << "Error deleting vlan interface " << vlanInterface << ".";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str());
            break;

        case 0:
            LOG("Vlan interface %s deleted.", vlanInterface.c_str());
            break;

        case 1:
            LOG("Could not delete vlan interface %s. It probably does not exist.", vlanInterface.c_str());
            break;

        default:
            oss << "Error deleting vlan interface " << vlanInterface << ".";
            oss.flush();

            LOG("%s", oss.str().c_str());
            throwError(oss.str()); 
            break;
        }
    }

    LOG("VLan deleted, tag=%d, interface=%s, bridge=%s", vlan, vlanInterface.c_str(), bridgeInterface.c_str());
}

int VLan::countBridgeInterfaces(const string& bridgeInterface)
{
    char command[250];
    int status;
    int pfd[2];
    pid_t cpid;
    char buf[10000];      //Optimitzar
    int found, len, point, interfaces, buflen;

    if (pipe(pfd) == -1)
    {
        return -1;
    }

    cpid = fork();

    if (cpid == -1)
    {
        return -1;
    }

    if (cpid == 0)
    {
        close(1);
        close(pfd[0]);
        dup(pfd[1]);
        snprintf (command, 250, "%s show 2>/dev/null", brctl.c_str());
        status = system (command);
        exit (0);
    }
    else
    {
        close (pfd[1]);
        status = 1;
        point = 0;

        while (status != 0)
        {
            status = read (pfd[0], buf + point, 10000);
            if (status < 0)
                return 2;
            point += status;
        }

        buflen = point;
        buf[buflen] = 0;
        close (pfd[0]);
        waitpid (cpid, &status, 0);

        sprintf (command, "%s", bridgeInterface.c_str());
        len = strlen (command);
        found = 0;
        point = 0;
        interfaces = 0;

        while (point < buflen) {
            if (found == 0 && strncmp (&buf[point], command, len) == 0) {
                interfaces = 1;
                found = 1;
            }
            //Go to next line
            while (buf[point] != '\n' && point < buflen)
                point++;
            if (point == buflen)
                break;
            point++;
            if (found == 1 && buf[point] != '\t')
                break;
            if (found == 1)
                interfaces = interfaces + 1;
        }
        LOG("Interfaces: %d", interfaces);
        return interfaces;
    }
}

void VLan::checkVlanRange(const int vlan)
{
    if (vlan < 1 || vlan > 4094)
    {
        ostringstream oss;

        oss << "VLAN tag out of range (" << vlan << ").";
        oss.flush();

        LOG("%s", oss.str().c_str());
        throwError(oss.str());
    }
}

bool VLan::existsVlan(const int vlan, const string& vlanInterface)
{
    checkVlanRange(vlan);

    ostringstream oss;

    oss << vlanInterface << "." << vlan;
    oss.flush();

    return existsInterface(oss.str());
}

bool VLan::existsInterface(const string& interface)
{
    ostringstream oss;

    oss << ifconfig << " " << interface << " > /dev/null 2>/dev/null";
    oss.flush();

    return (executeCommand(oss.str()) == 0);
}

bool VLan::existsBridge(const string& interface)
{
    return existsInterface(interface);
}

int VLan::executeCommand(string command, bool redirect)
{
    if (redirect)
    {
        command.append(" > /dev/null");
    }

    LOG("Executing '%s'", command.c_str());

    int status = system(command.c_str());

    return WEXITSTATUS(status);
}

bool VLan::commandExist(string& command)
{
    return (executeCommand(command, true) != 127);
}

void VLan::checkVLANConfiguration()
{
    string error = "Failed to check the command/s: ";
    bool ok = true;

    if (executeCommand(ifconfig) == 127)
    {
        ok = false;
        error.append(ifconfig).append(" ");
    }

    if (executeCommand(vconfig) == 127)
    {
        ok = false;
        error.append(vconfig).append(" ");
    }   

    if (executeCommand(brctl) == 127)
    {
        ok = false;
        error.append(brctl).append(" ");
    }   

    if (!ok)
    {
        LOG("%s", error.c_str());
        throwError(error);
    }
}
