<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 16:06
 */

require_once "Table.php";

class EventsDB extends Table {
    public static $NAME = "events";

    private static $CREATE = "CREATE TABLE events (
        eid integer NOT NULL AUTO_INCREMENT,
		iid varchar(16) DEFAULT NULL,
		pid varchar(16) DEFAULT NULL,
		hash varchar(32) NOT NULL UNIQUE,
		eventName varchar(255) NOT NULL,
		date varchar(255) DEFAULT NULL,
		infoLink varchar(255) DEFAULT '',
		registration varchar(255) DEFAULT '',
		registrationLink varchar(255) DEFAULT '',
		PRIMARY KEY (eid),
		FOREIGN KEY (iid)
        REFERENCES intervals(iid)
        ON DELETE SET NULL,
        FOREIGN KEY (pid)
        REFERENCES places(pid)
        ON DELETE SET NULL
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "eid",			        // 0
        "iid",                  // 1
        "pid",                  // 2
        "hash",                 // 3
        "eventName",		    // 4
        "date",                 // 5
        "infoLink",             // 6
        "registration",         // 7
        "registrationLink"      // 8
    );

    const EID = "eid", IID = "iid", PID = "pid", HASH = "hash", EVENT_NAME = "eventName", DATE = "date",INFO_LINK = "infoLink", REGISTRATION = "registration", REGISTRATION_LINK = "registrationLink";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($hash,$event){
        return $this->insert(array(self::HASH,self::EVENT_NAME),array($hash,$event));
    }

    public function isExists($eventID){
        return !$this->isUnique(array(self::EID),array($eventID));
    }

    public function setInterval($eventID,$intervalID){
        $this->updateByID(array(self::EID),array($eventID),array(self::IID),array($intervalID));
    }

    public function setPlace($eventID,$placeID){
        $this->updateByID(array(self::EID),array($eventID),array(self::PID),array($placeID));
    }

    public function getData($eventID){
        return $this->getRow($this->selectByID(array(self::EID),array($eventID),self::$DB),0);
    }

    public function getIdByHash($hash){
        $data = $this->getRow($this->selectByID(array(self::HASH),array($hash),array(self::EID)),0);
        if (count($data) === 0){
            return 0;
        }
        else return Int::intValue($data[self::EID]);
    }

    public function updateAll($eventID,$eventName, $date, $infoLink,$registration,$registrationLink){
        $this->updateByID(array(self::EID),array($eventID),array(self::EVENT_NAME,self::DATE,self::INFO_LINK,self::REGISTRATION,self::REGISTRATION_LINK),array($eventName,$date,$infoLink,$registration,$registrationLink));
    }

    public function delete($eventID){
        $this->deleteByID(array(self::EID),array($eventID));
    }
} 