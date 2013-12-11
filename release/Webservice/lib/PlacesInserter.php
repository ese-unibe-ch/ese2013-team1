<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 07.12.13
 * Time: 9:59
 */

class PlacesInserter {
    const DATA_FILE_NAME = "places.dat";

    public function insert(){
        if (!file_exists(self::DATA_FILE_NAME)) die ("error reading file ".self::DATA_FILE_NAME);
        $handle = fopen(self::DATA_FILE_NAME, "r");
        $placeDB = new PlaceDB();
        if ($handle) {
            while (($line = fgets($handle)) !== false) {
                $place = explode("|",$line);
                if (count($place) != 4) continue;
                $hash = $place[0];
                $name = $place[1];
                $lat = $place[2];
                $lon = $place[3];
                $placeDB->update($hash,$name,$lat,$lon);
            }
        } else {
            die ("error reading file ".self::DATA_FILE_NAME);
        }
    }
} 