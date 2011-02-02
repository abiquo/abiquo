#include <Aim.h>
#include <protocol/TBinaryProtocol.h>
#include <server/TSimpleServer.h>
#include <transport/TServerSocket.h>
#include <transport/TBufferTransports.h>

#include <getopt.h>
#include <iniparser.h>
#include <dictionary.h>

#include <AimServer.h>
#include <Service.h>
#include <ConfigConstants.h>
#include <Macros.h>
#include <Debug.h>

#include <vector>
#include <signal.h>
#include <sys/stat.h>

#include <version.h>

#define checkProperty(c, e) if (!existProperty(c, e)) { LOG("Missing configuration property '%s'", e); return false; }
#define existProperty(c, e) (iniparser_find_entry(c, e) != 0)
#define logservice(action, name, current, all) LOG("[%d/%d] %s %s service", current, all, action, name)

using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;

using namespace std;

int main(int argc, char **argv)
{
    // Parse command line arguments
    dictionary *commandLineConfiguration = iniparser_new(); 
    const char* configFilename = parseArguments(argc, argv, commandLineConfiguration);

    if (emptyString(configFilename))
    {
        configFilename = DEFAULT_CONFIG;
    }

    // Load configuration file
    configuration = iniparser_load(configFilename);

    if (configuration == NULL)
    {
        LOG("Unable to load '%s'", configFilename);
        exit(EXIT_FAILURE);
    }

    // Overlay configuration with command line arguments
    iniparser_merge(commandLineConfiguration, configuration);
    iniparser_freedict(commandLineConfiguration);

    // Daemonize
    if (existProperty(configuration, daemonizeServer))
    {
        daemonize();
    }

    // Print configuration summary
    LOG("Configuration from '%s'", configFilename);
    
    if (!checkConfiguration(configuration))
    {
        exit(EXIT_FAILURE);
    }

    printConfiguration(configuration);

    // Aim server initialization
    LOG("Initializing server");
    shared_ptr<TProcessor> processor(new AimProcessor(aimHandler));
    shared_ptr<TServerTransport> serverTransport(new TServerSocket(getIntProperty(configuration, serverPort)));
    shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
    shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());

    // Aim services initialization and start
    vector<Service*> services = aimHandler->getServices();
    vector<Service*>::iterator it;
    int i;

    for (it = services.begin(), i = 1; it < services.end(); ++it, i++)
    {
        Service* service = *it;

        logservice("Initializing", service->getName(), i, (int)services.size());
        if (!service->initialize(configuration))
        {
            exit(EXIT_FAILURE);
        }
    }

    for (it = services.begin(), i = 1; it < services.end(); ++it, i++)
    {
        Service* service = *it;

        logservice("Starting", service->getName(), i, (int)services.size());
        if (!service->start())
        {
            exit(EXIT_FAILURE);
        }
    }

    // Using signals to deinitialize
    signal(SIGINT, deinitialize);
    signal(SIGTERM, deinitialize);

    // Main loop
    LOG("Aim listening at port %d", getIntProperty(configuration, serverPort));
    TSimpleServer server(processor, serverTransport, transportFactory, protocolFactory);
    server.serve();

    exit(EXIT_FAILURE);
}

void deinitialize(int param)
{
    vector<Service*> services = aimHandler->getServices();
    vector<Service*>::iterator it;
    bool done = false;
    int i;

    for (it = services.begin(), i = 1; it < services.end(); ++it, i++)
    {
        Service* service = *it;

        logservice("Stopping", service->getName(), i, (int)services.size());
        done = service->stop();
        done = (done ? service->cleanup() : false);

        if (!done)
        {
            LOG("Unable to stop properly %s service", service->getName());
        }
    }

    iniparser_freedict(configuration);

    LOG("Bye!");
    exit(EXIT_SUCCESS);
}

const char * parseArguments(int argc, char **argv, dictionary *d)
{
    int next_opt, ret;
    const char * filename = "\0";

    while ((next_opt = getopt_long(argc, argv, short_opt, long_opt, NULL)) != -1)
    {
        switch (next_opt)
        {
            case 'h':
                printUsage(argv[0]);
                exit(EXIT_SUCCESS);
                break;

            case 'c':
                filename = optarg;
                break;

            case 'p':
                ret = iniparser_setstring(d, serverPort, optarg);
                break;

            case 'u':
                ret = iniparser_setstring(d, monitorUri, optarg);
                break; 

            case 'r':
                ret = iniparser_setstring(d, rimpRepository, optarg);
                break;

            case 's':
                ret = iniparser_setstring(d, rimpDatastore, optarg);
                break;

            case 'd':
                ret = iniparser_setstring(d, daemonizeServer, daemonizeServer);
                break;

            case 'v':
                printf("AIM server version %s\n", AIM_VERSION);
                exit(EXIT_SUCCESS);

            default:
                printUsage(argv[0]);
                exit(EXIT_FAILURE);
        }
    }

    return filename;
}

void printUsage(const char* program)
{
    printf("Usage: %s options\n", program);
    printf( "    -h --help                       Show this help\n"
            "    -c --config-file=<file>         Alternate configuration file\n"
            "    -p --port=<port>                Port to bind\n"
            "    -d --daemon                     Run as daemon\n"
            "    -u --uri=<uri>                  Hypervisor URI\n"
            "    -r --repository=<repository>    Repository export location\n"
            "    -s --datastore=<datastore>      Local file system path\n" 
            "    -v --version                    Show AIM server version\n" );
}

void printConfiguration(dictionary * d)
{
    iniparser_dumpfields(d, stderr); 
}

bool checkConfiguration(dictionary * d)
{
    // Server
    checkProperty(d, serverPort);

    // Monitor
    checkProperty(d, monitorUri);
    checkProperty(d, redisHost);
    checkProperty(d, redisPort);

    // Rimp
    checkProperty(d, rimpRepository);
    checkProperty(d, rimpDatastore);

    // VLan
    checkProperty(d, vlanIfConfigCmd);
    checkProperty(d, vlanVconfigCmd);
    checkProperty(d, vlanBrctlCmd);

    return true;
}

static void daemonize(void)
{
    pid_t pid, sid;

    // Already a daemon
    if ( getppid() == 1 )
    {
        return;
    }

    // Fork off the parent process
    pid = fork();
    if (pid < 0)
    {
        exit(EXIT_FAILURE);
    }
    
    if (pid > 0)
    {
        // Exit the parent process
        exit(EXIT_SUCCESS);
    }

    // At this point we are executing as the child process

    // Change the file mode mask
    umask(0);

    // Create a new SID for the child process
    sid = setsid();
    if (sid < 0)
    {
        exit(EXIT_FAILURE);
    }

    if ((chdir("/")) < 0)
    {
        exit(EXIT_FAILURE);
    }

    // Redirect standard files to /dev/null
    freopen( "/dev/null", "r", stdin);
    freopen( "/dev/null", "w", stdout);
    freopen( "/dev/null", "w", stderr);
}
