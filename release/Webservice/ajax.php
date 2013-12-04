<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 20.11.13
 * Time: 12:09
 */

require_once "lib/all.php";

Activity::create();

AjaxActionsHandler::process();

class AjaxActionsHandler {
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

        $action = $_POST[self::ACTION];
        if(String::length($action) === 0) self::error("[AjaxActionsHandler] action isn't specified");
        switch ($action) {
            case AjaxAddSport::ACTION:{
                new AjaxAddSport();
                break;
            }
            case AjaxDeleteSport::ACTION:{
                new AjaxDeleteSport();
                break;
            }
            case AjaxAddCourse::ACTION:{
                new AjaxAddCourse();
                break;
            }
            case AjaxEditCourse::ACTION:{
                new AjaxEditCourse();
                break;
            }
            case AjaxDeleteCourse::ACTION:{
                new AjaxDeleteCourse();
                break;
            }
            case AjaxUpdate::ACTION:{
                new AjaxUpdate();
                break;
            }
            default: {
                self::error("[AjaxActionsHandler] action not found");
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

Activity::destroy();