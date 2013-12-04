<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 18:18
 */

require_once "Time.php";

class Interval {

    private $intervalID;

    private $timeFrom;
    private $timeTo;
    private $status;

    private $intervalDB;

    const INTERVAL_ID = "intervalID",TIME_FROM = "timeFrom", TIME_TO = "timeTo", STATUS = "status";

    function __construct() {
        $this->intervalDB = new IntervalDB();
    }

    public static function createFrom($timeFrom,$timeTo){
        $interval = new Interval();
        $interval->timeFrom = $timeFrom;
        $interval->timeTo = $timeTo;
        return $interval;
    }

    public static function createByID($intervalID){
        $interval = new Interval();
        $interval->intervalID = $intervalID;
        $interval->init();
        return $interval;
    }

    private function init(){
        $data = $this->intervalDB->getData($this->intervalID);
        $this->timeFrom = Time::fromMinutes(Integer::intValue($data[IntervalDB::TIME_FROM]));
        $this->timeTo = Time::fromMinutes(Integer::intValue($data[IntervalDB::TIME_TO]));
        $this->status = $data[IntervalDB::STATUS];
    }

    public function saveInDB(){
        if (String::length($this->intervalID) === 0){
            $this->intervalID = $this->intervalDB->add($this->hash(),$this->timeFrom->toMinutes(),$this->timeTo->toMinutes(),$this->status);
        }
    }

    public function getIntervalID(){
        return $this->intervalID;
    }


    public function setTimeFrom($timeFrom){
        $this->timeFrom = $timeFrom;
    }

    public function setTimeTo($timeTo){
        $this->timeTo = $timeTo;
    }


    public function setStatus($status){
        $this->status = $status;
    }

    public function __toString(){
        return 'time: '.$this->timeFrom.'-'.$this->timeTo.' status: '.$this->status;
    }

    public function toJson(){
        $jSonArray = array();
        $jSonArray[self::INTERVAL_ID] = $this->intervalID;
        $jSonArray[self::TIME_FROM] = $this->timeFrom->toJson();
        $jSonArray[self::TIME_TO] = $this->timeTo->toJson();
        $jSonArray[self::STATUS] = $this->status;

        return $jSonArray;
    }

    public function hash(){
        return substr(md5($this->timeFrom->hash().$this->timeTo->hash().$this->status),0,16);
    }
} 