<?php
/**
 * Created by PhpStorm.
 * User: Team 1 2013
 * Date: 29.10.13
 * Time: 23:34
 */

require_once "lib/all.php";

ActionsHandler::process();

class ActionsHandler {
    const ACTION = "do";
    const RESULT = "result";
    const OK = "OK";
    const YES = "true";
    const NO = "false";
    const ERROR = "ERROR";
    const MESSAGE = "message";
    const DATA = "data";

    public static final function process(){
        /* setting corresponding json header to use with Spring */
        header('Content-Type: application/json');

        $action = $_GET[self::ACTION];
        if(String::length($action) === 0) self::error("[ActionsHandler] action isn't specified");
        switch ($action) {
            case AddToAttended::ACTION :{
                new AddToAttended();
                break;
            }
            case IsAttended::ACTION :{
                new IsAttended();
                break;
            }
            case Rate::ACTION:{
                new Rate();
                break;
            }
            case IsRated::ACTION:{
                new IsRated();
                break;
            }

            case GetRating::ACTION:{
                new GetRating();
                break;
            }
            case RegisterUUID::ACTION:{
                new RegisterUUID();
                break;
            }
            default: {
                self::error("[ActionsHandler] action not found");
            }
        }
    }

    public static final function error($message){
        die(json_encode(array(self::RESULT => self::ERROR, self::MESSAGE=>$message)));
    }

    public static final function success($message){
        exit(json_encode(array(self::RESULT => self::OK, self::DATA=>$message)));
    }

    public static final function yes($data){
        exit(json_encode(array(self::RESULT => self::YES, self::DATA=>$data)));
    }

    public static final function no($message) {
        exit(json_encode(array(self::RESULT => self::NO, self::MESSAGE=>$message)));
    }
}






























