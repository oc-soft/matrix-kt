<?php

require_once implode('/', array(dirname(__DIR__), 'site', 'config.php'));
require_once implode('/', array(__DIR__, 'db.php'));

/**
 * icon handler
 */
class Icon {
    static $instance = NULL;
    /**
     * constructor
     */
    function __construct() {
    }

    /**
     * get database object
     */
    function get_db() {
        return DB::$instance;
    }

    /**
     * save icon data
     */
    function write($id, $data) {
        global $db_icon_manipulation;
        global $db_settings;
        $data = $this->get_necessary_data($data);
        $result = FALSE;
        if ($data) {
            $stmt_str = sprintf($db_icon_manipulation['write']['query'],
                $db_settings['prefix']);
            $stmt = $this->get_db()->get_sql_client()->prepare($stmt_str);     
            $str_data = json_encode($data);
            $stmt->bind_param($db_icon_manipulation['write']['params'][0], 
                $id, $str_data);
            $result = $stmt->execute();
            $stmt->close();
        }
        return $result;
    }
    /**
     * read icon data from data-base
     */
    function read($id) {
        global $db_icon_manipulation;
        global $db_settings;
        $result = FALSE;
        $stmt_str = sprintf($db_icon_manipulation['read']['query'],
            $db_settings['prefix']);
        $stmt = $this->get_db()->get_sql_client()->prepare($stmt_str);
        $stmt->bind_param($db_icon_manipulation['read']['params'][0], 
            $id);
        $stmt->execute();
        $result = NULL;
        $stmt->bind_result($result);
        $stmt->fetch();
        $stmt->close();
        return $result;
    }

    /**
     * filter only you wanna save 
     */
    function get_necessary_data($data) {
        $specs_str = file_get_contents(
            implode('/', array(dirname(__DIR__), 'specs', 'config.js')));
        $specs = json_decode($specs_str, TRUE);
        $result = NULL;
        foreach ($specs['iconData'] as $item) {
            $new_data = array();
            $contains_all = TRUE;
            foreach ($item as $key => $value) {
                $contains_all = isset($data[$key]);
                if ($contains_all) {
                    $new_data[$key] = $data[$key];
                } else {
                    break;
                }
            }
            if ($contains_all) {
                $result = $new_data;
                break;
            } 
        }
        return $result;
    }
}

Icon::$instance = new Icon();

?>
