<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 16:25
 */

require_once "Table.php";

class EventPeriods extends Table {
    public static $NAME = "eventPeriods";

    private static $CREATE = "CREATE TABLE eventPeriods (
        eid integer NOT NULL,
		period TINYINT NOT NULL,
		CHECK (period BETWEEN 1 AND 5),
		CONSTRAINT unique_eid_period UNIQUE (eid,period),
		FOREIGN KEY (eid)
        REFERENCES events(eid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "eid",			    // 0
        "period"		    // 1
    );

    const EID = "eid", PERIOD = "period";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * Adds period to course
     * @param $eventID - course id
     * @param $period - period number between 1 and 5
     */
    public function add($eventID,$period){
        $this->insert(array(self::EID,self::PERIOD),array($eventID,$period));
    }

    public function getPeriods($eventID){
        return $this->selectByID(array(self::EID),array($eventID),array(self::PERIOD));
    }

    public function updateAll($eventID, $periods){
        $this->deleteByID(array(self::EID),array($eventID));
        foreach($periods as $period){
            $this->add($eventID,$period);
        }
    }

} 