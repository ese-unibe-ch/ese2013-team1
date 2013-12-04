<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 18:19
 */

class Time {
    private $hours;
    private $minutes;

    private $unknown;

    const HOURS = "hours", MINUTES = "minutes", UNKNOWN = "unknown";

    private function __construct() {}

    public static function fromMinutes($minutes){
        $time = new Time();
        if ($minutes < 0 || $minutes >= 1440) $time->unknown = true;
        else {
            $time->minutes = $minutes % 60;
            $time->hours = ($minutes - $time->minutes) / 60;
            $time->unknown = false;
        }
        return $time;
    }

    public static function fromTime($hours,$minutes){
        $time = new Time();
        $time->hours = $hours;
        $time->minutes = $minutes;
        $time->unknown = FALSE;
        return $time;
    }

    public static function unknownTime(){
        $time = new Time();
        $time->unknown = TRUE;
        $time->hours = -1;
        $time->minutes = -1;
        return $time;
    }

    public static function allDayStart(){
        $time = new Time();
        $time->unknown = FALSE;
        $time->hours = 0;
        $time->minutes = 0;
        return $time;
    }

    public static function allDayFinish(){
        $time = new Time();
        $time->unknown = FALSE;
        $time->hours = 23;
        $time->minutes = 59;
        return $time;
    }

    public function __toString(){
        if ($this->unknown) return '?';
        return $this->hours.':'.$this->minutes;
    }

    public function toMinutes(){
        if ($this->unknown) return -1;
        return $this->hours * 60 + $this->minutes;
    }

    public function toJson(){
        $jSonArray = array();
        $jSonArray[self::HOURS] = $this->hours;
        $jSonArray[self::MINUTES] = $this->minutes;
        $jSonArray[self::UNKNOWN] = $this->unknown;

        return $jSonArray;
    }

    public function hash(){
        return md5($this->hours.$this->minutes.$this->unknown);
    }
} 