<?php

require_once "./lib/all.php";

abstract class Table {
	private $table;
	private $create;
	private $structure;

    private $useTemp;

	function __construct($name,$create,$structure) {
		$this->table = $name;
		$this->create = $create;
		$this->structure = $structure;
        $this->useTemp = FALSE;
		$this->__init();
	}
	
	private function __init(){
		if ($this->isExist() === FALSE) $this->create();
	}
	
	public function create(){
        DB::db()->prepare($this->create)->execute();
	}

    public function drop(){
        DB::db()->prepare("DROP TABLE IF EXISTS ".$this->name().';')->execute();
    }

    public function useTemp(){
        $this->useTemp = TRUE;
    }

    public function name(){
        if ($this->useTemp){
            return "tmp".$this->table;
        }
        else {
            return $this->table;
        }
    }

    public function moveFromTemp(){
        DB::db()->prepare("RENAME TABLE ".DB::DATABASE_TEMP.".".$this->name()." TO ".DB::DATABASE.".".$this->name().";")->execute();
    }

    public function copyToTemp(){
        DB::db()->prepare("DROP TABLE IF EXISTS tmp".$this->table.';')->execute();
        DB::db()->prepare("CREATE TABLE tmp".$this->table." LIKE ".$this->table.";")->execute();
        DB::db()->prepare("INSERT tmp".$this->table." SELECT * FROM ".$this->table.";")->execute();
    }

    public function renameToTemp(){
        DB::db()->prepare("RENAME TABLE ".$this->table." TO tmp".$this->table.';')->execute();
    }
	
	protected function isExist(){
		$query = "select 1 from ".$this->name().";";
        $statement = DB::db()->prepare($query);
        return (count(self::getRow(self::getResult( $statement, $statement->execute()),0)) > 0);
	}
	
	protected function selectByID($keys, $keyValues,$columns){
		$query = new Query();
		$query = $query->select($columns)->from($this->name());
        foreach ($keys as $key){
            $query->whereEquals($key);
        }
        $query = $query->build();
        $statement = DB::db()->prepare($query);
        return self::getResult($statement, $statement->execute($keyValues));
	}

    /**
     * @param $columns
     * @param[optional] $order
     * @return array
     */
    protected function selectAll($columns){
        $query = new Query();
        $query = $query->select($columns)->from($this->name());
        if (func_num_args() > 1){
            $query->orderBy(func_get_arg(1));
        }
        $query = $query->build();
        $statement = DB::db()->prepare($query);
        return self::getResult($statement,$statement->execute());
    }

    protected function updateByID($keys, $keyValues,$columns,$values){
        $query = new Query();
        $query->update($this->name())->set($columns);
        foreach ($keys as $key){
            $query->whereEquals($key);
        }
        $query = $query->build();
        $preparedValues = array_merge((array)$values, (array)$keyValues);
        DB::db()->prepare($query)->execute($preparedValues);
    }

    protected function deleteByID($keys, $keyValues){
        $query = new Query();
        $query->deleteFrom($this->name());
        foreach ($keys as $key){
            $query->whereEquals($key);
        }
        $query = $query->build();
        DB::db()->prepare($query)->execute($keyValues);
    }

    protected function insert($params,$paramValues){
        if (count($params) !== count($paramValues)) die ("number of columns != values in Table::insert()");
        $query = new Query();
        $query = $query->insert($params)->into($this->name())->build();
        DB::db()->prepare($query)->execute($paramValues);
        return DB::db()->lastInsertId();
    }

    protected function isUnique($columns, $values){
        return count($this->selectByID($columns, $values,array("*"))) === 0;
    }

    public static final function getResult($cursor,$success){
        $result = array(array());
        if ($success === FALSE) return $result;
        $result = $cursor->fetchAll(PDO::FETCH_ASSOC);
        return $result;
    }

    public static final function getRow($matrix,$row){
        if (count($matrix) >= $row && $row !== 0) die ("ArrayOutOfBounds row=$row while size=".count($matrix));
        if ($row === 0 && count($matrix) === 0) return array();
        return $matrix[$row];
    }
}

?>