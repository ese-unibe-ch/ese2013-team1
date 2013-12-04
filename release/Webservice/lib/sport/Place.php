<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 17:56
 */

class Place {

    private $placeID;
    private $place;
    private $lat;
    private $lon;

    private $isExists;

    private $placesDB;

    private function __construct() {
        /*$this->placesDB = new PlaceDB();
        $data = $this->placesDB->getData($placeID);
        if (length($data) === 0) {
            $this->isExists = FALSE;
            return;
        }

        $this->placeID = $data[PlaceDB::PID];
        $this->place = $data[PlaceDB::PLACE];
        $this->lat = $data[PlaceDB::LAT];
        $this->lon = $data[PlaceDB::LON];*/
    }

    public static function fromPlace($placeName){
        $place = new Place();
        $place->place = $placeName;
        return $place;
    }

    public function __toString(){
        return $this->place;
    }


} 