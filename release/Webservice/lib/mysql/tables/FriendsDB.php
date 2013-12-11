<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 09.12.13
 * Time: 19:17
 */

require_once "Table.php";

class FriendsDB extends Table {
    public static $NAME = "friends";

    private static $CREATE = "CREATE TABLE friends (
        userid integer NOT NULL,
        friendid integer NOT NULL,
        FOREIGN KEY (userid)
        REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (friendid)
        REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT unique_userid_friendid UNIQUE (userid,friendid)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "userid",			    // 0
        "friendid"				// 1
    );

    const USER_ID = "userid", FRIEND_ID = "friendid";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($userid,$friendid) {
        $this->insert(array(self::USER_ID,self::FRIEND_ID),array($userid,$friendid));
        $this->insert(array(self::USER_ID,self::FRIEND_ID),array($friendid,$userid));
    }

    public function isFriends($userid,$friendid){
        return !$this->isUnique(array(self::USER_ID,self::FRIEND_ID),array($userid,$friendid));
    }

    public function getFriends($userid){
        $sql = "SELECT id as userID,nickname as nickname,username as username,picture as picture FROM ".Users::$NAME." WHERE id IN (SELECT friendid FROM ".self::$NAME." WHERE userid = ?) ORDER BY nickname,username";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array($userid)));
    }
}