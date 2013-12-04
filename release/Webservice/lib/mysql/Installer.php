<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 26.11.13
 * Time: 18:23
 */

class Installer {

    private $tables;

    public function __construct(){
        $this->tables = new ArrayList();

        $this->tables->add(new IntervalDB());
        $this->tables->add(new PlaceDB());

        $this->tables->add(new SportsDB());
        $this->tables->add(new EventsDB());
        $this->tables->add(new EventKEW());
        $this->tables->add(new EventPeriods());
        $this->tables->add(new EventDaysOfWeek());
        $this->tables->add(new SportEvents());
        $this->tables->add(new Installations());
        $this->tables->add(new Users());
        $this->tables->add(new Attended());
        $this->tables->add(new Friends());
        $this->tables->add(new Rating());
    }

    public function drop(){
        $length = $this->tables->size();
        for($i = $length - 1; $i >= 0; $i--){
            $this->tables->get($i)->drop();
        }
    }

    public function create(){
        foreach ($this->tables->getAll() as $table){
            $table->create();
        }
    }
} 