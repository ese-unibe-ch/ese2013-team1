<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 30.10.13
 * Time: 14:35
 */

class Course {

    private $sportID;

    private $courseID;              // Course unique identificator              +
    private $courseName;            // Course name                              +

    private $events;                // ArrayList of all events

    private $date;                  // Period alternative, only time course
    private $periods;               // When course takes place (array)          +
    private $infoLink;              // Link to .pdf with detail info            +
    private $registration;          // Registration date                        +
    private $registrationLink;      // Link to registration date                +
    private $kew;                   // Course type: k,e,w  (array)              +

    private $courseDB;
    private $coursePeriodsDB;
    private $courseKEWDB;
    private $sportCoursesDB;

    public function __construct(){

    }

    public static function createByID($courseID){
        $course = new Course();

        $course->courseID = $courseID;
        $course->courseDB = new EventsDB();
        $course->coursePeriodsDB = new EventPeriods();
        $course->courseKEWDB = new EventKEW();
        $course->sportCoursesDB = new SportEvents();
        $course->events = new ArrayList();
        $course->periods = new ArrayList();
        $course->kew = new ArrayList();
        $course->init();
        return $course;
    }

    private function init(){
        $data = $this->courseDB->getData($this->courseID);
        if (count($data) == 0) die("Unknown courseID".$this->courseID);
        $this->courseName = $data[EventsDB::EVENT_NAME];
        $this->infoLink = $data[EventsDB::INFO_LINK];
        $this->registration = $data[EventsDB::REGISTRATION];
        $this->registrationLink = $data[EventsDB::REGISTRATION_LINK];

        $this->initSportID();
        $this->initPeriods();
        $this->initKEW();
    }

    public function setDate($date){
        $this->date = $date;
    }

    public function setPeriods($periods){
        $this->periods = $periods;
    }

    public function setInfoLink($infoLink){
        $this->infoLink = $infoLink;
    }

    public function setRegistration($registration){
        $this->registration = $registration;
    }

    public function setRegistrationLink($registrationLink){
        $this->registrationLink = $registrationLink;
    }

    private function initSportID(){
        $data = $this->sportCoursesDB->getSportID($this->courseID);
        if (count($data) === 0) return;
        $this->sportID = $data[SportEvents::SID];
    }

    private function initPeriods(){
        $data = $this->coursePeriodsDB->getPeriods($this->courseID);
        if (count($data) === 0 || count($data[0]) === 0) return;
        foreach ($data as $period){
            $this->periods->add($period[EventPeriods::PERIOD]);
        }
    }

    private function initKEW(){
        $data = $this->courseKEWDB->getKEW($this->courseID);
        if (count($data) === 0 || count($data[0]) === 0) return;
        foreach ($data as $kew){
            $this->kew->add($kew[EventKEW::KEW]);
        }
    }

    public function getName(){
        return $this->courseName;
    }

    public function toJson(){
        $jsonArray = array();

        $jsonArray['sportID'] = $this->sportID;
        $jsonArray['courseID'] = $this->courseID;
        $jsonArray['courseName'] = $this->courseName;
        $jsonArray['periods'] = $this->periods->getAll();
        $jsonArray['infoLink'] = $this->infoLink;
        $jsonArray['registration'] = $this->registration;
        $jsonArray['registrationLink'] = $this->registrationLink;
        $jsonArray['kew'] = $this->kew->getAll();

        return $jsonArray;
    }
}



