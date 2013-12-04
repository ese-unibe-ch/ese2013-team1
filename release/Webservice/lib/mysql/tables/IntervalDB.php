<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 18:21
 */

require_once "Table.php";

class IntervalDB extends Table {
    public static $NAME = "intervals";

    private static $CREATE = "CREATE TABLE intervals (
        iid varchar(16) NOT NULL,
		timeFrom integer NOT NULL,
		timeTo integer NOT NULL,
		status varchar(255) DEFAULT NULL,
		PRIMARY KEY (iid)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "iid",			    // 0
        "timeFrom",	        // 1
        "timeTo",           // 2
        "status"            // 3
    );

    const IID = "iid", TIME_FROM = "timeFrom", TIME_TO = "timeTo", STATUS = "status";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($hash,$timeFrom,$timeTo,$status) {
        $this->insert(array(self::IID,self::TIME_FROM,self::TIME_TO,self::STATUS),array($hash,$timeFrom,$timeTo,$status));
        return $hash;
    }

    public function isExists($intervalID){
        return !$this->isUnique(array(self::IID),array($intervalID));
    }

    public function getData($intervalID){
        return $this->getRow($this->selectByID(array(self::IID),array($intervalID),self::$DB),0);
    }
}