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

/*

   CustomTree class, to display data in a Tree format.

   A CustomTree accepts any type of data in its customTreeDataProvider, since all data in the customTreeDataProvider will be
   encapsulated in a CustomTreeNode, and displayed using a CustomTreeNodeRenderer.

   An ITreeDataDescriptor describes how to build the Tree that represents the data present in the customTreeDataProvider

   For Drag & Drop functionality, the CustomTreeNodeRenderers are the responsibles, using the ICustomTreeDataDescriptor.
   Default Drag & Drop operations, inherited from the List component, are ignored

 */


package net.undf.abicloud.utils.customtree
{
    import flash.events.Event;
    
    import mx.binding.utils.BindingUtils;
    import mx.collections.ArrayCollection;
    import mx.controls.List;
    import mx.core.ClassFactory;
    import mx.events.DragEvent;
    import mx.events.FlexEvent;
    import mx.events.ItemClickEvent;
    
    import net.undf.abicloud.events.InfrastructureEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.infrastructure.InfrastructureElement;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachine;
    import net.undf.abicloud.vo.infrastructure.Rack;
    import net.undf.abicloud.vo.infrastructure.UcsRack;

    /**
     *  CustomTree class, to display data in a Tree format.

       A CustomTree accepts any type of data in its customTreeDataProvider, since all data in the customTreeDataProvider will be
       encapsulated in a CustomTreeNode, and displayed using a CustomTreeNodeRenderer.

       An ITreeDataDescriptor describes how to build the Tree that represents the data present in the customTreeDataProvider

       For Drag & Drop functionality, the CustomTreeNodeRenderers are the responsibles, using the ICustomTreeDataDescriptor.
       Default Drag & Drop operations, inherited from the List component, are ignored
     * @author Oliver
     *
     */
    public class CustomTree extends List
    {

        //The data provider for the inner List of this CustomTree. It will contain only CustomTreeNodes
        protected var _innerListDataProvider:ArrayCollection;
        
        [Bindable]
        private var _selectedElement:InfrastructureElement;

		
		//Associated to the filter text
		[Bindable]
		public var searchFilter:String;
		
        /**
         * Constructor
         **/
        public function CustomTree()
        {
            super();

            addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);

        }


        /**
         * Creation Complete event handler
         **/
        protected function creationCompleteHandler(flexEvent:FlexEvent):void
        {
            //Preparing the inner List
            super.itemRenderer = this._customTreeNodeRenderer;

            //Listening for changes in the data provider
            BindingUtils.bindSetter(buildTree, this, "customTreeDataProvider");

            //Listening for user interaction with CustomTree nodes
            addEventListener("customTreeNodeClick", customTreeNodeClickHandler);
            
            if(!AbiCloudModel.getInstance().infrastructureManager.hasEventListener(InfrastructureEvent.PHYSICALMACHINE_BY_RACK_RETRIEVED)){
	            AbiCloudModel.getInstance().infrastructureManager.addEventListener(InfrastructureEvent.PHYSICALMACHINE_BY_RACK_RETRIEVED,updatePhysicalMachineList);
           		this._isPreviousRackComplete = true;
            }
            
            
        }

        /**
         * The Node renderer class used for this CustomTree
         * CustomTreeNodeRenderer by default
         */
        private var _customTreeNodeRenderer:ClassFactory = new ClassFactory(CustomTreeNodeRenderer);

        public function set customTreeNodeRenderer(value:Class):void
        {
            this._customTreeNodeRenderer = new ClassFactory(value);
        }

        /**
         * The data descriptor for this CustomTree
         **/
        protected var _customTreeDataDescriptor:ICustomTreeDataDescriptor;

        public function set customTreeDataDescriptor(descriptor:ICustomTreeDataDescriptor):void
        {
            this._customTreeDataDescriptor = descriptor;

        }
        
        /**
         * We need to know the selecte element to select it when the tree is rebuilt
         * 
         */
         
         public function get selectedElement():InfrastructureElement{
         	return this._selectedElement
         }
         
         public function set selectedElement(value:InfrastructureElement):void{
         	this._selectedElement = value;
         }


        /**
         * The data provider for this CustomTree. Must be an ArrayCollection
         **/
        private var _customTreeDataProvider:ArrayCollection;

        [Bindable(event="customTreeDataProviderChange")]
        public function get customTreeDataProvider():Object
        {
            return this._customTreeDataProvider;
        }

        public function set customTreeDataProvider(arrayCollection:Object):void
        {
            //We only accept ArrayCollection as data provider
            if (arrayCollection is ArrayCollection)
            {
                this._customTreeDataProvider = arrayCollection as ArrayCollection;
                dispatchEvent(new Event("customTreeDataProviderChange"));
            }
        }
        
        private function elementMatch(element:InfrastructureElement):Boolean{
        	if(selectedElement){
        		if(selectedElement is PhysicalMachine){
		        	if(element.id == selectedElement.id){
		        		return true;
		        	}        		
        		}
        	}
        	return false;
        }


        /**
         * We override the setter dataProvider from the List component, to control external access to it
         **/
        override public function set dataProvider(value:Object):void
        {
            if (value == this._innerListDataProvider)
                super.dataProvider = value;
        }


        /**
         * We override the methods selectedItem and selectedItems from the inner List, to not return CustomTreeNode, but
         * the item that it contains
         **/
        override public function get selectedItem():Object
        {
            var listSelectedItem:Object = super.selectedItem;

            if (listSelectedItem != null)
            {
                return CustomTreeNode(listSelectedItem).item;
            }
            else
                return null;
        }

        override public function get selectedItems():Array
        {
            var listSelectedItems:Array = super.selectedItems;

            if (listSelectedItems.length > 0)
            {
                var i:int;
                var length:int = listSelectedItems.length;
                for (i = 0; i < length; i++)
                {
                    listSelectedItems[i] = CustomTreeNode(listSelectedItems[i]).item;
                }

                return listSelectedItems;
            }
            else
                return listSelectedItems;
        }

		private var _isPreviousRackComplete:Boolean = true;

        /**
         * Builds this CustomTree, using the inner List to draw the nodes
         **/
        private function buildTree(arrayCollection:ArrayCollection):void
        {
            if (arrayCollection != null)
            {
                this._innerListDataProvider = new ArrayCollection();

                var length:int = arrayCollection.length;
                var i:int;
                var customTreeNode:CustomTreeNode;
                var object:Object;
                for (i = 0; i < length; i++)
                {

                    object = arrayCollection.getItemAt(i);
                    customTreeNode = new CustomTreeNode(object, object[super.labelField],
                                                        this._customTreeDataDescriptor,
                                                        false);
                    this._innerListDataProvider.addItem(customTreeNode);

                    //Check if this CustomTree is set to save the state and, if so, check if this branch was opened before
                    if (this._saveState && this._customTreeDataDescriptor.isBranchOpened(customTreeNode.item))
                    {
                        
                        customTreeNode.isBranchOpened = true;
                        	            
			            //In case of refreshing the view (create/delete rack, physical machine, etc..)
			            //we need to retrieve PM in each opened rack, so we wait until previous rack is complete
			            openBranch(customTreeNode);
			            
	                    
                        
                        
                        //Checking if this brach that was opened before, still contains any child
                        /* var objects:ArrayCollection = this._customTreeDataDescriptor.getChildren(customTreeNode.item) as ArrayCollection;
                        if (objects.length > 0)
                        {
                            customTreeNode.isBranchOpened = true;
                            openBranch(customTreeNode);
                        }
                        else
                        {
                            //This branch no longer contains childs, so it is no necessary to open it
                            this._customTreeDataDescriptor.unmarkBranchAsOpened(customTreeNode.item);
                        } */
                    }
                }

                //Setting the inner List's dataProvider
                dataProvider = this._innerListDataProvider;
            }
        }

        /**
         * Handler to control when user clicks in a CustomTreeNode
         **/
        private function customTreeNodeClickHandler(itemClickEvent:ItemClickEvent):void
        {
            //Retrieving the CustomTreeNode that user has clicked
            var customTreeNodeClicked:CustomTreeNode = itemClickEvent.item as CustomTreeNode;

            if (this._customTreeDataDescriptor.isBranch(customTreeNodeClicked.item))
            {
                if (customTreeNodeClicked.isBranchOpened)
                {
                    //If the branch is opened, we closed it and hide its children
                    customTreeNodeClicked.isBranchOpened = false;
                    closeBranch(customTreeNodeClicked);

                    //Removing this branch from the list of opened branches
                    if (this._saveState)
                    {
                        this._customTreeDataDescriptor.unmarkBranchAsOpened(customTreeNodeClicked.item);
                    }
                }
                else
                {
                    //If the branch is closed, we open it and show its children
                    customTreeNodeClicked.isBranchOpened = true;
                    openBranch(customTreeNodeClicked);

                    //Saving the state of this branch
                    if (this._saveState)
                        this._customTreeDataDescriptor.markBranchAsOpened(customTreeNodeClicked.item);
                }
            }
        }

        /**
         * Shows a opened branch's children in the inner list
         */
        protected function openBranch(branch:CustomTreeNode):void
        {
            
            //In case of
            if(branch.item is Rack){
            
            	//we open the taag and retrieve the list of physical machine
	            var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.GET_PHYSICALMACHINE_BY_RACK);
	            event.dataCenter = Rack(branch.item).dataCenter;
	            event.branch = branch;
	            event.rackId = branch.item.id;
	            if(searchFilter.length > 0){
	            	event.filters = searchFilter;
	            }else{
		            event.filters = null;
	            }

	            dispatchEvent(event);	

            }else{
            	
            	branchPosition = this._innerListDataProvider.getItemIndex(branch);
	            
	            var objects:ArrayCollection = this._customTreeDataDescriptor.getChildren(branch.item) as ArrayCollection;
	
	            //Building CustomTreeNodes for the children, and putting them in the right position in the inner List
	            var object:Object;
	            var customTreeNodeChildren:ArrayCollection = new ArrayCollection();
	            var customTreeNodeChild:CustomTreeNode;
	            var length:int = objects.length;
	            var i:int;
	            for (i = 0; i < length; i++)
	            {
	                object = objects.getItemAt(i);
	                customTreeNodeChild = new CustomTreeNode(object, object[super.labelField],
	                                                         this._customTreeDataDescriptor,
	                                                         false);
	                this._innerListDataProvider.addItemAt(customTreeNodeChild, branchPosition + 1 + i);
	            }
            	
            }      
        }
        
        public var branchPosition:int;
        
        private function updatePhysicalMachineList(event:InfrastructureEvent):void{
        	//to avoid a bad refresh when a user retrieve the list of physical machine in the allocation engine rules
        	if(event.branch){
	        	var listOfMachines:ArrayCollection = event.physicalMachineByRack;
	        	var customTreeNodeChild:CustomTreeNode;
	        	var physicalMachine:PhysicalMachine;
	        	var position:int = this._innerListDataProvider.getItemIndex(event.branch);
	        	for (var i:int = 0; i < listOfMachines.length; i++)
	            {
	                physicalMachine = listOfMachines.getItemAt(i) as PhysicalMachine;
	                //UPDATE ASSIGNEDto ELEMENT
	                physicalMachine.assignedTo = event.branch.item as InfrastructureElement;
	                if(physicalMachine.assignedTo is UcsRack){
                        customTreeNodeChild = new CustomTreeNode(physicalMachine, physicalMachine.description,
                                                                 this._customTreeDataDescriptor,
                                                                 false);
	                }else{
		                customTreeNodeChild = new CustomTreeNode(physicalMachine, physicalMachine[super.labelField],
		                                                         this._customTreeDataDescriptor,
		                                                         false);
	                }
	                this._innerListDataProvider.addItemAt(customTreeNodeChild, position + 1 + i);
		            if(elementMatch(physicalMachine)){
		            	this.selectedIndex = position + 1 + i;
		            	//to inform the selectedItem has been found
		        		dispatchEvent(new Event("selectedItemUpdated"));
		            }
	            } 
	            this._isPreviousRackComplete = true;
        	}        	
        }

        protected function closeBranch(branch:CustomTreeNode):void
        {
            //Getting the branch position
            var rackBranchPosition:int = this._innerListDataProvider.getItemIndex(branch);
            
            if(branch.item is Rack){
	            //we remove all machines form the position to next rack or until the end of the collection
	            while(rackBranchPosition + 1 != this._innerListDataProvider.length && CustomTreeNode(this._innerListDataProvider.getItemAt(rackBranchPosition + 1)).item is PhysicalMachine ){
	            	this._innerListDataProvider.removeItemAt(rackBranchPosition + 1);
	            }
            }else{
	            //Getting the num of children that branch has
	            var numChildren:int = ArrayCollection(this._customTreeDataDescriptor.getChildren(branch.item)).length;
	            var i:int
	            //Removing the children from the inner List's dataProvider
	            for (i = 0; i < numChildren; i++)
	            {
	                this._innerListDataProvider.removeItemAt(rackBranchPosition + 1);
	            } 
            }

        }


        /**
         * This flag indicates that this CustomTree will save the branches state. If this
         * CustomTree's data provider is refreshed, branches previously opened will remain opened.
         * The CustomTreeNode.item property will be used to identify the branch, since new CustomTreeNodes
         * are created when the data provider changes
         */
        private var _saveState:Boolean = false;

        public function get saveState():Boolean
        {
            return this._saveState;
        }

        public function set saveState(value:Boolean):void
        {
            this._saveState = value;
        }

        public function cleanState():void
        {
            this._customTreeDataDescriptor.cleanMarks();
        }

        ////////////////////////////////////////////////
        //DRAG & DROP Functionality

        //Drag & Drop operations, inherited from the List component, are overrided and ignored

        override protected function dragStartHandler(dragEvent:DragEvent):void
        {
        }

        override protected function dragCompleteHandler(event:DragEvent):void
        {
        }

        override protected function dragDropHandler(event:DragEvent):void
        {
        }

        override protected function dragEnterHandler(event:DragEvent):void
        {
        }

        override protected function dragExitHandler(event:DragEvent):void
        {
        }

        override protected function dragOverHandler(event:DragEvent):void
        {
        }

        override protected function dragScroll():void
        {
        }

    }
}