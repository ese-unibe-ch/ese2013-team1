<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 20:53
 */

class Int {

    public static final function isInt($string){
        $string .="";
        $int = intval($string);
        if (String::length($int.'') !== String::length($string) || $int.'' !== $string){
            $test = ''.$int;
            for ($i = String::length($test); $i < String::length($string); $i++){
                $test = '0'.$test;
            }
            if ($test !== $string){
                return false;
            }
            else return true;
        }
        else return true;
    }

    public static final function intValue($string){
        if (self::isInt($string)) return (int)$string;
        return 0;
    }
} 