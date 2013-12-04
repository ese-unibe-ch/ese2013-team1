<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 02.12.13
 * Time: 14:24
 */

require_once "Table.php";
class EventDaysOfWeek extends Table {
    public static $NAME = "eventDaysOfWeek";

    private static $CREATE = "CREATE TABLE eventDaysOfWeek (
        eid integer NOT NULL,
		dayOfWeek TINYINT NOT NULL,
		CONSTRAINT unique_eid_kew UNIQUE (eid,dayOfWeek),
		FOREIGN KEY (eid)
        REFERENCES events(eid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "eid",		    	    // 0
        "dayOfWeek"		        // 1
    );

    const EID = "eid", DAY_OF_WEEK = "dayOfWeek";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * Adds days of week to event
     * @param $eventID - course id
     * @param $daysOfWeek - number between 1 and 7
     */
    public function add($eventID,$daysOfWeek){
        $this->insert(array(self::EID,self::DAY_OF_WEEK),array($eventID,$daysOfWeek));
    }

    public function getDaysOfWeek($eventID){
        return $this->selectByID(array(self::EID),array($eventID),array(self::DAY_OF_WEEK));
    }

    public function updateAll($eventID, $daysOfWeek){
        $this->deleteByID(array(self::EID),array($eventID));
        foreach($daysOfWeek as $dayOfWeek){
            $this->add($eventID,$dayOfWeek);
        }
    }
} 