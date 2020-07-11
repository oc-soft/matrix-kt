<?php
require_once implode('/', array(dirname(__DIR__), 'site', 'config.php'));
require_once implode('/', array(__DIR__, 'db.php'));
require_once implode('/', array(__DIR__, 'data_format.php'));

/**
 * point light handler
 */
class PointLight {
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
        global $db_point_light_manipulation;
        global $db_settings;
        $data = $this->get_necessary_data($data);
        $result = FALSE;
        if ($data) {
            $stmt_str = sprintf(
                $db_point_light_manipulation['write']['query'],
                $db_settings['prefix']);
            $stmt = $this->get_db()->get_sql_client()->prepare($stmt_str);     
            $saved_precision = ini_get('serialize_precision');
            if ($saved_precision) {
                ini_set('serialize_precision', 6);
            }
            $str_data = json_encode($data);
            if ($saved_precision) {
                ini_set('serialize_precision', $saved_precision);
            }
 
            $stmt->bind_param(
                $db_point_light_manipulation['write']['params'][0], 
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
        global $db_point_light_manipulation;
        global $db_settings;
        $result = FALSE;
        $stmt_str = sprintf(
            $db_point_light_manipulation['read']['query'],
            $db_settings['prefix']);
        $stmt = $this->get_db()->get_sql_client()->prepare($stmt_str);
        $stmt->bind_param(
            $db_point_light_manipulation['read']['params'][0], 
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
        $point_light_0 = $data;
        $data_template = $specs['pointLight'][$version];
        $result = NULL;
        if ($data_template) {
            $point_light_1 = $this->get_necessary_data_i(
                $data_template, $point_light_0);
            if ($point_light_1) {
                $result = $point_light_1;
            } 
        }
        return $result;
    }
    /**
     * filter only you wanna save 
     */
    function get_necessary_data_i($data_template, $data) {
        $result = array();
        foreach ($data_template['template'] as $key0 => $value0) {
            $value1 = NULL;
            if (isset($data[$key0])) {
                if (isset($data_template['format'][$key0])) {
                    $format = $data_template['format'][$key0];
                    $value1 = $this->format($format, $data[$key0]);
                } else {
                    $value1 = $data[$key0];
                }
            }
            if ($value1) {
                $result[$key0] = $data[$key0];
            } else {
                $result = NULL;
                break;
            }
        }

        return $result;
    }
    /**
     * format value
     */
    function format($format, $data) {
        return DataFormat::$instance->format($format, $data);
    }
}

PointLight::$instance = new PointLight();

// vi: se ts=4 sw=4 et:
