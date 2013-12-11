<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 23:17
 */

/**
 * Converted from java TimeParser
 */
class TimeParser {
    private $mRaw;

    private $intervals;

    const ALL_DAY = "ganzer Tag";
    const ON_REQUEST = "auf Anfrage";
    const INTERVAL_DIVIDER = "/";
    const TIME_DIVIDER = "-";
    const HOUR_MINUTE_DIVIDER = ".";

    private $ALL_DAY_START_TIME;
    private $ALL_DAY_FINISH_TIME;
    private $UNKNOWN_TIME;

    function __construct($raw){
        $this->mRaw = trim($raw);
        $this->intervals = new ArrayList();
        $this->ALL_DAY_START_TIME = Time::allDayStart();
        $this->ALL_DAY_FINISH_TIME = Time::allDayFinish();
        $this->UNKNOWN_TIME = Time::unknownTime();

        if (String::length($this->mRaw) === 0){
            $this->intervals->add(Interval::createFrom($this->UNKNOWN_TIME,$this->UNKNOWN_TIME));
        }
        else {
            $intervalStrArray = explode(self::INTERVAL_DIVIDER,$this->mRaw);
			foreach ($intervalStrArray as $intervalStr){
                $this->parse($intervalStr);
            }
		}
    }

    private function parse($str){

		if (String::length($str) === 0){
            $this->intervals->add(Interval::createFrom($this->UNKNOWN_TIME,$this->UNKNOWN_TIME));
            return;
        }
		if (String::toLowerCase($str) === String::toLowerCase(self::ALL_DAY)){
            $interval = Interval::createFrom($this->ALL_DAY_START_TIME,$this->ALL_DAY_FINISH_TIME);
            $interval->setStatus($str);
            $this->intervals->add($interval);
            return;
        }
		if (String::toLowerCase($str) === String::toLowerCase(self::ON_REQUEST)){
            $interval = Interval::createFrom($this->UNKNOWN_TIME,$this->UNKNOWN_TIME);
            $interval->setStatus($str);
            $this->intervals->add($interval);
            return;
        }

        $times = explode(self::TIME_DIVIDER,$str);
        if (count($times) === 2){
            $timeFrom = $this->parseTime($times[0]);
            $timeTo = $this->parseTime($times[1]);
            $interval = Interval::createFrom($timeFrom,$timeTo);
            if ($timeFrom === $this->UNKNOWN_TIME || $timeTo === $this->UNKNOWN_TIME)$interval->setStatus($str);
            $this->intervals->add($interval);
        }
        else if (count($times) === 1){
            $timeFrom = $this->parseTime($times[0]);
            $timeTo = $this->UNKNOWN_TIME;
            $interval = Interval::createFrom($timeFrom,$timeTo);
            if ($timeFrom === $this->UNKNOWN_TIME || $timeTo === $this->UNKNOWN_TIME)$interval->setStatus($str);
            $this->intervals->add($interval);
        }
        else {
            $interval = Interval::createFrom($this->UNKNOWN_TIME,$this->UNKNOWN_TIME);
            $interval->setStatus($str);
            $this->intervals->add($interval);
        }


		/*$startHour = 0;
		$startMinute = 0;
		$finishHour = 0;
		$finishMinute = 0;
		$startHourFound = 0;
		$startMinuteFound = 0;
		$finishHourFound = 0;
		$finishMinuteFound = 0;
		$lastFoundAt = 0;
        $k = 0;
		foreach ($chars as $c){
            $integer = ord($c)-ord('0');
            if ($integer >= 0 && $integer <= 9){
                if ($startHourFound < 2){
                    $startHour *= $startHourFound * 10;
                    $startHour += $integer;
                    $startHourFound++;
                    $k++;
                    $lastFoundAt = $k;
                    continue;
                }
                if ($startMinuteFound < 2){
                    if ($startMinuteFound === 0 && $lastFoundAt === $k) return;
                    $startMinute *= $startMinuteFound * 10;
                    $startMinute += $integer;
                    $startMinuteFound++;
                    $k++;
                    $lastFoundAt = k;
                    continue;
                }
                if ($finishHourFound < 2){
                    if ($finishHourFound === 0 && $lastFoundAt === $k) return;
                    $finishHour *= $finishHourFound * 10;
                    $finishHour += $integer;
                    $finishHourFound++;
                    $k++;
                    $lastFoundAt = $k;
                    continue;
                }
                if ($finishMinuteFound < 2){
                    if ($finishMinuteFound === 0 && $lastFoundAt === $k) return;
                    $finishMinute *= $finishMinuteFound * 10;
                    $finishMinute += $integer;
                    $finishMinuteFound++;
                    $k++;
                    $lastFoundAt = $k;
                    continue;
                }
                else return;
            }
            $k++;
        }
		if ($startHourFound === 2 && $startMinuteFound === 2 && $finishHourFound === 2 && $finishMinuteFound === 2){
            $this->intervals->add(Interval::createFrom(Time::fromTime($startHour,$startMinute),Time::fromTime($finishHour,$finishMinute)));
        }
        else if ($startHourFound === 2 && $startMinuteFound === 2){
            $this->intervals->add(Interval::createFrom(Time::fromTime($startHour,$startMinute),$this->UNKNOWN_TIME));
        }
        else {
            $interval = Interval::createFrom($this->UNKNOWN_TIME,$this->UNKNOWN_TIME);
            $interval->setStatus($str);
            $this->intervals->add($interval);
        }*/
    }

    private function parseTime($str){
        $time = explode(self::HOUR_MINUTE_DIVIDER,$str);
        if (count($time) !== 2){
            return $this->UNKNOWN_TIME;
        }
        $hours = $time[0];
        $minutes = $time[1];
        if (!Int::isInt($hours) || !Int::isInt($minutes)){
            return $this->UNKNOWN_TIME;
        }
        return Time::fromTime(Int::intValue($hours),Int::intValue($minutes));
    }

    public function getIntervals(){
        return $this->intervals;
    }
} 