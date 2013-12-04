<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 22:09
 */

class KewParser {

    private $mKew;

    private $mRaw;

    function __construct($raw){
        $this->mRaw = String::toLowerCase(trim($raw));
        $this->mKew = new ArrayList();
        $this->parse();
    }

    private function parse(){
        if (String::length($this->mRaw) === 0) return;
        $kewArray = explode('/',$this->mRaw);
        foreach($kewArray as $kew){
            $this->mKew->add(trim($kew));
        }
    }

    public function getKew(){
        return $this->mKew;
    }
} 