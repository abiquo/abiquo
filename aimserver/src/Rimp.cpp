#include <Rimp.h>
#include <RimpUtils.h>
#include <ConfigConstants.h>
#include <Debug.h>
#include <Macros.h>

#include <aim_types.h>

Rimp::Rimp() : Service("Rimp")
{
}

Rimp::~Rimp()
{
}

bool Rimp::start()
{
    LOG("[DEBUG] [RIMP] start");
    return true;
}

bool Rimp::stop()
{
    LOG("[DEBUG] [RIMP] stop");
    return true;
}

bool Rimp::cleanup()
{
    LOG("[DEBUG] [RIMP] cleanup");
    return true;
}

bool Rimp::initialize(dictionary * configuration)
{
    const char* repository_c = getStringProperty(configuration, rimpRepository);
    const char* datastore_c = getStringProperty(configuration, rimpDatastore);

    if (repository_c == NULL || strlen(repository_c) < 2)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "\tcan not be read the ''repository'' configuration element "
                "\tset [rimp]\nrepository = XXXX ");

        return false;
    }

    if (datastore_c == NULL || strlen(datastore_c) < 2)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "\tcan not be read the ''datastore'' configuration element "
                "\tset [rimp]\ndatastore = XXXX ");

        return false;
    }

    repository = string(repository_c);
    datastore_default = string(datastore_c);

    // check ends with '/'
    if (repository.at(repository.size() - 1) != '/')
    {
        repository = repository.append("/");
    }
    if (datastore_default.at(datastore_default.size() - 1) != '/')
    {
        datastore_default = datastore_default.append("/");
    }

    bool initialized = true;

    /***
     * LOCAL REPOSITORY
     * */

    // check ''local repository'' exist or create it
    if (access(LOCAL_REPOSITORY.c_str(), F_OK) == -1)
    {
        int err = mkdir(LOCAL_REPOSITORY.c_str(), S_IRWXU | S_IRWXG);

        if (err == -1)
        {
            LOG("[ERROR] [RIMP] Initialization fails :\n"
                    "\tCan not create ''local repository'' folder at [%s]\n"
                    "\tCaused by: %s",LOCAL_REPOSITORY.c_str(), strerror(errno));

            initialized = false;
        }
    }// ''local repository'' exist
    else if (access(LOCAL_REPOSITORY.c_str(), W_OK | R_OK) == -1)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "''local repository'' folder at [%s] exist but can not be R/W",LOCAL_REPOSITORY.c_str());

        initialized = false;

    }// ''local repository'' exist


    bool initDatastore = checkDatastore(datastore_default);
    bool initRepository = checkRepository(repository);

    initialized = initialized && initDatastore && initRepository;

    return initialized;
}

void Rimp::checkRimpConfiguration()
{
    string error("Invalid RIMP configuration:");
    bool initDatastore = checkDatastore(datastore_default);
    bool initRepository = checkRepository(repository);

    if (!initDatastore)
    {
        error = error.append("\n''datastore'' :").append(datastore_default);
    }

    if (!initRepository)
    {
        error = error.append("\n''repository'' :").append(repository);
    }

    if (!initDatastore || !initRepository)
    {
        RimpException rexecption;
        rexecption.description = error;
        throw rexecption;
    }
}

int64_t Rimp::getDatastoreSize()
{
    unsigned long int space = getFreeSpaceOn(datastore_default);

    return space;
}

vector<Datastore> Rimp::getDatastores()
{
    LOG("[DEBUG] [RIMP] Get Datastores");

    return getDatastoresFromMtab();
}

vector<NetInterface> Rimp::getNetInterfaces()
{
    LOG("[DEBUG] [RIMP] Get Network Interfaces");

    return getNetInterfacesFromXXX();
}

int64_t Rimp::getDiskFileSize(const std::string& virtualImageDatastorePath)
{
    checkRimpConfiguration();

    LOG("[DEBUG] [RIMP] Get Disk File Size [%s]", virtualImageDatastorePath.c_str());

    // Check the file exist and can be read
     if (access(virtualImageDatastorePath.c_str(), F_OK | R_OK) == -1)
     {
         RimpException rexecption;
         string error ("File do not exist at [");
         error = error.append(virtualImageDatastorePath).append("]");

         LOG("[ERROR] [RIMP] %s", error.c_str());
         rexecption.description = error;
         throw rexecption;
     }

     return getFileSize(virtualImageDatastorePath);
}


void Rimp::copyFromRepositoryToDatastore(const std::string& virtualImageRepositoryPath, std::string& datastore,
        const std::string& virtualMachineUUID)
{
    string error("");
    RimpException rexecption;

    if (datastore.empty())
    {
        datastore = datastore_default;
    }
    else
    {
        // check datastore path end with '/'
        if (datastore.at(datastore.size() - 1) != '/')
        {
            datastore = datastore.append("/");
        }
    }

    checkRimpConfiguration();
    if (!checkDatastore(datastore))
    {
        error = error.append("Provided ''datastore'' :").append(datastore).append(" can not be used");
        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    LOG("[DEBUG] [RIMP] Instantiating virtual image [%s] for virtual machien [%s]",
            virtualImageRepositoryPath.c_str(), virtualMachineUUID.c_str());

    /**
     * 1.- copy form ''Repository'' to ''Local Repository''.
     * **/

    string viRepositoryPath(repository);
    viRepositoryPath = viRepositoryPath.append(virtualImageRepositoryPath);

    // Check the source file (on the repository) exist and can be read
    if (access(viRepositoryPath.c_str(), F_OK | R_OK) == -1)
    {
        error = error.append("Source file do not exist at [").append(viRepositoryPath).append("]");

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    unsigned long int viSize = getFileSize(viRepositoryPath);

    string viLocalRepoPath(LOCAL_REPOSITORY);
    viLocalRepoPath = viLocalRepoPath.append(virtualImageRepositoryPath);

    // Check if the image is already on the local repository
    if (access(viLocalRepoPath.c_str(), F_OK | R_OK) == -1)
    {
        // Checking there are enough free space to copy
        unsigned long int localRepositoryFreeSize = getFreeSpaceOn(LOCAL_REPOSITORY);

        if (localRepositoryFreeSize < viSize)
        {
            error = error.append("There is no enough size to copy the file :");
            error = error.append(viRepositoryPath).append(" to :").append(LOCAL_REPOSITORY);

            LOG("[ERROR] [RIMP] %s", error.c_str());
            rexecption.description = error;
            throw rexecption;
        }

        string copyError1 = fileCopy(viRepositoryPath, viLocalRepoPath);
        if (!copyError1.empty())
        {
            error = error.append("Can not copy to :").append(viLocalRepoPath);
            error = error.append("\nCaused by :").append(copyError1);

            LOG("[ERROR] [RIMP] %s", error.c_str());
            rexecption.description = error;
            throw rexecption;
        }

        // check it was successfully copied
        if (access(viLocalRepoPath.c_str(), F_OK) == -1)
        {
            error = error.append("The destination file can not be created at :").append(viLocalRepoPath);

            LOG("[ERROR] [RIMP] %s", error.c_str());
            rexecption.description = error;
            throw rexecption;
        }
    }// copy to local repository


    /**
     * 2.- copy form ''Local Repository'' to ''Datastore''.
     * **/

    string viDatastorePath(datastore);
    viDatastorePath = viDatastorePath.append(virtualMachineUUID);

    // if the file exist on the datastore delete it
    if (access(viDatastorePath.c_str(), F_OK) == 0)
    {
        LOG("[WARNING] [RIMP] File with the same UUID already present on the ''datastore'' [%s], removing it.",
                viDatastorePath.c_str());

        remove(viDatastorePath.c_str());
        // TODO also remove ORIGINAL and LINKS (¿ delete using UUID ?)
    }

    // XXX viSize is the same on the repositroy and on the local repository
    unsigned long int datastoreFreeSize = getFreeSpaceOn(datastore);

    if (datastoreFreeSize < viSize)
    {
        error = error.append("There is no enough size to copy the file :");
        error = error.append(viLocalRepoPath).append(" to :").append(datastore);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    string copyError2 = fileCopy(viLocalRepoPath, viDatastorePath);
    if (!copyError2.empty())
    {
        error = error.append("Can not copy to :").append(viDatastorePath).append("\nCaused by :").append(copyError2);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    /**
     * 3.- create the links to relate ''datastore'' <-> ''local repository''
     * */

    string errorLink = createLinkFormClonedToLocal(viLocalRepoPath, viDatastorePath, virtualMachineUUID);
    if (!errorLink.empty())
    {
        remove(viDatastorePath.c_str());

        error = error.append("Can not create virtual image relations for virtual machine:");
        error = error.append(virtualMachineUUID).append("\nCaused by :").append(errorLink);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    LOG("[INFO] [RIMP] Created virtual image instance for virtual machine [%s]", virtualMachineUUID.c_str());
}

void Rimp::deleteVirtualImageFromDatastore(std::string& datastore, const std::string& virtualMachineUUID)
{
    string error("");
    RimpException rexecption;

    if (datastore.empty())
    {
        datastore = datastore_default;
    }
    else
    {
        // check datastore path end with '/'
        if (datastore.at(datastore.size() - 1) != '/')
        {
            datastore = datastore.append("/");
        }
    }

    checkRimpConfiguration();
    if (!checkDatastore(datastore))
    {
        error = error.append("Provided ''datastore'' :").append(datastore).append(" can not be used");
        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    LOG("[DEBUG] [RIMP] Deleting virtual machine [%s]", virtualMachineUUID.c_str());

    string viDatastorePath(datastore);
    viDatastorePath = viDatastorePath.append(virtualMachineUUID);

    // check the file exist on the datastore
    if (access(viDatastorePath.c_str(), F_OK) == -1)
    {
        error = error.append("Virtual image file do not exist on the ''datastore'' :");
        error = error.append(viDatastorePath);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    string viLocalRepoPath = followLinkToLocalRepository(viDatastorePath);
    if (viLocalRepoPath.empty())
    {
        error = error.append("Can not locate the original file on the ''local repositroy'' for :");
        error = error.append(viDatastorePath);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    remove(viDatastorePath.c_str());

    string errorLinks = deleteLinkToClonedAndCheckUsedLocal(viLocalRepoPath, virtualMachineUUID);
    if (!errorLinks.empty())
    {
        error = error.append("Can not delete the relations for the virtual machine:");
        error = error.append(virtualMachineUUID).append("Caused by :").append(errorLinks);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    LOG("[INFO] [RIMP] Deleted virtual machine [%s]", virtualMachineUUID.c_str());
}

void Rimp::copyFromDatastoreToRepository(const std::string& virtualMachineUUID, const std::string& snapshot,
        const std::string& destinationRepositoryPathIn, const std::string& sourceDatastorePathIn)
{
    string error("");
    RimpException rexecption;

    checkRimpConfiguration();

    string destinationRepositoryPath(repository);
    destinationRepositoryPath = destinationRepositoryPath.append(destinationRepositoryPathIn);

    string sourceDatastorePath(sourceDatastorePathIn);

    if (destinationRepositoryPath.at(destinationRepositoryPath.size() - 1) != '/')
    {
        destinationRepositoryPath = destinationRepositoryPath.append("/");
    }
    if (sourceDatastorePath.at(sourceDatastorePath.size() - 1) != '/')
    {
        sourceDatastorePath = sourceDatastorePath.append("/");
    }

    /**
     * TODO destination repository concat configured ''repository''
     * */

    LOG("[DEBUG] [RIMP] Creating virtual machine [%s] instance [%s] from [%s] to [%s]",
            virtualMachineUUID.c_str(), snapshot.c_str(), sourceDatastorePath.c_str(),
            destinationRepositoryPath.c_str());

    // Check destination repository folder exist and can be written
    if (access(destinationRepositoryPath.c_str(), F_OK | W_OK) == -1)
    {
        int err = mkdir(destinationRepositoryPath.c_str(), S_IRWXU | S_IRWXG);

        if (err == -1)
        {
            error = error.append("Can not create destination ''repository'' folder at :");
            error = error.append(destinationRepositoryPath).append(" Caused by: ").append(strerror(errno));

            LOG("[ERROR] [RIMP] %s", error.c_str());
            rexecption.description = error;
            throw rexecption;
        }
    }

    string viDatastoreSource(sourceDatastorePath);
    viDatastoreSource = viDatastoreSource.append(virtualMachineUUID);

    // Check source file exist and can be read
    if (access(viDatastoreSource.c_str(), F_OK | R_OK) == -1)
    {
        error = error.append("Source file do not exist or can not be read: ");
        error = error.append(viDatastoreSource);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    string viRepositoryDestination(destinationRepositoryPath);
    viRepositoryDestination = viRepositoryDestination.append(snapshot);

    // Check target path do not exist
    if (access(viRepositoryDestination.c_str(), F_OK) == 0)
    {
        error = error.append("Snapshot already exist on the repository: ");
        error = error.append(viRepositoryDestination);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    // Checking there are enough free space to copy
    unsigned long int viSize = getFileSize(viDatastoreSource);
    unsigned long int repositoryFreeSize = getFreeSpaceOn(destinationRepositoryPath);

    if (repositoryFreeSize < viSize)
    {
        error = error.append("There is no enough size to copy the file :");
        error = error.append(viDatastoreSource).append(" to :").append(viRepositoryDestination);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    string errorCopy = fileCopy(viDatastoreSource, viRepositoryDestination);
    if (!errorCopy.empty())
    {
        error = error.append("Can not copy to :");
        error = error.append(viRepositoryDestination).append("\nCaused by :").append(errorCopy);

        LOG("[ERROR] [RIMP] %s", error.c_str());
        rexecption.description = error;
        throw rexecption;
    }

    LOG("[INFO] [RIMP] Created snapshot [%s] from virtual machine [%s]", snapshot.c_str(), virtualMachineUUID.c_str());
}
