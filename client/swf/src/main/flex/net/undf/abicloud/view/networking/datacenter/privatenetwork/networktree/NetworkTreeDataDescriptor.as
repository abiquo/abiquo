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

package net.undf.abicloud.view.networking.datacenter.privatenetwork.networktree
{
    import mx.collections.ArrayCollection;
    import mx.collections.ICollectionView;
    import mx.controls.treeClasses.ITreeDataDescriptor;
    
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    public class NetworkTreeDataDescriptor implements ITreeDataDescriptor
    {
        public function NetworkTreeDataDescriptor()
        {
            super();
        }

        public function getChildren(node:Object, model:Object = null):ICollectionView
        {
            if (node is NetworkTreeItem)
            {
                if (NetworkTreeItem(node).virtualDatacenters == null)
                {
                    //We need to request the Private Networks for this node
                    NetworkTreeItem(node).requestFunction(node);

                    //Until we get them, just set an empty array
                    NetworkTreeItem(node).virtualDatacenters = new ArrayCollection();
                }

                //We already have the Private networks for this node
                return NetworkTreeItem(node).virtualDatacenters;
            }
            else if (node is VirtualDataCenter)
            {
                if(VirtualDataCenter(node).network){
	                return VirtualDataCenter(node).network.networks;
                }else{
                	return new ArrayCollection();
                }
            }
            else
                return null;
        }

        public function hasChildren(node:Object, model:Object = null):Boolean
        {
            if (node is NetworkTreeItem || node is VirtualDataCenter)
                return true;
            else
                return false;
        }

        public function isBranch(node:Object, model:Object = null):Boolean
        {
            if (node is NetworkTreeItem || node is VirtualDataCenter)
                return true;
            else
                return false;
        }

        public function getData(node:Object, model:Object = null):Object
        {
            return node;
        }

        public function addChildAt(parent:Object, newChild:Object, index:int, model:Object = null):Boolean
        {
            //Nodes cant be moved
            return false;
        }

        public function removeChildAt(parent:Object, child:Object, index:int, model:Object = null):Boolean
        {
            //Nodes cant be moved
            return false;
        }
    }
}