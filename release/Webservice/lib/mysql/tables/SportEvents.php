<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 19:48
 */

require_once "Table.php";

class SportEvents extends Table {

    public static $NAME = "sportEvents";

    private static $CREATE = "CREATE TABLE sportEvents (
        sid integer NOT NULL,
        eid integer NOT NULL,
		CONSTRAINT unique_sid_eid UNIQUE (sid,eid),
		FOREIGN KEY (sid)
        REFERENCES sports(sid)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
		FOREIGN KEY (eid)
        REFERENCES events(eid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "sid",			    // 0
        "eid"   		    // 1
    );

    const SID = "sid", EID = "eid";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($sid,$eid){
        $this->insert(array(self::SID,self::EID),array($sid,$eid));
    }

    public function getAllEventsIDs($sportID){
        return $this->selectByID(array(self::SID),array($sportID),array(self::EID));
    }

    public function getSportID($eventID){
        return $this->getRow($this->selectByID(array(self::EID),array($eventID),array(self::SID)),0);
    }
} 