<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 22:43
 */

class DaysOfWeekParser {

    private $mRaw;

    private $mDaysOfWeek;

    const MONDAY = "mo";
    const TUESDAY = "di";
    const WEDNESDAY = "mi";
    const THURSDAY = "do";
    const FRIDAY = "fr";
    const SATURDAY = "sa";
    const SUNDAY = "so";
    const DIVIDER_INTERVAL = "-";
    const DIVIDER_AND = "/";

    function __construct($raw){
        $this->mRaw = trim($raw);
        $this->mDaysOfWeek = new ArrayList();
        $this->parse();
    }

    private function parse(){
        if (String::length($this->mRaw) === 0) return;
        $tmpDays = new ArrayList();

		$days = explode(self::DIVIDER_AND,$this->mRaw);

		foreach ($days as $day){
            $interval = explode(self::DIVIDER_INTERVAL,$day);
            if (count($interval) === 2){
                $dayFrom = $this->parseDay($interval[0]);
				$dayTo = $this->parseDay($interval[1]);

				if ($dayFrom === 0 || $dayTo === 0) return;

                if ($dayFrom <= $dayTo){
                    for ($i = $dayFrom; $i <= $dayTo;$i++){
                        if (!$tmpDays->contains($i)) $tmpDays->add($i);
                    }
                }
                else {
                    for ($i = $dayFrom; $i <= 7;$i++){
                        if (!$tmpDays->contains($i)) $tmpDays->add($i);
                    }
                    for ($i = 1; $i <= $dayTo;$i++){
                        if (!$tmpDays->contains($i)) $tmpDays->add($i);
                    }
                }

			}
            else if (count($interval) === 1){
                $dayOfWeek = $this->parseDay($interval[0]);
                if ($dayOfWeek === 0) return;
				if (!$tmpDays->contains($dayOfWeek)) $tmpDays->add($dayOfWeek);
			}
            else {
                return;
            }
        }

        $values = $tmpDays->getAll();
        sort($values,SORT_NUMERIC);
        $this->mDaysOfWeek->addAll($values);
    }

    private function parseDay($day) {
        if (String::length($day) === 0) return 0;

        $day = String::toLowerCase($day);

        if ($day === self::MONDAY){
            return 1;
        }
        else if ($day === self::TUESDAY){
            return 2;
        }
        else if ($day === self::WEDNESDAY){
            return 3;
        }
        else if ($day === self::THURSDAY){
            return 4;
        }
        else if ($day === self::FRIDAY){
            return 5;
        }
        else if ($day === self::SATURDAY){
            return 6;
        }
        else if ($day === self::SUNDAY){
            return 7;
        }
        else {
            return 0;
        }
    }

    public function getDaysOfWeek(){
        return $this->mDaysOfWeek;
    }
} 