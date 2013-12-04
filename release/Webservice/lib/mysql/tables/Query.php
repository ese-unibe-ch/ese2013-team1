<?php
require_once "lib/lang/ArrayList.php";
require_once "lib/lang/Arrays.php";

/**
 * Class Query
 */
class Query {
    const SELECT = "select";
    const INSERT = "insert";
    const UPDATE = "update";
    const DELETE = "delete";

    private $action;            // String
    private $table;             // String

	private $columns;           // ArrayList
    private $values;            // ArrayList
    private $whereClauses;      // ArrayList

    private $orderBy;
	
	function __construct() {
		$this->columns = new ArrayList();
        $this->whereClauses = new ArrayList();
        $this->values = new ArrayList();
        $this->orderBy = new ArrayList();
	}
	
	public function select($columns){
		$this->columns->addAll($columns);
        $this->action = self::SELECT;
		return $this;
	}

    public function insert($column){
        $this->columns->addAll($column);
        for ($i = 0,$length = count($column); $i < $length; $i++){
            $this->values->add("?");
        }
        $this->action = self::INSERT;
        return $this;
    }

    public function update($table){
        $this->table = $table;
        $this->action = self::UPDATE;
        return $this;
    }

    public function set($columns){
        $this->columns->addAll($columns);
        return $this;
    }

    public function into($table){
        $this->table = $table;
        return $this;
    }
	
	public function from($table){
		$this->table = $table;
		return $this;
	}

    public function deleteFrom($table){
        $this->table = $table;
        $this->action = self::DELETE;
        return $this;
    }

	
	public function whereEquals($column){
        $clause = new WhereClause($column);
        $clause->equals();
        $this->whereClauses->add($clause);
		return $this;
	}
	
	public function whereLess($column){
        $clause = new WhereClause($column);
        $clause->less();
        $this->whereClauses->add($clause);
        return $this;
	}

    public function whereMore($column){
        $clause = new WhereClause($column);
        $clause->more();
        $this->whereClauses->add($clause);
        return $this;
    }

    public function whereLessEquals($column){
        $clause = new WhereClause($column);
        $clause->lessEquals();
        $this->whereClauses->add($clause);
        return $this;
    }

    public function whereMoreEquals($column){
        $clause = new WhereClause($column);
        $clause->moreEquals();
        $this->whereClauses->add($clause);
        return $this;
    }

    public function orderBy($column){
        $this->orderBy->addAll($column);
        return $this;
    }

    public function build(){
        switch($this->action){
            case self::INSERT:{
                return $this->buildInsert();
                break;
            }
            case self::SELECT:{
                return $this->buildSelect();
                break;
            }
            case self::UPDATE:{
                return $this->buildUpdate();
                break;
            }
            case self::DELETE:{
                return $this->buildDelete();
                break;
            }
            default:{
                die("Query type undefined");
            }
        }
    }


    public function getOrderBy(){
        return $this->orderBy;
    }

    /**
     * Builds insert into sql query
     * @return string - built query
     */
    private function buildInsert(){
        if ($this->action !== self::INSERT || $this->columns->size() !== $this->values->size()) die ("query is not Insert or number of columns != number of values");
        $query = 'INSERT INTO '.$this->table;
        if ($this->columns->size() > 0){
            $query .= ' ('.Arrays::merge($this->columns->getAll(),',').')';
        }
        $query .= ' VALUES ('.Arrays::merge($this->values->getAll(),',').');';
        return $query;
    }

    /**
     *
     */
    private function buildSelect(){
        if ($this->action !== self::SELECT) die ("query is not Select");
        $query = 'SELECT '.Arrays::merge($this->columns->getAll(),',').' FROM `'.$this->table.'`';
        if ($this->whereClauses->size() > 0){
            $query .= ' WHERE ';
            $index = 0;
            foreach ($this->whereClauses->getAll() as $clause){
                if ($index !== 0) $query .= ' AND ';
                $query .= $clause->toString();
                $index++;
            }
        }

        if($this->orderBy->size() > 0){
            $query .= ' ORDER BY ';

            $index = 0;
            foreach ($this->orderBy->getAll() as $order){
                if ($index !== 0) $query .= ',';
                $query .= 'UPPER('.$order.')';
                $index++;
            }
        }
        return $query.';';
    }

    private function buildUpdate(){
        if ($this->action !== self::UPDATE) die ("query is not Update");
        $query = 'UPDATE '.$this->table.' SET ';
        $index = 0;
        foreach ($this->columns->getAll() as $column){
            if ($index !== 0) $query .= ', ';
            $query .= $column.' = ?';
            $index++;
        }

        if ($this->whereClauses->size() > 0){
            $query .= ' WHERE ';
            $index = 0;
            foreach ($this->whereClauses->getAll() as $clause){
                if ($index !== 0) $query .= ' AND ';
                $query .= $clause->toString();
                $index++;
            }
        }
        return $query.';';
    }

    private function buildDelete(){
        if ($this->action !== self::DELETE) die ("query is not Delete");
        $query = 'DELETE FROM '.$this->table;
        if ($this->whereClauses->size() > 0){
            $query .= ' WHERE ';
            $index = 0;
            foreach ($this->whereClauses->getAll() as $clause){
                if ($index !== 0) $query .= ' AND ';
                $query .= $clause->toString();
                $index++;
            }
        }
        return $query.';';
    }
}

/**
 * Class WhereClause
 */
class WhereClause {
    const EQUALS = "=";
    const MORE = ">";
    const LESS = "<";
    const MORE_EQUALS = ">=";
    const LESS_EQUALS = "<=";

    private $clause;
    private $column;
    private $value;

    function __construct($column) {
        $this->column = $column;
        $this->value = "?";
    }

    public function equals(){
        $this->clause = self::EQUALS;
        return $this;
    }

    public function more(){
        $this->clause = self::MORE;
        return $this;
    }

    public function less(){
        $this->clause = self::LESS;
        return $this;
    }

    public function  moreEquals(){
        $this->clause = self::MORE_EQUALS;
        return $this;
    }

    public function lessEquals(){
        $this->clause = self::LESS_EQUALS;
        return $this;
    }

    public function toString(){
        return '`'.$this->column.'` '.$this->clause.' '.$this->value;
    }
}
?>