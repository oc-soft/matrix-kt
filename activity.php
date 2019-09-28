<?php
require_once implode('/', array(__DIR__, 'lib', 'begin0.php')); 

/**
 * manage user activity
 */
class Activity {
    static $instance = NULL; 
    /**
     *  constructor
     */
    function __construct() {
    }


    /**
     * manage activity
     */
    function run() {
        $status = $this->record();
        $result = array(
            'status' => $status
        ); 
        echo json_encode($result);
    }

    /**
     * record activity
     */
    function record() {
        Session::$instance->start();
        $result = Session::$instance->update_session();
        return $result;
    }
}

Activity::$instance = new Activity();
Activity::$instance->run();
?>
