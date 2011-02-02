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
package net.undf.abicloud.view.user.components.user.combocheckbox {
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.controls.ComboBox;
	import mx.core.ClassFactory;
	import mx.events.FlexEvent;
	import mx.resources.ResourceManager;
	
	[Event(name="addItem", type="flash.events.Event")]
	
    public class ComboCheck extends ComboBox {
    	private var _selectedItems:ArrayCollection;
    	
    	[Bindable("change")]
    	[Bindable("valueCommit")]
    	[Bindable("collectionChange")]
    	
    	public function set selectedItems(value:ArrayCollection):void {
    		_selectedItems=value;
    	}
        
    	public function get selectedItems():ArrayCollection {
    		return _selectedItems;
    	}
    	
        public function ComboCheck() {
            super();
            addEventListener("comboChecked", onComboChecked);
            addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
            var render:ClassFactory = new ClassFactory(ComboCheckItemRenderer);
		    itemRenderer=render;
		    var myDropDownFactory:ClassFactory = new ClassFactory(ComboCheckDropDownFactory);
        	super.dropdownFactory=myDropDownFactory;
        	selectedItems=new ArrayCollection();
        }
         
        private function onCreationComplete(event:Event):void {
        	dropdown.addEventListener(FlexEvent.CREATION_COMPLETE, onDropDownComplete);
        }
        
        private function onDropDownComplete(event:Event):void {
        	trace ("dropdown complete!");
        }
        
        private function onComboChecked(event:ComboCheckEvent):void {
        	var obj:Object = event.obj;
        	var index:int=selectedItems.getItemIndex(obj);
        	if (index==-1) {
        		selectedItems.addItem(obj);
        	} else {
        		selectedItems.removeItemAt(index);
        	}  
        	text = ResourceManager.getInstance().getString('Configuration','LABEL_VIRTUAL_DATACENTERS');  	
        	dispatchEvent(new Event("valueCommit"));
        	dispatchEvent(new Event("addItem"));
        }
    }
}