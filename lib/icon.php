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
        $version = "0";
        $icons_0 = $data;
        if (isset($data['version']) && isset($data['icons'])) {
            $version = $data['version']; 
            $icons_0 = $data['icons'];
        }
        $data_template = NULL;
        if (isset($specs['iconData'][$version])) {
            $data_template = $specs['iconData'][$version];
        }
        $result = NULL;
        if ($data_template) {
            $icons_1 = $this->get_necessary_data_i($data_template, $icons_0);
            if ($icons_1) {
                if ($version != "0") {
                    $result = array('version' => $version,
                        'icons' => $icons_1);
                } else {
                    $result = $icons_1;
                }
            } 
        }
        return $result;
    }
    /**
     * filter only you wanna save 
     */
    function get_necessary_data_i($data_template, $data) {
        $result = NULL;

        $new_data = array();
        $contains_all = TRUE;
        foreach ($data_template as $key0 => $value0) {
            $contains_all = isset($data[$key0]);
            if ($contains_all) {
                $new_data[$key0] = $data[$key0];
            } else {
                break;
            }
        }
        if ($contains_all) {
            $result = $new_data;
        } 
        return $result;
    }
}

Icon::$instance = new Icon();

?>
