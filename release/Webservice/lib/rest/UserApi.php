<?php
/**
 * Created by PhpStorm.
 * User: Aliaksei Syrel
 * Date: 07.12.13
 * Time: 19:16
 */

require_once "./lib/all.php";

class RegisterUser {
    const ACTION = "registerUser";
    const UUID = "uuid";
    const NICKNAME = "nickname";
    const PASSWORD = "password";

    private $uuid;
    private $nickname;
    private $password;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->nickname = $_GET[self::NICKNAME];
        $this->password = $_GET[self::PASSWORD];
        if(String::length($this->uuid) === 0 || String::length($this->nickname) === 0 || String::length($this->password) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $installations = new Installations();
        $users = new Users();
        if (String::length($this->password) < 5) ActionsHandler::error("Password length should be > 4");
        if (String::length($this->nickname) < 2) ActionsHandler::error("Nickname length should be > 1");
        if (String::length($this->nickname) === "null" || $this->nickname === "NULL") ActionsHandler::error("Nickname can't be 'null' or 'NULL'");
        if (!$installations->isRegistered($this->uuid)) ActionsHandler::error("Unknown uuid");
        if ($users->isExists($this->nickname)) ActionsHandler::error("User with this nickname exists");

        $id = $users->add($this->uuid,$this->nickname,$this->password);
        if ($id === -1) ActionsHandler::error("You already registered");
        if ($id === -2) ActionsHandler::error("User with this nickname exists");

        ActionsHandler::success($id);
    }
}

class LoginUser {
    const ACTION = "loginUser";

    const NICKNAME = "nickname";
    const PASSWORD = "password";

    private $nickname;
    private $password;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->nickname = $_GET[self::NICKNAME];
        $this->password = $_GET[self::PASSWORD];
        if(String::length(String::length($this->nickname) === 0 || String::length($this->password) === 0)) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        $users = new Users();

        if (!$users->isExists($this->nickname)) ActionsHandler::error("User with this nickname doesn't exist");

        $data = $users->getUserLoginData($this->nickname,$this->password);

        if (count($data) == 0) ActionsHandler::error("Error login");

        $uuid = $data[Users::UUID];
        $id = $data[Users::ID];
        $nickname = $data[Users::NICKNAME];
        $username = $data[Users::USERNAME];

        ActionsHandler::success(array($id,$uuid,$nickname,$username));
    }
}

class SearchForNewFriends {
    const ACTION = "findNewFriends";

    const UUID = "uuid";
    const USERID = "userID";
    const QUERY = "query";

    private $uuid;
    private $userid;
    private $query;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->query = $_GET[self::QUERY];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->query) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        if (String::length($this->query) <= 1) ActionsHandler::error("[".self::ACTION."] query length should be > 1");
        $users = new Users();

        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");

        $data = $users->search($this->userid,$this->query);
        exit(trim(json_encode(array("users"=>$data)),'"'));
    }
}

class SendFriendRequest {
    const ACTION = "sendFriendRequest";

    const UUID = "uuid";
    const USERID = "userID";            // request receiver
    const FRIENDID = "friendID";        // request sender

    private $uuid;
    private $userid;
    private $friendid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->friendid = $_GET[self::FRIENDID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->friendid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid) || !Int::isInt($this->friendid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friendRequests = new FriendRequests();
        $friends = new FriendsDB();

        if (!$users->isUserExists($this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$users->isRegistered($this->uuid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if ($friendRequests->isExists($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] request exists");
        if ($friends->isFriends($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] already friends");

        $friendRequests->add($this->userid,$this->friendid);
        ActionsHandler::success("Request sent");
    }
}

class AcceptFriendRequest {
    const ACTION = "acceptFriendRequest";

    const UUID = "uuid";
    const USERID = "userID";            // request receiver
    const FRIENDID = "friendID";        // request sender

    private $uuid;
    private $userid;
    private $friendid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->friendid = $_GET[self::FRIENDID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->friendid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid) || !Int::isInt($this->friendid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friendRequests = new FriendRequests();
        $friends = new FriendsDB();

        if (!$users->isUserExists($this->friendid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$friendRequests->isExists($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] request doesn't exist");
        if ($friends->isFriends($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] already friends");

        $friends->add($this->userid,$this->friendid);
        $friendRequests->removeRequest($this->userid,$this->friendid);
        ActionsHandler::success("Request accepted");
    }
}

class CancelFriendRequest {
    const ACTION = "cancelFriendRequest";

    const UUID = "uuid";
    const USERID = "userID";            // request receiver
    const FRIENDID = "friendID";        // request sender

    private $uuid;
    private $userid;
    private $friendid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->friendid = $_GET[self::FRIENDID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->friendid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid) || !Int::isInt($this->friendid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friendRequests = new FriendRequests();
        $friends = new FriendsDB();

        if (!$users->isUserExists($this->friendid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$friendRequests->isExists($this->userid,$this->friendid)) ActionsHandler::success("Request canceled");
        if ($friends->isFriends($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] already friends");

        $friendRequests->removeRequest($this->userid,$this->friendid);
        ActionsHandler::success("Request canceled");
    }
}

class GetFriendRequests {
    const ACTION = "getFriendRequests";

    const UUID = "uuid";
    const USERID = "userID";

    private $uuid;
    private $userid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friendRequests = new FriendRequests();

        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");

        $data = $friendRequests->getRequestsTo($this->userid);
        exit(trim(json_encode(array("users"=>$data)),'"'));
    }
}

class GetUserData {
    const ACTION = "getUserData";

    const UUID = "uuid";
    const USERID = "userID";

    private $uuid;
    private $userid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friendRequests = new FriendRequests();
        $friends = new FriendsDB();

        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");

        $requestsToMe = $friendRequests->getRequestsTo($this->userid);
        $myRequests = $friendRequests->getMyRequests($this->userid);
        $myFriends = $friends->getFriends($this->userid);
        $userData = $users->getData($this->userid);
        $userID = $userData[Users::ID];
        $nickname = $userData[Users::NICKNAME];
        $username = $userData[Users::USERNAME];
        $picture = $userData[Users::PICTURE];

        $news = $users->getNews($this->userid);
        exit(trim(json_encode(array("userID"=>$userID,"picture"=>$picture,"username"=>$username,"nickname"=>$nickname,"myFriendRequests"=>$myRequests,"myFriends"=>$myFriends,"friendRequestsToMe"=>$requestsToMe,"news"=>$news)),'"'));
    }
}

class GetFriendData {
    const ACTION = "getFriendData";

    const UUID = "uuid";
    const USERID = "userID";
    const FRIENDID = "friendID";
    const DATE = "date";

    private $uuid;
    private $userid;
    private $friendid;
    private $date;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->friendid = $_GET[self::FRIENDID];
        $this->date = $_GET[self::DATE];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->friendid) === 0 || String::length($this->date) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid) || !Int::isInt($this->friendid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        $friends = new FriendsDB();

        if (!$users->isUserExists($this->friendid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");
        if (!$friends->isFriends($this->userid,$this->friendid)) ActionsHandler::error("[".self::ACTION."] not friends");
        if (!Int::isInt($this->date)) ActionsHandler::error("[isAttended] wrong date! should be > 0");

        $userData = $users->getData($this->friendid);
        $attendedEvents = $users->getAttendedEvents($this->friendid,$this->date);

        $eventsArray = new ArrayList();
        foreach($attendedEvents as $attended){
            $event = Event::createByHash($attended[Attended::HASH]);
            $event->setAttendedDate($attended[Attended::DATE]);
            $event->initSportName();
            $eventsArray->add($event->toJson());
        }

        $userID = $userData[Users::ID];
        $nickname = $userData[Users::NICKNAME];
        $username = $userData[Users::USERNAME];
        $picture = $userData[Users::PICTURE];

        $user = array("userID"=>$userID,"picture"=>$picture,"username"=>$username,"nickname"=>$nickname,"attendedEvents"=>$eventsArray->getAll());

        exit(trim(json_encode($user),'"'));
    }
}

class GetFriendNews {
    const ACTION = "getFriendNews";

    const UUID = "uuid";
    const USERID = "userID";

    private $uuid;
    private $userid;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();
        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");

        $news = $users->getNews($this->userid);

        exit(trim(json_encode(array("news"=>$news)),'"'));
    }
}

class SetUsername {
    const ACTION = "setUsername";

    const UUID = "uuid";
    const USERID = "userID";
    const USERNAME = "username";

    private $uuid;
    private $userid;
    private $username;

    function __construct() {
        if ($_GET[ActionsHandler::ACTION] !== self::ACTION) ActionsHandler::error("[".self::ACTION."] wrong class error");
        $this->uuid = $_GET[self::UUID];
        $this->userid = $_GET[self::USERID];
        $this->username = $_GET[self::USERNAME];
        if(String::length($this->uuid) === 0 || String::length($this->userid) === 0 || String::length($this->username) === 0) ActionsHandler::error("[".self::ACTION."] not all parameters are specified");
        $this->init();
    }

    private function init(){
        if (!Int::isInt($this->userid)) ActionsHandler::error("[".self::ACTION."] user id should be integer");
        $users = new Users();

        if (!$users->isRegistered($this->uuid,$this->userid)) ActionsHandler::error("[".self::ACTION."] unknown user");

        $users->setUsername($this->userid,$this->username);

        ActionsHandler::success("Username changed");
    }
}
