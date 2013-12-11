<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 30.10.13
 * Time: 14:35
 */

require_once "Event.php";

class Sport {
    private $events;

    private $sportID;                                       // String representing unique sport identificator
    private $sportHash;

    private $sportName;
    private $sportLink;
    private $sportImage;
    private $descriptionHeader;

    private $sportDB;
    private $sportEventsDB;

    const SPORT_ID = "sportID", SPORT_HASH = "sportHash", SPORT_NAME = "sportName",SPORT_LINK = "sportLink",
        SPORT_IMAGE = "sportImage", DESCRIPTION_HEADER = "descriptionHeader", EVENTS = "events";

    public static function createByID($sportID){
        $sport = new Sport();
        $sport->sportID = $sportID;

        $sport->init();
        return $sport;
    }

    public function __construct(){
        $this->events = new ArrayList();
        $this->sportDB = new SportsDB();
        $this->sportEventsDB = new SportEvents();
    }


    private function init(){
        $data = $this->sportDB->getData($this->sportID);
        if (count($data) == 0) die("Unknown sportID: ".$this->sportID);
        $this->sportName = $data[SportsDB::SPORT];
        $this->sportHash = $data[SportsDB::HASH];
        $this->sportLink = $data[SportsDB::URL];
        $this->sportImage = $data[SportsDB::SPORT_IMAGE];
        $this->descriptionHeader = $data[SportsDB::DESCRIPTION_HEADER];
        $eventsIDs = $this->sportEventsDB->getAllEventsIDs($this->sportID);

        if (count($eventsIDs) === 0) return;

        foreach($eventsIDs as $id){
            $this->events->add(Event::createByID($id[SportEvents::EID]));
        }
    }

    public function saveInDB(){
        if (String::length($this->sportID) === 0){
            $this->sportHash = $this->hash();
            $this->sportID = $this->sportDB->add($this->sportHash,$this->sportName,$this->sportLink,$this->sportImage,$this->descriptionHeader);
        }

        foreach ($this->events->getAll() as $event){
            $event->saveInDB();
            $this->sportEventsDB->add($this->sportID,$event->getEventID());
        }
    }

    private function hash(){
        return substr(md5($this->sportName),0,16);
    }

    public function addEvent($event){
        $this->events->addAll($event);
    }

    public function setSportName($name){
        $this->sportName = $name;
    }

    public function setSportImage($image){
        $this->sportImage = $image;
    }

    public function setSportDescriptionHeader($description){
        $this->descriptionHeader = $description;
    }

    public function setSportURL($link){
        $this->sportLink = $link;
    }

    public function getSportID(){
        return $this->sportID;
    }

    public function getEvents(){
        return $this->events;
    }

    public function __toString(){
        return "SportID: ".$this->sportID." SportName: ".$this->sportName."\n".' URL: '.$this->sportLink.' Events:'."\n".$this->events;
    }

    public function toJson(){
        $jSonArray = array();
        $jSonArray[self::SPORT_ID] = Int::intValue($this->sportID);
        $jSonArray[self::SPORT_HASH] = $this->sportHash;
        $jSonArray[self::SPORT_NAME] = $this->sportName;
        $jSonArray[self::SPORT_LINK] = $this->sportLink;
        $jSonArray[self::SPORT_IMAGE] = $this->sportImage;
        $jSonArray[self::DESCRIPTION_HEADER] = $this->descriptionHeader;
        $jSonArray[self::EVENTS] = array();

        for ($i = 0,$length=$this->events->size();$i < $length;$i++){
            $jSonArray[self::EVENTS][] = $this->events->get($i)->toJson();
        }

        return $jSonArray;
    }
} 