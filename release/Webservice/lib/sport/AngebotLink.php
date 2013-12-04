<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 26.11.13
 * Time: 14:35
 */

class AngebotLink {

    private $name;
    private $url;

    public function __constructor(){

    }

    public static function create($name, $url){
        $link = new AngebotLink();
        $link->name = $name;
        $link->url = $url;
        return $link;
    }

    public function __toString(){
        return "name : ".$this->name.', url : '.$this->url;
    }

    public function getURL(){
        return $this->url;
    }

    public function getName(){
        return $this->name;
    }

    public function toJson(){
        return array("name"=>$this->name,"url"=>$this->url);
    }
} 