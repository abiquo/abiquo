#include <RimpUtils.h>

bool checkDatastore(const string& datastore)
{
    bool success = true;

    // check ''datastore'' exist and can be r/w
    if (access(datastore.c_str(), F_OK | R_OK | W_OK) == -1)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "''datastore'' [%s] do not exist or can not be r/w", datastore.c_str());

        success = false;
    } // ''datastore'' exist

    return success;
}

const string MTAB_FILE("/etc/mtab");

vector<string> createInvalidTypeVector()
{
    vector < string > types;
    types.push_back(string("proc"));
    types.push_back(string("sysfs"));
    types.push_back(string("fusectl"));
    types.push_back(string("debugfs"));
    types.push_back(string("securityfs"));
    types.push_back(string("tmpfs"));
    types.push_back(string("devpts"));
    types.push_back(string("tmpfs"));
    types.push_back(string("rpc_pipefs"));
    types.push_back(string("vmblock"));
    types.push_back(string("nfsd"));
    types.push_back(string("binfmt_misc"));
    types.push_back(string("fuse.gvfs-fuse-daemon"));
   
    return types;
}

static vector<string> invalidtypes = createInvalidTypeVector();

vector<Datastore> getDatastoresFromMtab()
{
    vector<Datastore> datastores;

    struct mntent *ent = NULL;
    FILE *mounts = setmntent(MTAB_FILE.c_str(), "r");

    while ((ent = getmntent(mounts)) != NULL)
    {
        string type(ent->mnt_type);
        string directory(ent->mnt_dir);

        // check if the file system type should be considered
        bool found = false;
        for (int i = 0; (i < (int) invalidtypes.size()) && !found; i++)
        {
            int com = invalidtypes[i].compare(type);
            found = (com == 0);
        }

        if (!found && directory.compare("/boot"))
        {
            Datastore datastore;

            char* device = ent->mnt_fsname;
            char* path = ent->mnt_dir;
            unsigned long int totalSize = getTotalSpaceOn(path);
            unsigned long int usableSize = getFreeSpaceOn(path);

            datastore.device = string(device);
            datastore.path = string(path);
            datastore.type = type;
            datastore.totalSize = totalSize;
            datastore.usableSize = usableSize;

            datastores.push_back(datastore);
        }
    }

    endmntent(mounts);
    //free( mountP);

    return datastores;
}

int detect_mii(int skfd, char *ifname)
{
    struct ifreq ifr;
    u16 *data, mii_val;
    unsigned phy_id;

    /* Get the vitals from the interface. */
    strncpy(ifr.ifr_name, ifname, IFNAMSIZ);
    if (ioctl(skfd, SIOCGMIIPHY, &ifr) < 0)
    {
        // fprintf(stderr, "SIOCGMIIPHY on %s failed: %s\n", ifname, strerror(errno));
        // (void) close(skfd);
        return 2;
    }

    data = (u16 *) (&ifr.ifr_data);
    phy_id = data[0];
    data[1] = 1;

    if (ioctl(skfd, SIOCGMIIREG, &ifr) < 0)
    {
        // fprintf(stderr, "SIOCGMIIREG on %s failed: %s\n", ifr.ifr_name, strerror(errno));
        return 2;
    }

    mii_val = data[3];

    return (((mii_val & 0x0016) == 0x0004) ? 0 : 1);
}

int detect_ethtool(int skfd, char *ifname)
{
    struct ifreq ifr;
    struct ethtool_value edata;

    memset(&ifr, 0, sizeof(ifr));
    edata.cmd = ETHTOOL_GLINK;

    strncpy(ifr.ifr_name, ifname, sizeof(ifr.ifr_name) - 1);
    ifr.ifr_data = (char *) &edata;

    if (ioctl(skfd, SIOCETHTOOL, &ifr) == -1)
    {
        // printf("ETHTOOL_GLINK failed: %s\n", strerror(errno));
        return 2;
    }
    return (edata.data ? 0 : 1);
}

/**
 * http://www.linuxjournal.com/article/6908
 * @return 0, up. 1 down. 2 unknown
 * */
int netIfaceUp(int skfd, char *ifname)
{
    int stat = detect_ethtool(skfd, ifname);

    if (stat == 2)
    {
        stat = detect_mii(skfd, ifname);
    }

    return stat;
}

string getHardwareAddress(const char* devname)
{
    int sockfd;
    int io;
    struct ifreq ifr;
    RimpException rimpexc;

    sprintf(ifr.ifr_name, "%s", devname);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
    {
        rimpexc.description = "Can not open socket stream";
        throw rimpexc;
    }

    io = ioctl(sockfd, SIOCGIFHWADDR, (char *) &ifr);
    if (io < 0)
    {
        rimpexc.description = string("Can not obtain hardware address for device").append(devname);
        throw rimpexc;
    }

    char macBuffer[17];

    sprintf(macBuffer, "%02x:%02x:%02x:%02x:%02x:%02x", (unsigned char) ifr.ifr_hwaddr.sa_data[0],
            (unsigned char) ifr.ifr_hwaddr.sa_data[1], (unsigned char) ifr.ifr_hwaddr.sa_data[2],
            (unsigned char) ifr.ifr_hwaddr.sa_data[3], (unsigned char) ifr.ifr_hwaddr.sa_data[4],
            (unsigned char) ifr.ifr_hwaddr.sa_data[5]);


    close(sockfd);

    return string(macBuffer);
}

/**
 * Get all the network device names from the ''/pro/net/dev''
 * Based on the code from  http://www.doctort.org/adam/ by Adam Pierce <adam@doctort.org>
 * */
vector<NetInterface> getNetInterfacesFromXXX()
{
    vector<NetInterface> ifaces;
    RimpException rimpexc;

    char line[512];
    char *colon;
    char *name;
    FILE *fp;
    int sck;

    /* Get a socket handle. */
    sck = socket(AF_INET, SOCK_DGRAM, 0);
    if (sck < 0)
    {
        rimpexc.description = "Can not obtain the socket handler";
        throw rimpexc;
    }

    if (0 == (fp = fopen("/proc/net/dev", "r")))
    {
        rimpexc.description = "Can open ''/proc/net/dev''";
        throw rimpexc;
    }

    while (0 != (name = fgets(line, 512, fp)))
    {
        while (isspace(name[0])) /* Trim leading whitespace */
        {
            name++;
        }

        colon = strchr(name, ':');

        if (colon)
        {
            *colon = 0;

            string inetdevice(name);
            //XXX string inetip(inet_ntoa(((struct sockaddr_in *) &item->ifr_addr)->sin_addr));
            int faceup = netIfaceUp(sck, name);

            if (inetdevice.compare(string("lo")) != 0 && faceup == 0)
            {
                NetInterface netiface;
                netiface.name = inetdevice;
                // XXX netiface.address = inetip;
                netiface.physicalAddress =  getHardwareAddress(name);
                ifaces.push_back(netiface);

            }// link up and not loopback


        }// its a device
    }// all net devices

    close(sck);
    fclose(fp);
    return ifaces;
}

/**
 * Based on the code from  http://www.doctort.org/adam/ by Adam Pierce <adam@doctort.org>
 * */
vector<NetInterface> getNetInterfacesFromYYY()
{
    vector<NetInterface> ifaces;

    char buf[1024];
    struct ifconf ifc;
    struct ifreq *ifr;
    int sck;
    int nInterfaces;
    int i;

    /* Get a socket handle. */
    sck = socket(AF_INET, SOCK_DGRAM, 0);
    if (sck < 0)
    {
        RimpException rimpexc;
        rimpexc.description = "Can not obtain the socket handler";
        throw rimpexc;
    }

    /* Query available interfaces. */
    ifc.ifc_len = sizeof(buf);
    ifc.ifc_buf = buf;
    if (ioctl(sck, SIOCGIFCONF, &ifc) < 0)
    {
        RimpException rimpexc;
        rimpexc.description = "Can not query available interfaces\n.ioctl(SIOCGIFCONF)";
        throw rimpexc;
    }

    /* Iterate through the list of interfaces. */
    ifr = ifc.ifc_req;
    nInterfaces = ifc.ifc_len / sizeof(struct ifreq);
    for (i = 0; i < nInterfaces; i++)
    {
        struct ifreq *item = &ifr[i];

        printf("*********************************\n net iface %s", item->ifr_name);

        //int com = invalidtypes[i].compare(type);
        //found = (com == 0);

        string inetdevice(item->ifr_name);
        //XXX string inetip(inet_ntoa(((struct sockaddr_in *) &item->ifr_addr)->sin_addr));
        int faceup = netIfaceUp(sck, item->ifr_name);

        printf("*********************************\n net iface %s \t %d \n*********************************\n",
                inetdevice.c_str(), faceup);

        if (inetdevice.compare(string("lo")) != 0 && faceup == 0)
        {
            // Get the MAC address
            // http://cboard.cprogramming.com/linux-programming/43261-ioctl-request-get-hw-address.html
            if (ioctl(sck, SIOCGIFHWADDR, item) < 0)
            {
                RimpException rimpexc;
                rimpexc.description = string("Can not obtain the Hardware address of network device ").append(
                        inetdevice);
                throw rimpexc;
            }

            char macBuffer[17];
            sprintf(macBuffer, "%02x:%02x:%02x:%02x:%02x:%02x", (unsigned char) item->ifr_hwaddr.sa_data[0],
                    (unsigned char) item->ifr_hwaddr.sa_data[1], (unsigned char) item->ifr_hwaddr.sa_data[2],
                    (unsigned char) item->ifr_hwaddr.sa_data[3], (unsigned char) item->ifr_hwaddr.sa_data[4],
                    (unsigned char) item->ifr_hwaddr.sa_data[5]);

            NetInterface netiface;
            netiface.name = inetdevice;
            // XXX netiface.address = inetip;
            netiface.physicalAddress = string(macBuffer);
            ifaces.push_back(netiface);

        }// link up and not loopback

    }

    close(sck);

    return ifaces;
}

bool checkRepository(const string& repository)
{
    // check ''repository'' exist and can be r/w
    if (access(repository.c_str(), F_OK | R_OK | W_OK) == -1)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "''repository'' [%s] do not exist or can not be r/w", repository.c_str());

        return false;
    } // ''repository'' exist


    // check for the ''REPOSITORY_MARK'' file on the ''repository''
    string repositoryFileMark(repository);
    repositoryFileMark = repositoryFileMark.append(REPOSITORY_MARK);

    if (access(repositoryFileMark.c_str(), F_OK) == -1)
    {
        LOG("[ERROR] [RIMP] Initialization fails :\n"
                "''repository'' [%s] do not contains the abiquo repository file mark [%s]",
                repository.c_str(), REPOSITORY_MARK.c_str());

        return false;
    }// ''repository'' file mark exist

    return true;
}

string createLinkFormClonedToLocal(const string& localrepo_ImagePath, const string& datastore_ImagePath,
        const string& imageUid)
{
    // TODO assert both paths exist

    int err;
    string error;

    /**
     * datastore/uid.source -> local
     * */
    string linkToLocal(datastore_ImagePath);
    linkToLocal = linkToLocal.append(ORIGINAL);

    err = symlink(localrepo_ImagePath.c_str(), linkToLocal.c_str());

    // TODO check ''File Exist''
    //if (err == -1) XXX link can exist if the deploy fails
    //{
    //    error = string("Can not create ''symlink'' from datastore:");
    //    error = error.append(linkToLocal).append("\nCaused by :").append(strerror(errno));
    //    return error;
    //}

    /**
     * local./links/uid -> destination/uid
     * */
    string linksToClonedPath(localrepo_ImagePath);
    linksToClonedPath = linksToClonedPath.append(LINKS);

    // check if the ''.links'' folder exist
    if (access(linksToClonedPath.c_str(), F_OK) == -1)
    {
        // Read, write, and execute by owner and group.
        int err = mkdir(linksToClonedPath.c_str(), S_IRWXU | S_IRWXG);

        if (err == -1)
        {
            // TODO delete ''linkToLocal'' symlink.
            error = string("Can not create folder:");
            error = error.append(linksToClonedPath).append("\nCaused by :").append(strerror(errno));
            return error;
        }
    }

    string linkToClonedImage(linksToClonedPath);
    linkToClonedImage = linkToClonedImage.append(imageUid);

    err = symlink(datastore_ImagePath.c_str(), linkToClonedImage.c_str());

    //if (err == -1) XXX link can exist if the deploy fails
    //{
    //    // TODO delete ''linkToLocal'' symlink.
    //    error = string("Can not create ''symlink'':");
    //    error = error.append(linkToClonedImage).append("\nCaused by :").append(strerror(errno));
    //    return error;
    //}

    return string();
}

string followLinkToLocalRepository(const string& datastore_ImagePath)
{
    string linkToLocal(datastore_ImagePath);
    linkToLocal = linkToLocal.append(ORIGINAL);

    char* filePath = realpath(linkToLocal.c_str(), NULL); //canonicalize_file_name(linkToLocal);

    // TODO if filePath == NULL check errno
    string filePathStr(filePath);

    remove(linkToLocal.c_str());

    return filePathStr;
}

extern int alphasort();
int getNumItemsOnDir(string dir)
{
    // using : http://www.programatium.com/manuales/c/19.htm
    struct dirent **files;
    return scandir(dir.c_str(), &files, 0, alphasort);
}

string deleteLinkToClonedAndCheckUsedLocal(const string& localrepo_ImagePath, const string& imageUid)
{
    string linksToClonedPath(localrepo_ImagePath);
    linksToClonedPath = linksToClonedPath.append(LINKS);

    string linkToClonedImage(linksToClonedPath);
    linkToClonedImage = linkToClonedImage.append(imageUid);

    int err;
    string error;

    err = remove(linkToClonedImage.c_str());
    if (err == -1)
    {
        error = string("Can not remove:");
        error = error.append(linkToClonedImage).append("\nCaused by :").append(strerror(errno));
        return error;
    }

    int nCloned = getNumItemsOnDir(linksToClonedPath);
    if (nCloned <= 2)
    {
        //debug("[RIMP] no more cloned images for src image [%s] so delete it ", localImagePath);

        err = rmdir(linksToClonedPath.c_str());
        if (err == -1)
        {
            error = string("Can not remove:");
            error = error.append(linksToClonedPath).append("\nCaused by :").append(strerror(errno));
            return error;
        }

        err = remove(localrepo_ImagePath.c_str());
        if (err == -1)
        {
            error = string("Can not remove:");
            error = error.append(localrepo_ImagePath).append("\nCaused by :").append(strerror(errno));
            return error;
        }
    }// no more cloned
    else
    {
        // debug("[RIMP] Still [%d] cloned images", nCloned);
    }

    return string();
}

/******************************************************************************
 * ***************************************************************************/

unsigned long int getFreeSpaceOn(const string& dir)
{
    struct statvfs sbuf;

    if (statvfs(dir.c_str(), &sbuf) < 0)
    {
        //error("[RIMP] Can not get the free space on [%s]", dir);
        return -1;
    }

    unsigned long int s = sbuf.f_bavail;
    long bs = sbuf.f_bsize;
    unsigned long int div = s / 1024l;
    unsigned int mod = s % 1024l;

    unsigned long int freeSpace = (div * bs) + ((mod * bs) / 1024l);

    return freeSpace;
}

unsigned long int getTotalSpaceOn(const string& dir)
{
    struct statvfs sbuf;

    if (statvfs(dir.c_str(), &sbuf) < 0)
    {
        //error("[RIMP] Can not get the total space on [%s]", dir);
        return -1;
    }

    unsigned long int s = sbuf.f_blocks;
    long bs = sbuf.f_bsize;
    unsigned long int div = s / 1024l;
    unsigned int mod = s % 1024l;

    unsigned long int totalSpace = (div * bs) + ((mod * bs) / 1024l);

    return totalSpace;
}

unsigned long int getFileSize(const string& filename)
{
    path p (filename, native);

    return file_size(p) / 1024;
}
//    struct stat results;
//    unsigned long int size;
//    if (stat(filename.c_str(), &results) == 0)
//    {
//        size = results.st_size / 1024l;
//    } else
//    {
//error("[RIMP] Can not get the size of [%s]", filename);
//        size = -1;
//    }
//    return size;


// target always ends with '/'
string createTargetDirectory(const string& target)
{
    string error = string();

    if (target.empty() || access(target.c_str(), F_OK) == 0)
    {
        return error;
    } else
    {
        int iPath = target.substr(0, target.length() - 1).find_last_of('/');
        string parentDir = target.substr(0, iPath + 1);

        error = createTargetDirectory(parentDir);
        if (!error.empty())
        {
            return error;
        }

        int err = mkdir(target.c_str(), S_IRWXU | S_IRWXG);
        if (err == -1)
        {
            error = error.append("Can not create folder at :").append(target);
            error = error.append("\nCaused by: ").append(strerror(errno));
            return error;
        }
    }

    return error;
}

string fileCopy(const string& source, const string& target)
{
    string targetCopy(target);

    int iPath = targetCopy.find_last_of('/');
    string parentDir = targetCopy.substr(0, iPath + 1);
    string error = createTargetDirectory(parentDir);

    if (!error.empty())
    {
        return error;
    }

    try
    {
    	path psource (source.c_str(), native);
    	path ptarget (target.c_str(), native);

        copy_file(psource, ptarget);

    } catch (exception& e)
    {
        string error(e.what());
        return error;
    }
    //std::ifstream ifs("input.txt", std::ios::binary);
    //std::ofstream ofs("output.txt", std::ios::binary);
    //ofs << ifs.rdbuf();

    return string();
}

