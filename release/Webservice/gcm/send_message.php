<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 10.12.13
 * Time: 0:27
 */

if (isset($_GET["regId"]) && isset($_GET["message"])) {
    $regId = $_GET["regId"];
    $message = $_GET["message"];

    include_once './GCM.php';

    $gcm = new GCM();

    $registration_ids = array($regId);
    $message = array("price" => $message);

    $result = $gcm->send_notification($registration_ids, $message);

    echo $result;
}