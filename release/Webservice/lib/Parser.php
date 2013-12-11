<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 26.11.13
 * Time: 14:28
 */

require_once "all.php";
require_once "ganon.php";

class Parser {
    const ROOT_URL = 'http://www.sport.unibe.ch/angebotlinks/';
    const ANGEBOT = 'angebot.htm';

    public function parseAngebotLinks(){
        $dom = file_get_dom(self::ROOT_URL.self::ANGEBOT);
        $linkLists = $dom->select('div[id=contentDienste] > p.linkliste');
        $index = 0;

        $angebotLinks = new ArrayList();

        foreach($linkLists as $element){
            if ($index === 0) {
                $index++;
                continue;
            }
            $links = $element->select('a');
            foreach($links as $sportLinkAnchor){
                $sportLink = trim($sportLinkAnchor->href);
                $linkArray = parse_url($sportLink);
                if (!Arrays::isKeyExists('host',$linkArray)){
                    $sportLink = self::ROOT_URL.$sportLink;
                }
                $sportName = trim($sportLinkAnchor->getPlainText());

                $link = AngebotLink::create($sportName,$sportLink);
                $angebotLinks->add($link);
            }
            $index++;
        }
        $links = $dom->select('div[id=contentDienste] > p > a.linkliste');
        foreach($links as $sportLinkAnchor){
            $sportLink = trim($sportLinkAnchor->href);
            $linkArray = parse_url($sportLink);
            if (!Arrays::isKeyExists('host',$linkArray)){
                $sportLink = self::ROOT_URL.$sportLink;
            }
            $sportName = trim($sportLinkAnchor->getPlainText());

            $link = AngebotLink::create($sportName,$sportLink);
            $angebotLinks->add($link);
        }
        return $angebotLinks;

    }

    public function parseSport($url){
        $urlTmp = explode('/',$url);
        $phpFile = $urlTmp[count($urlTmp)-1];
        $urlRoot = str_replace($phpFile,'',$url);

        $sport = new Sport();

        $sport->setSportURL($url);

        $html = file_get_dom($url);
        $descriptions = $html->select('div[id=contentDienste, class=contentDienste]');

        foreach ($descriptions as $description){
            $urlMatches = $description->select('a');
            foreach ($urlMatches as $url){
                $urlArray = parse_url($url->href);
                if (!Arrays::isKeyExists('host',$urlArray) && !Arrays::isKeyExists('scheme',$urlArray)){
                    $url->href = $urlRoot.$urlArray['path'];
                }
            }
            $imageMatches = $description->select('img');
            foreach ($imageMatches as $url){
                $urlArray = parse_url($url->src);
                if (!Arrays::isKeyExists('host',$urlArray) && !Arrays::isKeyExists('scheme',$urlArray)){
                    $url->src = $urlRoot.$urlArray['path'];
                }
            }
        }

        if (count($descriptions) > 0){
            /**
             * Setting sport name
             */
            $sportNameMatches = $descriptions[0]->select('h1');
            if (count($sportNameMatches) > 0){
                $sportName = $sportNameMatches[0]->getPlainTextUTF8();
                $sport->setSportName($sportName);
            }
            /**
             * Finding and parsing images
             */
            $imageMatches = $descriptions[0]->select('img');
            $imageURL = null;
            if (count($imageMatches) > 0){
                $sport->setSportImage($imageMatches[0]->src);
            }
            /**
             * Setting sport header description
             */

            $sport->setSportDescriptionHeader($descriptions[0]->htmlUTF8());
        }

        $matchedTables = $html->select('div.largebox > table');

        /* there is only one table per page */
        $table = $matchedTables[0];

        $sport->addEvent($this->parseSportTable($table)->getAll());

        return $sport;
    }

    private function parseSportTable($table){
        $events = new ArrayList();
        if (is_null($table)) return $events;

        $rows = $table->select('tr');
        $index = 0;
        foreach ($rows as $eventRow){
            if ($index == 0){
                $index++;
                continue;
            }
            $events->addAll($this->parseEventRow($eventRow)->getAll());
            $index++;
        }
        return $events;
    }

    private function parseEventRow($row){
        $events = new ArrayList();
        if (is_null($row)) return $events;

        $eventParams = $row->select('td > p');
        if (count($eventParams) === 0) return;

        $day = trim($eventParams[0]->getPlainText());
        $time = trim($eventParams[1]->getPlainTextUTF8());
        $periods = trim($eventParams[2]->getPlainText());

        $name = trim($eventParams[3]->getPlainTextUTF8());
        $place = trim($eventParams[4]->getPlainTextUTF8());
        $kew = trim($eventParams[7]->getPlainText());

        /* check if info link exists */
        $infoLink = trim($eventParams[5]->getPlainText());
        if (String::length($infoLink) > 0){
            $infoLinkMatches = $eventParams[5]->select('a');
            $infoLink = trim($infoLinkMatches[0]->href);
        }

        $registration = trim($eventParams[6]->getPlainText());
        $registrationLink = '';
        if (String::length($registration) > 0){
            $registrationLinkMatches = $eventParams[6]->select('a');
            if (count($registrationLinkMatches) > 0){
                $registrationLink = trim($registrationLinkMatches[0]->href);
            }
        }

        $timeParser = new TimeParser($time);
        $intervals = $timeParser->getIntervals()->getAll();
        $placeParser = new PlaceParser($place);
        $placeArray = $placeParser->getPlaces();
        $kewParser = new KewParser($kew);
        $daysOfWeekParser = new DaysOfWeekParser($day);
        $periodParser = new PeriodParser($periods);


        $index = 0;

        foreach($intervals as $interval){
            $event = new Event();
            $event->setInterval($interval);
            if ($placeArray->size() > 0){
                if ($placeArray->size() > $index) $event->setPlace($placeArray->get($index));
                else $event->setPlace($placeArray->get($placeArray->size()-1));
            }

            if ($periodParser->isDate()) {
                $event->setDate($periodParser->getDate());
            }
            else if ($periodParser->isPeriods()) {
                $event->setPeriods($periodParser->getPeriods());
            }

            $event->setKew($kewParser->getKew());
            $event->setDaysOfWeek($daysOfWeekParser->getDaysOfWeek());

            $event->setEventName($name);

            $event->setInfoLink($infoLink);
            $event->setRegistration($registration);
            $event->setRegistrationLink($registrationLink);

            $events->add($event);
            $index++;
        }

        return $events;
    }
}