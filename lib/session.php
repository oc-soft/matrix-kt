<?php

/**
 * handle session
 */
class Session {
    /**
     * the session instance
     */
    static $instance = NULL;
    
    /**
     * constructor
     */
    function __construct() {
    }


    /**
     * you get true if this session has id(db key).
     */
    function is_having_id() {
        global $session_setting; 
        $result = isset($_COOKIE[$session_setting['cookieName']]);
        return $result;
    }

    /**
     * start session
     */
    function start() {
        if (!$this->is_having_id()) {
            global $session_setting;
            $this->session_id = uniqid();
            $this->setcookie($this->session_id); 
        }
    }

    /**
     * get session id
     */
    function get_id() {
        if (!$this->is_having_id()) {
            $result = $this->session_id;
        } else {
            global $session_setting;
            $result = $_COOKIE[$session_setting['cookieName']];
        }
        return $result;
    }

    /**
     * write cookie to response header
     */
    function setcookie($id) {
        global $session_setting;
        $option = $session_setting['option'];
        $option['expires'] += time();
        setcookie($session_setting['cookieName'], $id, $option['expires']);
    }
}

Session::$instance = new Session();

?>
