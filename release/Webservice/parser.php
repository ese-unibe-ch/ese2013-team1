<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 25.11.13
 * Time: 9:27
 */

require_once "lib/all.php";

Activity::create();

$parser = new Parser();
$parser->parse();


Activity::destroy();