<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 18.11.13
 * Time: 11:40
 */


require_once "Table.php";

class Installations extends Table {
    public static $NAME = "installations";
    private static $CREATE = "CREATE TABLE installations (
        id integer AUTO_INCREMENT,
        uuid varchar(255) NOT NULL,
        CONSTRAINT unique_uuid UNIQUE (uuid),
        PRIMARY KEY (id)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "id",			    // 0
        "uuid"				// 1
    );

    const ID = "id", UUID = "uuid";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function register($uuid){
        $this->insert(array(self::UUID),array($uuid));
    }

    public function isRegistered($uuid){
        return !$this->isUnique(array(self::UUID),array($uuid));
    }
}