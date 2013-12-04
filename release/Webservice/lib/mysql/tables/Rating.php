<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 30.10.13
 * Time: 13:36
 */

require_once "Table.php";

class Rating extends Table {
    public static $NAME = "rating";
    private static $CREATE = "CREATE TABLE rating (
        uuid varchar(255) NOT NULL,
		hash varchar(255) NOT NULL,
		rating integer NOT NULL,
		CONSTRAINT unique_uuid_hash_rating UNIQUE (uuid,hash)
	)ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static $DB = array(
        "uuid",			    // 0
        "hash",				// 1
        "rating"			// 2
    );

    const UUID = "uuid", HASH = "hash", RATING = "rating", COUNT = "count";

    function __construct() {
        parent::__construct(self::$NAME,self::$CREATE,self::$DB);
    }

    public function add($uuid,$hash,$rating){
        $this->insert(array(self::UUID,self::HASH,self::RATING),array($uuid,$hash,$rating));
    }

    public function getData($uuid,$hash){
        return $this->getRow($this->selectByID(array(self::UUID,self::HASH),array($uuid,$hash),self::$DB),0);
    }

    public function isRated($uuid,$hash){
        return !$this->isUnique(array(self::UUID,self::HASH),array($uuid,$hash));
    }

    public function getRating($hash){
        $query = "SELECT rating, COUNT(*) as ".self::COUNT." FROM ".self::$NAME." WHERE hash = ? GROUP BY rating;";
        $statement = DB::db()->prepare($query);
        return self::getResult($statement, $statement->execute(array($hash)));
    }
} 