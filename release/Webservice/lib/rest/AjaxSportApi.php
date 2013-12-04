<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 20.11.13
 * Time: 0:10
 */

class AjaxAddSport {
    const ACTION = "ajaxAddSport";
    const SPORT_NAME = "sportName";

    private $sportName;

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->sportName = $_POST[self::SPORT_NAME];
        if(String::length($this->sportName) === 0) AjaxActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $sportsDB = new SportsDB();
        //$sid = $sportsDB->add($this->sportName);

        //AjaxActionsHandler::success(array("sportID"=>$sid,"sportName"=>$this->sportName));
    }
}

class AjaxDeleteSport {
    const ACTION = "ajaxDeleteSport";
    const SPORT_ID = "sportID";

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        if(!array_key_exists(self::SPORT_ID, $_POST)) AjaxActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $sportID = $_POST[self::SPORT_ID];

        $sportDB = new SportsDB();
        $sportDB->delete($sportID);

        AjaxActionsHandler::success(array("sportID"=>$sportID));
    }
}