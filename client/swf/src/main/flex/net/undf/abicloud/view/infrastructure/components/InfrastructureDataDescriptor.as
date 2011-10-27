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

package net.undf.abicloud.view.infrastructure.components
{
    /**
     * Data descriptor for the racks tree. Describes how racks, physicals and virtual machines are connected
     * to be able to draw them in a CustomTree component
     **/


    import mx.collections.ArrayCollection;
    import mx.collections.ICollectionView;
    import mx.controls.Alert;
    import mx.core.Application;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;
    import mx.utils.ObjectUtil;

    import net.undf.abicloud.events.InfrastructureEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.utils.customtree.ICustomTreeDataDescriptor;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.infrastructure.HyperVisor;
    import net.undf.abicloud.vo.infrastructure.InfrastructureElement;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachine;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachineCreation;
    import net.undf.abicloud.vo.infrastructure.Rack;
    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.infrastructure.VirtualMachine;

    public class InfrastructureDataDescriptor implements ICustomTreeDataDescriptor
    {

        [ResourceBundle("Infrastructure")]
        private var rb:ResourceBundle;

        [ResourceBundle("Common")]
        private var rb2:ResourceBundle;

        private var _openedBranches:ArrayCollection;

        public function InfrastructureDataDescriptor()
        {
            this._openedBranches = new ArrayCollection();
        }

        public function isCopyAllowed(parent:Object, newChild:Object):Boolean
        {
            return false;

            //TEMPORALY DISABLED DUE NETWORKING INCOMPATIBILITIES
        /*
           if(parent is Rack && newChild is PhysicalMachine)
           return true;
           else if(parent is PhysicalMachine && newChild is VirtualMachine)
           return true;
           else
         return false;*/
        }

        public function copyChild(parent:Object, newChild:Object):Boolean
        {
            var infrastructureEvent:InfrastructureEvent;

            if (parent is Rack && newChild is PhysicalMachine)
            {
                var parentRack:Rack = parent as Rack;
                var childPhysicalMachine:PhysicalMachine = newChild as PhysicalMachine;
                var childPhysicalMachineHypervisors:Array = AbiCloudModel.getInstance().infrastructureManager.getHyperVisorsByPhysicalMachine(childPhysicalMachine);
                var copiedPhysicalMachine:PhysicalMachine;
                var copiedHypervisors:Array;

                //Creating the copy
                copiedPhysicalMachine = ObjectUtil.copy(childPhysicalMachine) as PhysicalMachine;
                copiedPhysicalMachine.name = copiedPhysicalMachine.name + " (Clon)";
                if (copiedPhysicalMachine.name.length > 29)
                    //We have to check if PhysicalMachine's name is not too long...
                    copiedPhysicalMachine.name = copiedPhysicalMachine.name.substr(0,
                                                                                   29);

                copiedPhysicalMachine.assignedTo = parent as InfrastructureElement;

                //Physical Machine state is not cloned
                copiedPhysicalMachine.cpuUsed = 0;
                copiedPhysicalMachine.ramUsed = 0;
                copiedPhysicalMachine.hdUsed = 0;

                //Cloning physical machines hypervisors
                copiedHypervisors = new Array();
                var hypervisor:HyperVisor;
                for (var i:int = 0; i < childPhysicalMachineHypervisors.length; i++)
                {
                    hypervisor = ObjectUtil.copy(childPhysicalMachineHypervisors[i]) as HyperVisor;

                    //Some hypervisors information is not cloned
                    hypervisor.id = 0;
                    hypervisor.ip = "";

                    copiedHypervisors.push(hypervisor);
                }

                //Saving the copied PhysicalMachine
                var physicalMachineCreation:PhysicalMachineCreation = new PhysicalMachineCreation();
                physicalMachineCreation.physicalMachine = copiedPhysicalMachine;
                physicalMachineCreation.hypervisors = new ArrayCollection(copiedHypervisors);
                infrastructureEvent = new InfrastructureEvent(InfrastructureEvent.CREATE_PHYSICALMACHINE);
                infrastructureEvent.physicalMachineCreation = physicalMachineCreation;
                Application.application.dispatchEvent(infrastructureEvent);

                return true;
            }
            else if (parent is PhysicalMachine && newChild is VirtualMachine)
            {
                var parentPhysicalMachine:PhysicalMachine = parent as PhysicalMachine;
                var childVirtualMachine:VirtualMachine = newChild as VirtualMachine;
                var copiedVirtualMachine:VirtualMachine = new VirtualMachine();

                //Getting the HyperVisors assigned to this PhysicalMachine
                var hyperVisors:Array = AbiCloudModel.getInstance().infrastructureManager.getHyperVisorsByPhysicalMachine(parentPhysicalMachine);
                if (hyperVisors.length == 0)
                {
                    //There are no hypervisors assigned to this Physical Machine. Copy operation is not possible	   
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("Infrastructure",
                                                                                    "ALERT_CLONE_VIRTUALMACHINE_HEADER"),
                                            ResourceManager.getInstance().getString("Infrastructure",
                                                                                    "ALERT_MOVE_VIRTUALMACHINE_TEXT"),
                                            Alert.OK);

                    return false;
                }
                else
                {
                    //Making the copy of the virtual machine
                    //State and UUID fields will be created by server
                    copiedVirtualMachine.name = childVirtualMachine.name + " (Clon)";
                    if (copiedVirtualMachine.name.length > 99)
                        copiedVirtualMachine.name = copiedVirtualMachine.name.substr(0,
                                                                                     99);
                    copiedVirtualMachine.virtualImage = childVirtualMachine.virtualImage;
                    copiedVirtualMachine.description = childVirtualMachine.description;
                    copiedVirtualMachine.ram = childVirtualMachine.ram;
                    copiedVirtualMachine.cpu = childVirtualMachine.cpu;
                    copiedVirtualMachine.hd = childVirtualMachine.hd;
                    copiedVirtualMachine.highDisponibility = copiedVirtualMachine.highDisponibility;

                    //Fact: assign the Virtual Machine to the first HyperVisor in the list
                    copiedVirtualMachine.assignedTo = hyperVisors[0];

                    //Anouncing the creation of the virtual machine's copy
                    infrastructureEvent = new InfrastructureEvent(InfrastructureEvent.CREATE_VIRTUALMACHINE);
                    infrastructureEvent.infrastructureElement = copiedVirtualMachine;
                    Application.application.dispatchEvent(infrastructureEvent);

                    return true;
                }
            }
            else
                return false;
        }

        public function isMoveAllowed(parent:Object, newChild:Object):Boolean
        {
            return false;

            //TEMPORALY DISABLED DUE NETWORKING INCOMPATIBILITIES
        /*
           if(parent is Rack && newChild is PhysicalMachine)
           {
           var physicalMachinesByRack:ArrayCollection = new ArrayCollection(AbiCloudModel.getInstance().infrastructureManager.getPhysicalMachinesByRack(parent as Rack));
           if(! physicalMachinesByRack.contains(newChild))
           return true;
           else
           return false;
           }
           else if(parent is PhysicalMachine && newChild is VirtualMachine)
           {
           var virtualMachinesByPM:ArrayCollection = new ArrayCollection(AbiCloudModel.getInstance().infrastructureManager.getVirtualMachinesByPhysicalMachine(parent as PhysicalMachine));
           if(! virtualMachinesByPM.contains(newChild))
           return true;
           else
           return false;
           }
           else
         return false;*/
        }

        public function moveChild(parent:Object, newChild:Object):Boolean
        {

            var elementParent:InfrastructureElement;
            var elementChild:InfrastructureElement;
            var infrastructureEvent:InfrastructureEvent;

            if (parent is Rack && newChild is PhysicalMachine)
            {
                elementParent = parent as Rack;

                elementChild = ObjectUtil.copy(newChild) as PhysicalMachine;
                elementChild.assignedTo = elementParent;

                var physicalMachineCreation:PhysicalMachineCreation = new PhysicalMachineCreation();
                physicalMachineCreation.physicalMachine = elementChild as PhysicalMachine;
                physicalMachineCreation.hypervisors = new ArrayCollection();
                infrastructureEvent = new InfrastructureEvent(InfrastructureEvent.EDIT_PHYSICALMACHINE);
                infrastructureEvent.physicalMachineCreation = physicalMachineCreation;
                Application.application.dispatchEvent(infrastructureEvent);

                return true;
            }

            else if (parent is PhysicalMachine && newChild is VirtualMachine)
            {
                elementParent = parent as PhysicalMachine;
                elementChild = ObjectUtil.copy(newChild) as VirtualMachine;

                //Getting the HyperVisors assigned to this PhysicalMachine
                var hyperVisors:Array = AbiCloudModel.getInstance().infrastructureManager.getHyperVisorsByPhysicalMachine(elementParent as PhysicalMachine);
                if (hyperVisors.length == 0)
                {
                    //There are no hypervisors assigned to this Physical Machine. Move operation is not possible
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("Infrastructure",
                                                                                    "ALERT_MOVE_VIRTUALMACHINE_HEADER"),
                                            ResourceManager.getInstance().getString("Infrastructure",
                                                                                    "ALERT_MOVE_VIRTUALMACHINE_TEXT"),
                                            Alert.OK);

                    return false;
                }
                else
                {
                    //Fact: assign the Virtual Machine to the first HyperVisor in the list
                    elementChild.assignedTo = hyperVisors[0] as HyperVisor;

                    infrastructureEvent = new InfrastructureEvent(InfrastructureEvent.EDIT_VIRTUALMACHINE);
                    infrastructureEvent.infrastructureElement = elementChild;
                    if (VirtualMachine(elementChild).state.description != State.LOCKED)
                        Application.application.dispatchEvent(infrastructureEvent);

                    return true;
                }
            }
            else
                return false;
        }


        public function getChildren(node:Object, model:Object = null):ICollectionView
        {
            if (node is Rack)
            {
                var rack:Rack = node as Rack;
                return new ArrayCollection(AbiCloudModel.getInstance().infrastructureManager.getPhysicalMachinesByRack(rack));
            }
            else
                return null;
        }

        public function hasChildren(node:Object, model:Object = null):Boolean
        {
            if (node is Rack)
            {
                var rack:Rack = node as Rack;
                var rackPMAssigned:Array = AbiCloudModel.getInstance().infrastructureManager.getPhysicalMachinesByRack(rack);
                return (rackPMAssigned.length > 0);
            }
            else
                return false;
        }

        public function isBranch(node:Object, model:Object = null):Boolean
        {
            return node is Rack;
        }


        public function getNodeLevel(node:Object):int
        {
            if (node is Rack)
                return 0;
            else
                return 1;
        }

        public function isNodeDraggable(node:Object):Boolean
        {
            return false;
            //return node is PhysicalMachine;
        }

        public function isBranchOpened(node:Object):Boolean
        {
            if (node is Rack)
            {
                var index:int = this._openedBranches.getItemIndex(Rack(node).id);
                return index > -1;
            }
            else
                return false;
        }

        public function markBranchAsOpened(node:Object):void
        {
            if (node is Rack)
            {
                var index:int = this._openedBranches.getItemIndex(Rack(node).id);
                if (index == -1)
                    this._openedBranches.addItem(Rack(node).id);
            }
        }

        public function unmarkBranchAsOpened(node:Object):void
        {
            if (node is Rack)
            {
                var index:int = this._openedBranches.getItemIndex(Rack(node).id);
                if (index > -1)
                    this._openedBranches.removeItemAt(index);
            }
        }

        public function cleanMarks():void
        {
            this._openedBranches.removeAll();
        }
    }
}