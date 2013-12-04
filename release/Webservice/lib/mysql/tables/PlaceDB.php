<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 17:27
 */

require_once "Table.php";

class PlaceDB extends Table {
    public static $NAME = "places";

    private static $CREATE = "CREATE TABLE places (
        pid varchar(8) NOT NULL,
		place varchar(255) NOT NULL,
		lat double DEFAULT NULL,
		lon double DEFAULT NULL,
		PRIMARY KEY (pid),
		CONSTRAINT unique_place UNIQUE (place)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "pid",			    // 0
        "place",	        // 1
        "lat",              // 2
        "lon"               // 3
    );

    const PID = "pid", PLACE = "place", LAT = "lat", LON = "lon";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($place){
        static $pid = "";
        while($pid === ""){
            $hash = substr(md5($place.time()),0,8);
            if (!$this->isExistsID($hash)){
                $pid = $hash;
                break;
            }
        }
        $this->insert(array(self::PID,self::PLACE),array($pid,$place));
        return $pid;
    }

    public function isExistsID($placeID){
        return !$this->isUnique(array(self::PID),array($placeID));
    }

    public function isExistPlace($place){
        return !$this->isUnique(array(self::PLACE),array($place));
    }

    public function getData($placeID){
        return $this->getRow($this->selectByID(array(self::PID),array($placeID),self::$DB),0);
    }
} 