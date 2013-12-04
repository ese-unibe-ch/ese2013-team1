<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 23:59
 */

class PlaceParser {
    private $mRaw;
    private $mPlaces;

    function __construct($raw){
        $this->mRaw = trim($raw);
        $this->mPlaces = new ArrayList();
        $this->parse();
    }

    private function parse(){
        if (String::length($this->mRaw) === 0) return;
        $placeArray = explode('/',$this->mRaw);
        foreach($placeArray as $place){
            $this->mPlaces->add(trim($place));
        }
    }

    public function getPlaces(){
        return $this->mPlaces;
    }
} 