<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 07.12.13
 * Time: 12:23
 */

require "lib/all.php";

Activity::create();

$places = new PlacesInserter();
$places->insert();

Activity::destroy();