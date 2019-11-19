<?php
require_once implode('/', array(__DIR__, 'db.php'));

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
     *  get database object
     */
    function get_db() {
        return DB::$instance;
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

    /**
     * update session
     */
    function update_session() {
        $this->setcookie($this->get_id());
        $this->update($this->get_id());
    }
    /**
     * update session in db 
     */
    function update($id) {
        global $db_access_update;
        global $db_settings;
        $result = FALSE;
        if ($id) {
            $stmt_str = sprintf($db_access_update['update']['query'], 
                $db_settings['prefix']); 
            $stmt = $this->get_db()->get_sql_client()->prepare($stmt_str);
            $stmt->bind_param($db_access_update['update']['params'][0],
               $id); 
            $stmt->execute();
            $stmt->close();
            $result = TRUE;
        } 
        return $result;
    }

}

Session::$instance = new Session();

?>
