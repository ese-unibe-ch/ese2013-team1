<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 14:08
 */

require_once "Table.php";

class SportsDB extends Table {
    public static $NAME = "sports";

    private static $CREATE = "CREATE TABLE sports (
        sid integer AUTO_INCREMENT,
        hash varchar(16) NOT NULL UNIQUE,
		sport varchar(255) NOT NULL,
		url varchar(255) NOT NULL UNIQUE,
		sportImage varchar(255) DEFAULT '',
		descriptionHeader TEXT DEFAULT '',
		PRIMARY KEY (sid)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "sid",			        // 0
        "hash",                 // 1
        "sport",			    // 2
        "url",                  // 3
        "sportImage",           // 4
        "descriptionHeader"     // 5
    );

    const SID = "sid", HASH = "hash",SPORT = "sport", URL = "url",SPORT_IMAGE = "sportImage", DESCRIPTION_HEADER = "descriptionHeader";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($hash,$sportName, $url,$image,$descriptionHeader){
        return $this->insert(array(self::HASH,self::SPORT,self::URL,self::SPORT_IMAGE,self::DESCRIPTION_HEADER),array($hash,$sportName,$url,$image,$descriptionHeader));
    }

    public function clear(){
        $this->deleteByID(array(),array());
    }

    public function rename($sid, $sport){

    }

    public function isExists($sportID){
        return !$this->isUnique(array(self::SID),array($sportID));
    }

    public function getData($sportID){
        return $this->getRow($this->selectByID(array(self::SID),array($sportID),self::$DB),0);
    }

    public function getAllSportData(){
        return $this->selectAll(self::$DB,array(self::SPORT));
    }

    public function delete($sportID){
        $sportCoursesDB = new SportEvents();
        $courseDB = new EventsDB();
        $ids = $sportCoursesDB->getAllEventsIDs($sportID);

        foreach($ids as $cid){
            $courseDB->delete($cid[SportEvents::EID]);
        }

        $this->deleteByID(array(self::SID),array($sportID));

    }
} 