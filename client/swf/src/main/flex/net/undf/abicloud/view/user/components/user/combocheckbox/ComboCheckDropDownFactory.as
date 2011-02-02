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
	import flash.events.MouseEvent;
	
	import mx.controls.List;
	import mx.controls.listClasses.*;
	
	[Event(name="comboChecked", type="net.undf.abicloud.view.user.components.user.combocheckbox.ComboCheckEvent")]

    public class ComboCheckDropDownFactory extends List {

        private var index:int=0;
        public function ComboCheckDropDownFactory(): void {
            addEventListener("comboChecked", onComboChecked);
        }

        override protected function mouseEventToItemRenderer(event:MouseEvent):IListItemRenderer {
            var row:IListItemRenderer = super.mouseEventToItemRenderer(event);
            if (row!=null) {
            	index=itemRendererToIndex(row);
            }
            return null;
        }
	    private function onComboChecked (event:Event):void {
	    	var myComboCheckEvent:ComboCheckEvent=new ComboCheckEvent(ComboCheckEvent.COMBO_CHECKED);
	    	myComboCheckEvent.obj=ComboCheckEvent(event).obj;
	        owner.dispatchEvent(myComboCheckEvent);
	    }
    }
}