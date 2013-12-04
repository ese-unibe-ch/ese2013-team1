<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 15.11.13
 * Time: 10:01
 */

require_once "Table.php";

class Users extends Table {
    public static $NAME = "users";
    private static $CREATE = "CREATE TABLE users (
        id integer AUTO_INCREMENT,
        uuid varchar(255) NOT NULL,
        nickname varchar(255) UNIQUE,
        username varchar(255) DEFAULT 'user',
        picture varchar(255),
        CONSTRAINT unique_nickname_users UNIQUE (nickname),
        CONSTRAINT unique_uuid_users UNIQUE (uuid),
        PRIMARY KEY (id)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "id",			    // 0
        "uuid",				// 1
        "nickname",         // 2
        "username",	    	// 3
        "picture"           // 4
    );

    const ID = "id", UUID = "uuid", NICKNAME = "nickname", USERNAME = "username", PICTURE = "picture";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * Adds new user
     * @param $uuid
     * @param $name
     * @return id of new added users or -1 if user with this uuid and name already exists
     */
    public function add($uuid,$name){
        /* if there is already user with uuid and name, return -1 as error result */
        if (!$this->isUnique(array(self::UUID,self::NICKNAME),array($uuid,$name))) return -1;

        $this->insert(array(self::UUID,self::NICKNAME),array($uuid,$name));
        $result = $this->getRow($this->selectByID(array(self::UUID,self::NICKNAME),array($uuid,$name),array(self::ID)),0);
        return $result[self::ID];
    }

    public function getData($id){
        return $this->getRow($this->selectByID(array(self::ID),array($id),self::$DB),0);
    }

    public function isExists($username){

    }
} 