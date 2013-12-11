<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 26.11.13
 * Time: 18:10
 */

require_once "all.php";
require_once "Parser.php";

class Updater {

    const LOCK = "updater.dat";
    const LINKS = "angebot.json";

    const STEP = 1;

    private $inProgress;

    private $linksNum;
    private $currentLink = 0;

    private $installer;
    private $parser;
    private $links;

    private $parsedLinks;


    public function __construct(){
        $this->installer = new Installer();
        $this->parser = new Parser();
        $this->parsedLinks = new ArrayList();
        $this->init();
    }

    private function init(){
        DB::useTemp();
        if (file_exists(self::LOCK)){
            $this->inProgress = TRUE;
            $state = file_get_contents(self::LOCK);
            $tmp = explode(":",$state);
            $this->currentLink = Int::intValue($tmp[0])+1;
            $this->linksNum = Int::intValue($tmp[1]);
        }
        else {
            $this->installer->drop();
            $this->installer->create();
        }
    }

    public function update(){
        if ($this->inProgress){
            $this->loadAngebotLinks();
            if ($this->links->size() === 0) $this->parseAngebotLinks();
        }
        else {
            $this->parseAngebotLinks();
            $this->linksNum = $this->links->size();
        }

        if ($this->currentLink >= $this->linksNum) {
            return $this->onComplete();
        }

        $url = $this->links->get($this->currentLink)->getURL();

        $sport = null;
        if (!$this->parsedLinks->contains($url)){
            $sport = $this->parser->parseSport($url);
            //$timeStart = microtime(true);
            $sport->saveInDB();
            //echo ' saved in: '.(microtime(true)-$timeStart).' ';
        }

        $this->saveCurrentState();
        $this->currentLink++;
        if ($sport !== null){
            return array("current"=>$this->currentLink,"total"=>$this->linksNum,"completed"=>false,"sport"=>$sport->toJson());
        }
        else {
            return array("current"=>$this->currentLink,"total"=>$this->linksNum,"completed"=>false, "sport"=>array("sportName"=>"duplicated","sportHash"=>"-"));
        }
    }

    private function saveCurrentState(){
        file_put_contents(self::LOCK, $this->currentLink.':'.$this->linksNum, LOCK_EX);
    }


    private function onComplete(){
        $this->clearTemp();
        self::copyDatabase();
        return array("current"=>$this->currentLink,"total"=>$this->linksNum,"completed"=>true,"sport"=>array("sportName"=>"completed","sportHash"=>"-"));
    }

    private function clearTemp(){
        unlink(self::LOCK);
        unlink(self::LINKS);
    }

    private function parseAngebotLinks(){
        $this->links = $this->parser->parseAngebotLinks();
        $this->saveAngebotLinks();
    }

    private function saveAngebotLinks(){
        $json = new ArrayList();
        foreach ($this->links->getAll() as $link){
            $json->add($link->toJson());
        }
        $str = json_encode($json->getAll());

        file_put_contents(self::LINKS, $str, LOCK_EX);
    }

    private function loadAngebotLinks(){
        $this->links = new ArrayList();
        $str = file_get_contents(self::LINKS);

        $linksArray = json_decode($str,true);
        foreach($linksArray as $link){
            $this->links->add(AngebotLink::create($link['name'],$link['url']));
        }
        if ($this->currentLink > 0){
            $parsedLinks = $this->links->getArray(0,$this->currentLink);
            foreach ($parsedLinks as $link){
                $this->parsedLinks->add($link->getURL());
            }
        }
    }

    public static function copyDatabase(){
        $isTemp = DB::isTemp();
        DB::useMain();
        $tables = new ArrayList();
        $tables->add(new IntervalDB());
        $tables->add(new SportsDB());
        $tables->add(new EventsDB());
        $tables->add(new SportEvents());
        $tables->add(new EventKEW());
        $tables->add(new EventPeriods());
        $tables->add(new EventDaysOfWeek());

        foreach($tables->getAll() as $table){
            $table->renameToTemp();
            $table->moveFromTemp();
        }

        $length = $tables->size();
        for($i = $length - 1; $i >= 0; $i--){
            $tables->get($i)->useTemp();
            $tables->get($i)->drop();
        }

        if ($isTemp){
            DB::useTemp();
        }

    }

} 