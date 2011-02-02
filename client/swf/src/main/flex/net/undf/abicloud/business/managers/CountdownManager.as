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

package net.undf.abicloud.business.managers
{
    import flash.events.Event;
    import flash.events.EventDispatcher;
    import flash.events.TimerEvent;
    import flash.utils.Timer;

    /**
     * Simple countdown manager
     *
     * This class provides a countdown, and can be used to display a countdown from any number of seconds
     * The countdown can be repeated any number of times, and everytime the countdown is reached, an event
     * will be dispatched
     *
     * In the future, this class should be multiton, since CountdownManager could offer multiple countdowns to different
     * consumers at the same time
     *
     * @author Oliver
     *
     */
    public class CountdownManager extends EventDispatcher
    {

        public static const COUNTDOWN_COMPLETE_EVENT:String = "countdownCompleteCountdownManager";

        private var _timer:Timer;

        private var _currentSeconds:Number;

        private var _startTime:Number = 0;

        public function CountdownManager()
        {
            //Initializing timer for the first time, and setting listeners
            this._timer = new Timer(1000, 0);

            this._timer.addEventListener(TimerEvent.TIMER, timerHandler);
            this._timer.addEventListener(TimerEvent.TIMER_COMPLETE, timerCompleteHandler);
        }

        /**
         * Starts the countdown. The current state of the countdown can be watched using the property currentSeconds
         * @param startTime The start time (in seconds) for the countdown
         * @param repeatCount The number of times for the countdown. 0 means infinite
         *
         */
        public function startAutoCountdown(startTime:Number, repeatCount:Number = 0):void
        {
            if (this._timer.running)
            {
                this._timer.stop();
                dispatchEvent(new Event("clockRunningUpdate"));
            }

            this._timer.repeatCount = 1000 * startTime * repeatCount;

            this._startTime = startTime;
            this._currentSeconds = startTime;
            dispatchEvent(new Event("currentSecondsUpdate"));

            this._timer.start();
            dispatchEvent(new Event("clockRunningUpdate"));
        }

        /**
         * Stops the current countdown
         *
         */
        public function stopAutoCountdown():void
        {
            if (this._timer && this._timer.running)
            {
                this._timer.stop();
                this._currentSeconds = this._startTime;
                dispatchEvent(new Event("clockRunningUpdate"));
            }
        }

        private function timerHandler(timerEvent:TimerEvent):void
        {
            if (this._currentSeconds == 0)
            {
                this._currentSeconds = this._startTime;
                dispatchEvent(new Event(CountdownManager.COUNTDOWN_COMPLETE_EVENT));
            }
            else
                this._currentSeconds = this._currentSeconds - 1;

            dispatchEvent(new Event("currentSecondsUpdate"));
        }

        private function timerCompleteHandler(timerEvent:TimerEvent):void
        {
            this._timer.stop();
            dispatchEvent(new Event("clockRunningUpdate"));

            this._currentSeconds = 0;
            dispatchEvent(new Event("currentSecondsUpdate"));
        }

        [Bindable(event="clockRunningUpdate")]
        public function get clockRunning():Boolean
        {
            return this._timer.running;
        }

        [Bindable(event="currentSecondsUpdate")]
        public function get currentSeconds():Number
        {
            return this._currentSeconds;
        }
    }
}