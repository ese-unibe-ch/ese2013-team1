<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 15.11.13
 * Time: 10:50
 */

require_once "./lib/all.php";

class AddToAttended {
    const ACTION = "addToAttended";
    const UUID = "uuid";
    const HASH = "hash";
    const DATE = "date";
    const SHARE = "share";

    private $uuid;
    private $hash;
    private $date;
    private $share;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[AddToAttended] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->hash = $_GET[self::HASH];
        $this->date = $_GET[self::DATE];
        $this->share = $_GET[self::SHARE];
        if(String::length($this->uuid) === 0 || String::length($this->hash) === 0 || String::length($this->date) === 0 || String::length($this->share) === 0) ActionsHandler::error("[AddToAttended] not all parameters are specified");
        $this->date = intval($this->date);
        $this->init();
    }

    private function init(){
        if ($this->share !== "0" && $this->share !== "1") ActionsHandler::error("[AddToAttended] wrong share! 0 and 1 only allowed");
        if ($this->date <= 0 || "".$this->date !== $_GET[self::DATE]) ActionsHandler::error("[AddToAttended] wrong date! should be > 0");
        $attended = new Attended();
        if ($attended->isAttended($this->uuid,$this->hash,$this->date)) ActionsHandler::error("ATTENDED");
        $attended->add($this->uuid,$this->hash,$this->date,$this->share);
        ActionsHandler::success("course added to attended");
    }
}

class IsAttended {
    const ACTION = "isAttended";
    const UUID = "uuid";
    const HASH = "hash";
    const DATE = "date";

    private $uuid;
    private $hash;
    private $date;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[isAttended] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->hash = $_GET[self::HASH];
        $this->date = $_GET[self::DATE];
        if(String::length($this->uuid) === 0 || String::length($this->hash) === 0 || String::length($this->date) === 0) ActionsHandler::error("[isAttended] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->date)) ActionsHandler::error("[isAttended] wrong date! should be > 0");
        $this->date = Int::intValue($this->date);
        $attended = new Attended();
        if (!$attended->isAttended($this->uuid,$this->hash,$this->date)) ActionsHandler::no("course is not attended");
        $data = $attended->getData($this->uuid,$this->hash,$this->date);
        ActionsHandler::yes($data);
    }
}