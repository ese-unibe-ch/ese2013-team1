<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 26.11.13
 * Time: 22:55
 */

class AjaxUpdate {
    const ACTION = "ajaxUpdate";

    function __construct() {
        if ($_POST[AjaxActionsHandler::ACTION] !== self::ACTION) AjaxActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->init();
    }

    private function init(){

        $updater = new Updater();
        $result = $updater->update();

        AjaxActionsHandler::success($result);
    }
}