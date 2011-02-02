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

#ifndef NODE_CONFIG_H
#define NODE_CONFIG_H

#include <Service.h>

#include <string>

using namespace std;

#define ISCSI_DEFAULT_INITIATOR_NAME_FILE "/etc/iscsi/initiatorname.iscsi"

class NodeConfig : public Service
{
    protected:
        string iscsiInitiatorNameFile;

    public:
        NodeConfig();
        ~NodeConfig();

        virtual bool initialize(dictionary * configuration);
        virtual bool cleanup();
        virtual bool start();
        virtual bool stop();

        void getInitiatorIQN(string& iqn);
};

#endif
