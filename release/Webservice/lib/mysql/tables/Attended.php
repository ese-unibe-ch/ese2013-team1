<?php

require_once "Table.php";

class Attended extends Table {
	public static $NAME = "attended";
	private static $CREATE = "CREATE TABLE attended (
	    uuid varchar(255) NOT NULL,
		hash varchar(32) NOT NULL,
		date integer NOT NULL,
		share integer NOT NULL,
		stamp TIMESTAMP NOT NULL,
		CONSTRAINT unique_uuid_hash_date UNIQUE (uuid,hash,date)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	
	public static $DB = array(
        "uuid",             // 0
		"hash",			    // 1
		"date",				// 2
		"share",			// 3
        "stamp"             // 4
	);
	
	const UUID = "uuid", HASH = "hash", DATE = "date", SHARE = "share", STAMP = "stamp";
	
	function __construct() {
		parent::__construct(self::$NAME,self::$CREATE,self::$DB);
	}

    public function add($uuid,$hash, $date, $share){
        $this->insert(array(self::UUID,self::HASH,self::DATE,self::SHARE),array($uuid,$hash, $date, $share));
    }

    public function getDataAll(){
        return $this->selectAll(self::$DB);
    }

    public function getData($uuid,$hash,$date){
        return $this->getRow($this->selectByID(array(self::UUID,self::HASH,self::DATE),array($uuid,$hash,$date),self::$DB),0);
    }

    public function isAttended($uuid,$hash,$date){
        return !$this->isUnique(array(self::UUID,self::HASH,self::DATE),array($uuid,$hash,$date));
    }
}