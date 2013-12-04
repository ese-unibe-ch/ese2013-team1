<?php

class DB {
	const HOSTNAME = "localhost";
	const USERNAME = "";
	const PASSWORD = "";
	const DATABASE = "";
    const DATABASE_TEMP = "";

    private static $db = null;

    private static $useTemp = FALSE;

	public static final function db(){
        if (self::$db === null) {
            if (!self::$useTemp){
                self::$db = self::init(self::DATABASE);
            }
            else {
                self::$db = self::init(self::DATABASE_TEMP);
            }
        }
        return self::$db;
	}

    public static final function useTemp(){
        self::$useTemp = TRUE;
        self::$db = self::init(self::DATABASE_TEMP);
    }

    public static final function useMain(){
        self::$useTemp = FALSE;
        self::$db = self::init(self::DATABASE);
    }

    public static final function isTemp(){
        return self::$useTemp;
    }

    private static final function init($database){
        try {
            $db = new PDO('mysql:host='.self::HOSTNAME.';dbname='.$database, self::USERNAME, self::PASSWORD, array(
                PDO::ATTR_PERSISTENT => true,
                PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"
            ));
            return $db;
        } catch (PDOException $e) {
            print "Error!: unable to connect database <br/>";
            die();
        }
    }


    private function __construct(){}
    private function __clone(){}
}

?>