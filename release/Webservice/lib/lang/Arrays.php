<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 29.10.13
 * Time: 19:34
 */

class Arrays {
    /**
     * @param $array
     * @param $delimiter
     * @return string
     */
    public static final function merge($array, $delimiter){
        $i = 0;
        $string = "";
        foreach ($array as $object){
            if ($i!==0)$string .= $delimiter;
            $string .= $object;
            $i++;
        }
        return $string;
    }

    public static final function isKeyExists($key , $search){
        return array_key_exists($key, $search);
    }
} 