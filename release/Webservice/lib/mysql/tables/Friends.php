<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 15.11.13
 * Time: 10:31
 */

require_once "Table.php";

class Friends extends Table {
    public static $NAME = "friends";
    private static $CREATE = "CREATE TABLE friends (
        uid integer NOT NULL,
        fid integer NOT NULL,
        CONSTRAINT unique_user_friend UNIQUE (uid,fid)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "uid",			    // 0
        "fid"				// 1
    );

    const UID = "uid", FID = "fid";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function addFriend($uid,$fid){
        $this->insert(array(self::UID,self::FID),array($uid,$fid));
        $this->insert(array(self::UID,self::FID),array($fid,$uid));
    }

    public function getFriendsData($uid){
        $query = "SELECT u.* FROM ".Users::$NAME." u INNER JOIN ".self::$NAME." f ON f.".self::FID." = u.".Users::ID." WHERE f.".self::UID." = ?;";
        $statement = DB::db()->prepare($query);
        return self::getResult($statement, $statement->execute(array($uid)));
    }
} 