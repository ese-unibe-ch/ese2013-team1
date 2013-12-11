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
        username varchar(255) DEFAULT NULL,
        password varchar(64) NOT NULL,
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
        "password",         // 4
        "picture"           // 5
    );

    const ID = "id", UUID = "uuid", NICKNAME = "nickname", USERNAME = "username", PASSWORD = "password", PICTURE = "picture";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * Adds new user
     * @param $uuid
     * @param $nickname
     * @param $password
     * @return id of new added users or -1 if user with this uuid and name already exists
     */
    public function add($uuid,$nickname,$password){
        /* if there is already user with uuid and name, return -1 as error result */
        if (!$this->isUnique(array(self::UUID),array($uuid))) return -1;
        if (!$this->isUnique(array(self::NICKNAME),array($nickname))) return -2;

        $this->insert(array(self::UUID,self::NICKNAME,self::PASSWORD),array($uuid,$nickname,$this->passwordHash($password)));
        $result = $this->getRow($this->selectByID(array(self::UUID,self::NICKNAME),array($uuid,$nickname),array(self::ID)),0);
        return Int::intValue($result[self::ID]);
    }

    public function getUserLoginData($nickname,$password){
        return $this->getRow($this->selectByID(array(self::NICKNAME,self::PASSWORD),array($nickname,$this->passwordHash($password)),array(self::UUID,self::ID,self::NICKNAME,self::USERNAME)),0);
    }

    public function getData($id){
        return $this->getRow($this->selectByID(array(self::ID),array($id),self::$DB),0);
    }

    public function getAttendedEvents($id,$dateFrom){
        $sql = "SELECT * FROM ".Attended::$NAME." WHERE share = ? AND uuid IN (SELECT uuid FROM ".self::$NAME." WHERE id = ?) AND date >= ? AND hash IN (SELECT hash FROM ".EventsDB::$NAME.") ORDER BY date ASC";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array("1",$id,$dateFrom)));
    }

    public function getNews($userid){
        $sql = "SELECT u.id as userID,u.nickname, u.username, u.picture, a.hash, a.date, a.stamp, (SELECT e.eventName FROM ".EventsDB::$NAME." e WHERE e.hash = a.hash LIMIT 1) as eventName FROM ".self::$NAME." u INNER JOIN ".Attended::$NAME." a ON a.uuid = u.uuid WHERE a.share = ? AND a.uuid IN (SELECT uuid FROM ".self::$NAME." WHERE id IN (SELECT friendid as id FROM ".FriendsDB::$NAME." WHERE userid = ?)) AND hash IN (SELECT hash FROM ".EventsDB::$NAME.") ORDER BY stamp DESC LIMIT 30;";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array("1",$userid)));
    }

    public function setUsername($userid,$username){
        $this->updateByID(array(self::ID),array($userid),array(self::USERNAME),array($username));
    }

    public function passwordHash($password){
        return substr(hash("sha256",$password),0,64);
    }

    public function isExists($nickname){
        return !$this->isUnique(array(self::NICKNAME),array($nickname));
    }

    public function search($userid,$query){
        $sql = "SELECT id as userID,nickname as nickname,username as username,picture as picture FROM ".self::$NAME." WHERE (".self::NICKNAME." LIKE ? OR ".self::USERNAME." LIKE ?) AND id != ? AND id NOT IN (SELECT friendid as id FROM ".FriendsDB::$NAME." WHERE userid = ?) AND id NOT IN (SELECT friendid as id FROM ".FriendRequests::$NAME." WHERE userid = ?) AND id NOT IN (SELECT userid as id FROM ".FriendRequests::$NAME." WHERE friendid = ?)";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array("%$query%","%$query%",$userid,$userid,$userid,$userid)));
    }

    public function isRegistered($uuid,$userID){
        return !$this->isUnique(array(self::UUID,self::ID),array($uuid,$userID));
    }

    public function isUserExists($userID){
        return !$this->isUnique(array(self::ID),array($userID));
    }
} 