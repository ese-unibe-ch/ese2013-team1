<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 19.11.13
 * Time: 19:21
 */



class Unisport {

    private $sports;            // ArrayList of all sports

    private $sportsDB;

    function __construct() {
        $this->sports = new ArrayList();
        $this->sportsDB = new SportsDB();
        $this->init();
    }

    private function init(){
        $data = $this->sportsDB->getAllSportData();
        if (count($data) === 0 || count($data[0]) === 0) return;
        foreach ($data as $sportData){
            $this->sports->add(Sport::createByID($sportData[SportsDB::SID]));
        }
    }



    public function toString(){
        $str = "Sports:\n";

        for ($i = 0, $length = $this->sports->size(); $i < $length; $i++){
            $str .= $this->sports->get($i)->toString();
        }

        return $str;
    }

    public function toJson(){

        $sportsArray = array();

        for ($i = 0, $length = $this->sports->size(); $i < $length; $i++){
            $sportsArray[] = $this->sports->get($i)->toJson();
        }

        $unisportJsonArray['sports'] = $sportsArray;

        return $unisportJsonArray;
    }
}