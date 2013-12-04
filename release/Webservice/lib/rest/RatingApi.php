<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 15.11.13
 * Time: 10:51
 */

require_once "./lib/all.php";

class Rate {
    const ACTION = "rate";
    const UUID = "uuid";
    const HASH = "hash";
    const RATING = "rating";

    private $uuid;
    private $hash;
    private $rating;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[Rate] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->hash = $_GET[self::HASH];
        $this->rating = $_GET[self::RATING];
        if(String::length($this->uuid) === 0 || String::length($this->hash) === 0 || String::length($this->rating) === 0) ActionsHandler::error("[Rate] not all parameters are specified");
        $this->rating = intval($this->rating);
        $this->init();
    }

    private function init(){
        if ($this->rating <= 0 || $this->rating > 5 || "".$this->rating !== $_GET[self::RATING]) ActionsHandler::error("[Rate] wrong rating! should be > 0 and <= 5");
        $rating = new Rating();
        if ($rating->isRated($this->uuid,$this->hash)) ActionsHandler::error("RATED");
        $rating->add($this->uuid,$this->hash,$this->rating);
        ActionsHandler::success("course rated");
    }
}

class IsRated {
    const ACTION = "isRated";
    const UUID = "uuid";
    const HASH = "hash";

    private $uuid;
    private $hash;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[isRated] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->hash = $_GET[self::HASH];
        if(String::length($this->uuid) === 0 || String::length($this->hash) === 0) ActionsHandler::error("[isRated] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $rating = new Rating();
        if (!$rating->isRated($this->uuid,$this->hash)) ActionsHandler::no("course is not rated");
        $data = $rating->getData($this->uuid,$this->hash);
        ActionsHandler::yes($data);
    }
}

class GetRating {
    const ACTION = "getRating";
    const HASH = "hash";

    private $hash;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[getRating] wrong class error");
        $this->hash = $_GET[self::HASH];
        if(String::length($this->hash) === 0) ActionsHandler::error("[getRating] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $rating = new Rating();
        $data = $rating->getRating($this->hash);

        $ratingArray = array("1"=>0,"2"=>0,"3"=>0,"4"=>0,"5"=>0);
        foreach ($data as $rate){
            $ratingArray[$rate[Rating::RATING]] = $rate[Rating::COUNT]*1;
        }

        ActionsHandler::success($ratingArray);
    }
}