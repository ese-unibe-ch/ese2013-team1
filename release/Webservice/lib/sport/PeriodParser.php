<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 20:40
 */

class PeriodParser {

    private $isPeriod = FALSE;
    private $isDate = FALSE;
    private $isNull = TRUE;

    private $mRaw;

    private $mDate;
    private $mPeriods;

    function __construct($raw){
        $this->mRaw = trim($raw);
        $this->mPeriods = new ArrayList();
        $this->parse();
    }

    private function parse(){
        if (String::length($this->mRaw) === 0){
            $this->isNull = TRUE;
            return;
        }

        $this->isNull = FALSE;

        $periods = explode('|',$this->mRaw);
        if (count($periods) == 1){
            $this->isDate = TRUE;
            $this->mDate = $this->mRaw;
            return;
        }

        foreach($periods as $period){
            $periodInt = Int::intValue(trim($period));
            if ($periodInt > 0 && $periodInt < 6){
                $this->mPeriods->add($periodInt);
            }
        }

        $this->isPeriod = TRUE;
    }

    public function isNull(){
        return $this->isNull;
    }

    public function isPeriods(){
        return $this->isPeriod;
    }

    public function isDate(){
        return $this->isDate;
    }

    public function getDate(){
        return $this->mDate;
    }

    public function getPeriods(){
        return $this->mPeriods;
    }

} 