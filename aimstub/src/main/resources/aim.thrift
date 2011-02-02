# aim.thrift

struct Datastore
{
    1:string device;
    2:string path;
    3:string type;
    4:i64 totalSize;
    5:i64 usableSize;
}

struct NetInterface
{
    1:string name;
    2:string address;
    3:string physicalAddress;    	
}

exception RimpException
{
    1:string description;
}

exception VLanException
{
    1:string description;
}

service Aim
{
    # Rimp procedures
    void checkRimpConfiguration() throws (1:RimpException re),
    i64 getDatastoreSize() throws (1:RimpException re),
    i64 getDiskFileSize(1:string virtualImageDatastorePath) throws (1:RimpException re),
    list<Datastore> getDatastores() throws (1:RimpException re),
    list<NetInterface> getNetInterfaces() throws (1:RimpException re),    
    void copyFromRepositoryToDatastore(1:string virtualImageRepositoryPath, 2:string datastorePath, 3:string virtualMachineUUID) throws (1:RimpException re),
    void deleteVirtualImageFromDatastore(1:string datastorePath, 2:string virtualMachineUUID) throws (1:RimpException re),
    void copyFromDatastoreToRepository(1:string virtualMachineUUID, 2:string snapshot, 3:string destinationRepositoryPath, 4:string sourceDatastorePath) throws (1:RimpException re),

    # VLan procedures
    void createVLAN (1:i32 vlanTag, 2:string vlanInterface, 3:string bridgeInterface) throws (1:VLanException ve),
    void deleteVLAN (1:i32 vlanTag, 2:string vlanInterface, 3:string bridgeInterface) throws (1:VLanException ve),
    void checkVLANConfiguration() throws (1:VLanException ve),

    # Node configuration procedures
    string getInitiatorIQN()
}
