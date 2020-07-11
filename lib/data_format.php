<?php

class DataFormat {
    static $instance = NULL;

    /**
     * constructor
     */
    function __construct() {
    }

    /**
     * format data
     */
    function format($format, $data) {
        $result = NULL;
        if (isset($format['round'])) {
            $result = round($data, $format['round']);
        } else if (is_array($format) && is_array($data)) {
            $result = array();
            foreach ($format as $key => $value) {
                if (isset($data[$key])) {
                    $res = $this->format($value, $data[$key]); 
                    if ($res) {
                        $result[$key] = $res;
                    } else {
                        $result = NULL;
                        break;
                    } 
                }
            } 
        }

        return $result;
    }
}

DataFormat::$instance = new DataFormat();
// vi: se ts=4 sw=4 et:
