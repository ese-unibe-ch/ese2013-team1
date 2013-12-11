<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 09.12.13
 * Time: 22:16
 */

require_once "Table.php";

class FriendRequests extends Table {
    public static $NAME = "friendRequests";

    private static $CREATE = "CREATE TABLE friendRequests (
        userid integer NOT NULL,
        friendid integer NOT NULL,
        date TIMESTAMP NOT NULL,
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
        "friendid",				// 1
        "date"                  // 2
    );

    const USER_ID = "userid", FRIEND_ID = "friendid", DATE = "date";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    /**
     * @param $userid - person who receive request
     * @param $friendid - request sender
     */
    public function add($userid,$friendid) {
        $this->insert(array(self::USER_ID,self::FRIEND_ID),array($userid,$friendid));
    }

    public function isExists($userid,$friendid){
        return !$this->isUnique(array(self::USER_ID,self::FRIEND_ID),array($userid,$friendid))
            || !$this->isUnique(array(self::USER_ID,self::FRIEND_ID),array($friendid,$userid));
    }

    public function removeRequest($userid,$friendid){
        $this->deleteByID(array(self::USER_ID,self::FRIEND_ID),array($userid,$friendid));
        $this->deleteByID(array(self::USER_ID,self::FRIEND_ID),array($friendid,$userid));
    }

    public function getRequestsTo($userid){
        $sql = "SELECT id as userID,nickname as nickname,username as username,picture as picture FROM ".Users::$NAME." WHERE id IN (SELECT friendid FROM ".self::$NAME." WHERE userid = ?)";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array($userid)));
    }

    public function getMyRequests($userid){
        $sql = "SELECT id as userID,nickname as nickname,username as username,picture as picture FROM ".Users::$NAME." WHERE id IN (SELECT userid FROM ".self::$NAME." WHERE friendid = ?)";
        $statement = DB::db()->prepare($sql);
        return $this->getResult($statement,$statement->execute(array($userid)));
    }
}