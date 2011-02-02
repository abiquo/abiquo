#include <NodeConfig.h>
#include <Debug.h>
#include <Macros.h>

#include <iniparser.h>
#include <dictionary.h>

NodeConfig::NodeConfig() : Service("Node configuration")
{
    iscsiInitiatorNameFile = ISCSI_DEFAULT_INITIATOR_NAME_FILE;
}

NodeConfig::~NodeConfig()
{
}

bool NodeConfig::initialize(dictionary * configuration)
{
    dictionary* d = iniparser_load(iscsiInitiatorNameFile.c_str());

    bool initialized = (d != NULL);

    iniparser_freedict(d);
    
    return initialized;
}

bool NodeConfig::start()
{
    return true;
}

bool NodeConfig::stop()
{
    return true;
}

bool NodeConfig::cleanup()
{
    return true;
}

void NodeConfig::getInitiatorIQN(string& iqn)
{
    dictionary* d = iniparser_load(iscsiInitiatorNameFile.c_str());
    iqn = "";

    if (d == NULL)
    {
        LOG("Unable to load %s. The IQN returned will be empty.", iscsiInitiatorNameFile.c_str());
    }

    if (iniparser_find_entry(d, ":InitiatorName") != 0)
    {
        iqn = getStringProperty(d, ":InitiatorName");
    }

    LOG("Request for node ISCSI initiator iqn = '%s'", iqn.c_str());
}

