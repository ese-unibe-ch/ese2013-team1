<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 17:56
 */

class Place {

    private $placeID;
    private $placeName;
    private $lat;
    private $lon;

    private $placesDB;

    const PLACE_ID = "placeID", PLACE_NAME = "placeName", LATITUDE = "lat", LONGITUDE = "lon";

    function __construct() {
        $this->placesDB = new PlaceDB();
    }

    public static function createFrom($placeName){
        $place = new Place();
        $place->placeName = $placeName;
        return $place;
    }

    public static function createByID($placeID){
        $place = new Place();
        $place->placeID = $placeID;
        $place->init();
        return $place;
    }

    public function saveInDB(){
        if (String::length($this->placeID) === 0){
            $this->placeID = $this->placesDB->add($this->hash(),$this->placeName);
        }
    }

    private function init(){
        $data = $this->placesDB->getData($this->placeID);
        $this->lat = $data[PlaceDB::LAT];
        $this->lon = $data[PlaceDB::LON];
        $this->placeName = $data[PlaceDB::PLACE];
    }

    public function hash(){
        return substr(hash("sha256",$this->placeName),0,16);
    }

    public function getPlaceID(){
        return $this->placeID;
    }

    public function toJson(){
        $jSonArray = array();

        $jSonArray[self::PLACE_ID] = $this->placeID;
        $jSonArray[self::PLACE_NAME] = $this->placeName;
        $jSonArray[self::LATITUDE] = (double)$this->lat;
        $jSonArray[self::LONGITUDE] = (double)$this->lon;

        return $jSonArray;
    }


} 