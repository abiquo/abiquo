package net.undf.abicloud.view.general.components.info
{
	import flash.events.MouseEvent;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	
	import mx.controls.Image;
	import mx.effects.Fade;
	
	import net.undf.abicloud.view.main.CommonAssets;

	[Bindable]
	public class InfoIcon extends Image
	{		
		//show/hide effect
		private var _fadeEffect:Fade;
		
		//Wiki's URL opens when user clicks
		private var _wikiUrl:String;
		
		public function InfoIcon()
		{
			super();
			_fadeEffect = new Fade();
			source = CommonAssets.info;
			buttonMode = true;
			//visible = false;
			toolTip = resourceManager.getString('Common','TOOLTIP_INFO');
			addEventListener(MouseEvent.CLICK, openMoreInfo);
			setStyle("showEffect", _fadeEffect);
            setStyle("hideEffect", _fadeEffect);
		}
		
		/******************
		 * 
		 * Getters/Setters 
		 * 
		 * ****************/
		
		public function set wikiUrl(url:String):void{
			this._wikiUrl = url;
		}
		
		public function get wikiUrl():String{
			return this._wikiUrl;
		}
		
		
		/**
         * Display the specific Wiki page
         */
        private function openMoreInfo(mouseEvent:MouseEvent):void{
        	if(visible){
        		//visible = false;
        		navigateToURL(new URLRequest(wikiUrl),"_blank");
        	}
        }
        
        /**
         * Show the info icon if user presses the CTRL key
         */
        public function showIconInfo(keyPressed:Boolean):void
        {
           visible = keyPressed;
           buttonMode = keyPressed;
        }

        /**
         * Hide the info icon
         */
        public function hideIconInfo():void
        {
            visible = false;
            buttonMode = false;
        }		
		
		
	}
}