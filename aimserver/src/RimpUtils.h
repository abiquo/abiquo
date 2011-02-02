/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

#ifndef RIMP_UTILS_H
#define RIMP_UTILS_H

//#include <nfs/nfs.h>
//#include <linux/nfs_mount.h>
#include <sys/types.h>
#include <sys/mount.h>
#include <sys/param.h>
#include <sys/sendfile.h>
#include <sys/stat.h>
#include <sys/statfs.h>
#include <sys/statvfs.h>
#include <sys/vfs.h>
#include <pwd.h>
#include <fts.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <fstab.h>
#include <mntent.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#include <string>
#include <string.h>

#include <Debug.h>

#include <aim_types.h>

#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/fstream.hpp>

// net interfaces
#include <sys/ioctl.h>
#include <net/if.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <getopt.h>
#include <sys/socket.h>
typedef unsigned short u16;
typedef unsigned int u32;
typedef unsigned char u8;
#include <linux/cciss_ioctl.h>
#include <linux/ethtool.h>
#include <linux/sockios.h>

using namespace boost::filesystem;
using namespace std;

/** Identify a file present on the ''repository'' in order to validate as an ''abiquo'' repository. */
const string REPOSITORY_MARK = ".abiquo_repository";

/** Images from the remote repositories are copied there before cloning.*/
const string LOCAL_REPOSITORY = "/opt/localRepository/";

/** Used to create the folder containing the soft links to cloned images. From ''datastore'' to ''LOCAL_REPO''. */
const string LINKS = ".links/";

/** Used to create a soft link to the local image. From ''LOCAL_REPO'' to ''datastore''*/
const string ORIGINAL = ".source";

/**
 * @return true if the configured ''datastore'' path exist and can be read/write.
 * */
bool checkDatastore(const string& datastore);

/**
 * @return true if the configured ''repository'' path is mounted on the host and can be read/write and exist the ''REPOSITORY_MARK''.
 * */
bool checkRepository(const string& repository);

/******************************************************************************
 *                              SOFT LINKS
 * Functions related to how the cloned image and the image on the local
 * repository are pointing among themselves.
 * ***************************************************************************/

/**
 * Create a link two soft links to associate the image on the ''local repository''
 * to the ''datastore''.
 * 1) ''localrepo_ImagePath''+LINKS+''imageUid'' --> ''datastore_ImagePath''
 * 2) ''datastore_ImagePath''+ORIGINAL           --> ''localrepo_ImagePath''
 *
 * @param localrepo_ImagePath, the virtual image path on the ''local repository''.
 * @param datastore_ImagePath, the virtual image path on the ''datastore''.
 * @param imageUid, the UUID of the virtual machine using the virtual image (final segment of the ''datastore_ImagePath'').
 *
 * @return error message or NULL on success.
 * */
string createLinkFormClonedToLocal(const string& localrepo_ImagePath, const string& datastore_ImagePath,
        const string& imageUid);

/**
 * Follow the link of the virtual image on the ''datastore'' to obtain its path on the ''local repository''.
 *
 * Also delete the link from to ''local repository''.
 *
 * @param datastore_ImagePath, the virtual image path on the ''datastore''.
 *
 * @return null if the link do not exist
 * */
string followLinkToLocalRepository(const string& datastore_ImagePath);

/**
 * Deletes the link to ''datastore'' and check if there are more images using ''local repository'' as source, if any also deletes the virtual image from the ''local repository''.
 *
 * @param localrepo_ImagePath, the virtual image path on the ''local repository''.
 * @param imageUid, the UUID of the virtual machine using the virtual image.
 *
 * @return error message or NULL on success.
 * */
string deleteLinkToClonedAndCheckUsedLocal(const string& localrepo_ImagePath, const string& imageUid);

/******************************************************************************
 *                              COLLECTING INFORMATION
 * Functions related to obtain information about the system state.
 * ***************************************************************************/

/**
 * @return the available free space (in KiloBytes) on the provided directory or -1 if do not exist.
 * */
unsigned long int getFreeSpaceOn(const string& dir);

/**
 * @return the total size of the partition (in KiloBytes) of the provided directory or -1 if do not exist.
 * */
unsigned long int getTotalSpaceOn(const string& dir);

/**
 * @return the size of the provided file (in KiloBytes) or -1 if do not exist.
 * */
unsigned long int getFileSize(const string& filename);

/**
 * Copy ''source'' file into the ''target'' path.
 * If ''target'' path exist it returns.
 *
 * @return error message or NULL on success.
 * */
string fileCopy(const string& source, const string& target);

/**
 * TODO TBD
 * */
vector<Datastore> getDatastoresFromMtab();

/**
 * TODO TBD
 * */
vector<NetInterface> getNetInterfacesFromXXX();

#endif
