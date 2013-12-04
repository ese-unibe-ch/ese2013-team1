<?php

class ArrayList {

	private $elements = array();
	private $size = 0;
	
	public function add($element){
		$this->elements[] = $element;
		$this->size++;
	}

    public function addAll($elements){
        $this->elements = array_merge((array)$this->elements, (array)$elements);
        $this->size += count($elements);
    }

	public function get($index){
		if ($index < 0 || $index >= $this->size) die("ArrayOutOfBounds index=$index while size=$this->size");
		return $this->elements[$index];
	}

    public function getAll(){
        return $this->elements;
    }

    public function getArray($start,$finish){
        return array_slice($this->elements,$start,$finish);
    }
	
	public function indexOf($element){
		return array_keys($this->elements, $element);
	}
	
	public function contains($element){
		return in_array($element, $this->elements);
	}
	
	public function isEmpty(){
		return empty($this->elements);
	}
	
	public function removeByIndex($index){
		if ($index < 0 || $index >= $this->size) die("ArrayOutOfBounds index=$index while size=$this->size");
		unset($this->elements[$index]);
		$this->size--;
	}
	
	public function remove($element){
		if ($this->contains($element)){
			$this->removeByIndex($this->indexOf($element));
			$this->size--;
		}
	}
	
	public function size(){
		return $this->size;
	}

    public function __toString(){
        $str = '[';
        $index = 0;
        foreach($this->elements as $element){
            if ($index > 0) $str .= ',';
            $str .= $element;
            $index++;
        }
        $str .= ']';
        return $str;
    }

    public function hash(){
        $str = '';
        foreach($this->elements as $element){
            $str .= $element;
        }
        return md5($str);
    }
}

?>