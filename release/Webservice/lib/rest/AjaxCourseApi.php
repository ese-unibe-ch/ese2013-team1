<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 20.11.13
 * Time: 19:15
 */

class AjaxAddCourse {
    const ACTION = "ajaxAddCourse";
    const SPORT_ID = "sportID";
    const COURSE_NAME = "courseName";

    private $sportID;
    private $courseName;

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->sportID = $_POST[self::SPORT_ID];
        $this->courseName = $_POST[self::COURSE_NAME];
        if(String::length($this->courseName) === 0 || String::length($this->sportID) !== 4) AjaxActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $sportsDB = new SportsDB();
        $coursesDB = new EventsDB();
        $sportCourses = new SportEvents();

        if (!$sportsDB->isExists($this->sportID)) AjaxActionsHandler::error("[".self::ACTION."] sport not found");

        $cid = $coursesDB->add($this->courseName);
        $sportCourses->add($this->sportID,$cid);

        $course = Course::createByID($cid);

        AjaxActionsHandler::success($course->toJson());
    }
}

class AjaxEditCourse {
    const ACTION = "ajaxEditCourse";
    const COURSE_ID = "courseID";
    const COURSE_NAME = "courseName";
    const INFO_LINK = "infoLink";
    const REGISTRATION = "registration";
    const REGISTRATION_LINK = "registrationLink";
    const PERIODS = "periods";
    const KEW = "kew";

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        if(!array_key_exists(self::COURSE_ID, $_POST) || !array_key_exists(self::COURSE_NAME, $_POST) || !array_key_exists(self::INFO_LINK, $_POST)
            || !array_key_exists(self::REGISTRATION, $_POST) || !array_key_exists(self::REGISTRATION_LINK, $_POST) || !array_key_exists(self::PERIODS, $_POST)
            || !array_key_exists(self::KEW, $_POST))  AjaxActionsHandler::error("[".self::ACTION."] not all parameters are specified");

        $this->init();

    }
    private function init(){
        $courseID = $_POST[self::COURSE_ID];

        $courseDB = new EventsDB();
        $coursePeriodDB = new EventPeriods();
        $courseKewDB = new EventKEW();

        if (!$courseDB->isExists($courseID)) AjaxActionsHandler::error("[".self::ACTION."] course not found");

        $courseDB->updateAll($courseID,$_POST[self::COURSE_NAME],$_POST[self::INFO_LINK],$_POST[self::REGISTRATION],$_POST[self::REGISTRATION_LINK]);

        $periods = $_POST[self::PERIODS];
        $kew = $_POST[self::KEW];

        if (String::length($periods) > 0){
            $coursePeriodDB->updateAll($courseID,explode(',',$periods));
        }

        if (String::length($kew) > 0){
            $courseKewDB->updateAll($courseID,explode(',',$kew));
        }


        $course = Course::createByID($courseID);
        AjaxActionsHandler::success($course->toJson());

    }
}

class AjaxDeleteCourse {
    const ACTION = "ajaxDeleteCourse";
    const COURSE_ID = "courseID";

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        if(!array_key_exists(self::COURSE_ID, $_POST)) AjaxActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $courseID = $_POST[self::COURSE_ID];
        $course = Course::createByID($courseID);

        $courseDB = new EventsDB();
        $courseDB->delete($courseID);

        AjaxActionsHandler::success($course->toJson());
    }
}