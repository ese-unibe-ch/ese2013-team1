<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 17:19
 */

require_once "Table.php";

class EventKEW extends Table {
    public static $NAME = "eventKew";

    private static $CREATE = "CREATE TABLE eventKew (
        eid integer NOT NULL,
		kew varchar(1) NOT NULL,
		CONSTRAINT unique_eid_kew UNIQUE (eid,kew),
		FOREIGN KEY (eid)
        REFERENCES events(eid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "eid",			    // 0
        "kew"		        // 1
    );

    const EID = "eid", KEW = "kew", K = "k", E = "e", W = "w";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * Adds kew to event
     * @param $eventID - event id
     * @param $kew - kew type, can be 'k','e' or 'w'
     */
    public function add($eventID,$kew){
        $this->insert(array(self::EID,self::KEW),array($eventID,$kew));
    }

    public function getKEW($eventID){
        return $this->selectByID(array(self::EID),array($eventID),array(self::KEW));
    }

    public function updateAll($eventID, $kew){
        $this->deleteByID(array(self::EID),array($eventID));

        if (in_array(self::K, $kew)){
            $this->add($eventID,self::K);
        }
        if (in_array(self::E, $kew)){
            $this->add($eventID,self::E);
        }
        if (in_array(self::W, $kew)){
            $this->add($eventID,self::W);
        }
    }

} 