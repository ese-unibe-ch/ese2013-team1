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
        pid varchar(16) NOT NULL,
		place varchar(255) NOT NULL UNIQUE,
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

    public function add($hash,$place) {
        $isTemp = DB::isTemp();
        if ($isTemp === true) $this->insert(array(self::PID,self::PLACE),array($hash,$place));
        DB::useMain();
        $this->insert(array(self::PID,self::PLACE),array($hash,$place));
        if ($isTemp === true) DB::useTemp();
        return $hash;
    }

    public function isExistsID($placeID){
        return !$this->isUnique(array(self::PID),array($placeID));
    }

    public function isExistPlace($place){
        return !$this->isUnique(array(self::PLACE),array($place));
    }

    public function update($hash,$place,$lat,$lon){
        $this->updateByID(array(self::PID),array($hash),array(self::PLACE,self::LAT,self::LON),array($place,$lat,$lon));
    }

    public function getData($placeID){
        $isTemp = DB::isTemp();
        DB::useMain();
        $data = $this->getRow($this->selectByID(array(self::PID),array($placeID),self::$DB),0);
        if ($isTemp === true) DB::useTemp();
        return $data;
    }

    public function getAllData(){
        $isTemp = DB::isTemp();
        DB::useMain();
        $data = $this->selectAll(self::$DB,array(self::PLACE));
        if ($isTemp === true) DB::useTemp();
        return $data;
    }
} 