<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 27.11.13
 * Time: 13:10
 */

require "lib/all.php";

Activity::create();

header('Content-Type: application/json');
$unisport = new Unisport();
echo trim(json_encode($unisport->toJson()),'"');
Activity::destroy();