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
   RepeatedBackground

   Repeated Background.....just like that!

   Created by Maikel Sibbald
   info@flexcoders.nl
   http://labs.flexcoders.nl

   Free to use.... just give me some credit
 */

package 
{
    import flash.display.Bitmap;
    import flash.display.BitmapData;
    import flash.display.Graphics;
    import flash.display.Loader;
    import flash.events.Event;
    import flash.events.IOErrorEvent;
    import flash.geom.Matrix;
    import flash.net.URLRequest;

    import mx.controls.Image;
    import mx.core.BitmapAsset;
    import mx.graphics.RectangularDropShadow;
    import mx.skins.RectangularBorder;

    public class RepeatedBackground extends RectangularBorder
    {
        private var tile:BitmapData;

        [Embed(source="/assets/application/postlogin_background_image.png")]
        public var imgCls:Class;


        public function RepeatedBackground():void
        {
            var background:BitmapAsset = BitmapAsset(new imgCls());
            this.tile = background.bitmapData;
        }

        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
        {
            super.updateDisplayList(unscaledWidth, unscaledHeight);

            var transform:Matrix = new Matrix();

            // Finally, copy the resulting bitmap into our own graphic context.
            graphics.clear();
            graphics.beginBitmapFill(this.tile, transform, true);
            graphics.drawRect(0, 0, unscaledWidth, unscaledHeight);
        }
    }
}