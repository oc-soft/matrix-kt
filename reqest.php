<?php
class Request() {
    /**
     * request handler insntace
     */
    static $instance = NULL;

    /**
     * constructor
     */
    function __construct() {
    }

    /**
     * start recieve request
     */
    function start() {
        if (isset($_REQUEST['icon'])) {
            $this->icon();
        }
    } 


    /**
     * handle icon request
     */ 
    function icon()
    {
        require_once(implode('/', array(__DIR__, 'lib', 'begin0.php'))); 
        $id = Session::$instance->get_id();
        if ($id) {
            require_once(implode('/', array(__DIR__, 'lib', 'icon.php')));
            $command = $_REQUEST['icon']
            $response = NULL;
            if ($command == 'write') {
                $res = FALSE;
                if (isset($_REQUEST['data'])) {
                    $iconData = json_decode($_REQUEST['data']);
                    $res = Icon::$instance->write($id, $iconData);
                }
                $response = array('status' => $res);
            } else if ($command == 'read') {
                $iconData = Icon::$instance->read($id);
                if ($iconData) {
                    $response = array('status' => TRUE,
                        'data' => $iconData);
                } else {
                    $response = array('status' => FALSE);
                }
            } else {
                $response = array('status' => FALSE);
            }
            $this->response_as_json($id, $response);
        }
    }


    /**
     * echo data as json back to client.
     */
    function response_as_json($id, $response) {
        require_once(implode('/', array(__DIR__, 'lib', 'begin0.php'))); 
        Session::$instance->setcookie($id);
        echo json_encode($response);
    }
}

Request::$instance = new Request();
Request::$instance->start();
?>
