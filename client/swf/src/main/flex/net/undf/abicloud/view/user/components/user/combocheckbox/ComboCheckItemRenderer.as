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
package net.undf.abicloud.view.user.components.user.combocheckbox{
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import mx.controls.CheckBox;
	import mx.events.FlexEvent;
	
	[Event(name="comboChecked", type="net.undf.abicloud.view.user.components.user.combocheckbox.ComboCheckEvent")]
	
	public class ComboCheckItemRenderer extends CheckBox {
		
		public function ComboCheckItemRenderer() {
			super();
			addEventListener(FlexEvent.CREATION_COMPLETE, updateCheckState);
			addEventListener(FlexEvent.SHOW, updateCheckState);
			addEventListener(MouseEvent.CLICK,onClick);			
		}

		private function updateCheckState(event:Event):void {
			if(data){
				if (data.checked==true) {
					selected=true;
					var cck:ComboCheck=ComboCheck(ComboCheckDropDownFactory(owner).owner);
					var index:int=cck.selectedItems.getItemIndex(data);
	        		if (index==-1) {
						cck.selectedItems.addItem(data);
	        		}
				}else{
					selected = false;
				}
			}
		}

        private function onClick(event:Event):void {
	        super.data.checked=selected;
	        var myComboCheckEvent:ComboCheckEvent=new ComboCheckEvent(ComboCheckEvent.COMBO_CHECKED);
	        myComboCheckEvent.obj=data;
	        owner.dispatchEvent(myComboCheckEvent);
        }
	}
}