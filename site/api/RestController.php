<?php
    require_once(__DIR__."/handlers/ContactGroupMemberRestHandler.php");
    require_once(__DIR__."/handlers/ContactGroupRestHandler.php");
    require_once(__DIR__."/handlers/ContactRequestRestHandler.php");
    require_once(__DIR__."/handlers/ContactRestHandler.php");
    require_once(__DIR__."/handlers/TransactionRestHandler.php");
    require_once(__DIR__."/handlers/UserRestHandler.php");

    $view = "";
    if(isset($_GET["view"]))
        $view = $_GET["view"];
    
    $type = "";
    if(isset($_GET["type"]))
        $type = $_GET["type"];
    
    /*
    controls the RESTful services
    URL mapping
    */
    switch($type)
    {
        case "contact_group_member":
            $contactGroupMemberRestHandler = new ContactGroupMemberRestHandler();
            switch($view)
            {
                case "memberlist":
                    $contactGroupMemberRestHandler->memberList($_GET["id"]);
                    break;
                case "add":
                    $contactGroupMemberRestHandler->add($_GET["id"], $_GET["user"]);
                    break;
                case "remove":
                    $contactGroupMemberRestHandler->remove($_GET["id"], $_GET["user"]);
                    break;
                case "":
                    break;
            }
            break;
        case "contact_group":
            $contactGroupRestHandler = new ContactGroupRestHandler();
            switch($view)
            {
                case "grouplist":
                    $contactGroupRestHandler->groupList($_GET["user"]);
                    break;
                case "group":
                    $contactGroupRestHandler->group($_GET["id"]);
                    break;
                case "add":
                    $contactGroupRestHandler->add($_GET["info"]);
                    break;
                case "delete":
                    $contactGroupRestHandler->delete($_GET["id"]);
                    break;
                case "":
                    break;
            }
            break;
        case "contact_request":
            $contactRequestRestHandler = new ContactRequestRestHandler();
            switch($view)
            {
                case "pending":
                    $contactRequestRestHandler->pending($_GET["user"]);
                    break;
                case "pendingfrom":
                    $contactRequestRestHandler->pendingFromuser($_GET["user"]);
                    break;
                case "add":
                    $contactRequestRestHandler->add($_GET["user"], $_GET["contact"]);
                    break;
                case "accept":
                    $contactRequestRestHandler->accept($_GET["user"], $_GET["contact"]);
                    break;
                case "deny":
                    $contactRequestRestHandler->deny($_GET["user"], $_GET["contact"]);
                    break;
                case "":
                    break;
            }
            break;
        case "contact":
            $contactRestHandler = new ContactRestHandler();
            switch($view)
            {
                case "all":
                    $contactRestHandler->id($_GET["id"]);
                    break;
                case "delete":
                    $contactRestHandler->delete($_GET["user"], $_GET["contact"]);
                    break;
                case "":
                    break;
            }
            break;
        case "transaction":
            $transactionRestHandler = new TransactionRestHandler();
            switch($view)
            {
                case "id":
                    $transactionRestHandler->id($_GET["id"]);
                    break;
                case "debtor":
                    $transactionRestHandler->debtor($_GET["id"]);
                    break;
                case "creditor":
                    $transactionRestHandler->creditor($_GET["id"]);
                    break;
                case "add":
                    $transactionRestHandler->add($_GET["info"]);
                    break;
                case "balances":
                    $transactionRestHandler->computeBalances($_GET["id"]);
                case "":
                    break;
            }
            break;
        case "user":
            $userRestHandler = new UserRestHandler();
            switch($view)
            {
                case "login":
                    $userRestHandler->login($_GET["email"], $_GET["pass"]);
                    break;
                case "id":
                    $userRestHandler->getUserById($_GET["id"]);
                    break;
                case "email":
                    $userRestHandler->getUserByEmail($_GET["email"]);
                    break;
                case "add":
                    $userRestHandler->add($_GET["info"]);
                    break;
                case "update":
                    $userRestHandler->update($_GET["info"]);
                    break;
                case "delete":
                    $userRestHandler->delete($_GET["id"]);
                    break;
                case "":
                    break;
            }
            break;
        case "":
            break;
    }
?>