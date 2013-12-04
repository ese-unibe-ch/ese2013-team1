<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 18.11.13
 * Time: 11:43
 */

require_once "./lib/all.php";

class RegisterUUID {
    const ACTION = "registerUUID";
    const UUID = "uuid";

    private $uuid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        if(String::length($this->uuid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->date = intval($this->date);
        $this->init();
    }

    private function init(){
        $installations = new Installations();
        if ($installations->isRegistered($this->uuid)) ActionsHandler::error("EXISTS");
        $installations->register($this->uuid);
        ActionsHandler::success($this->uuid);
    }
}