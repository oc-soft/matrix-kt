<?php

/**
 * represent version 
 */
class Version {
    
    /**
     * parse version  
     */
    static function parse($str_version) {
        return explode('.', $str_version); 
    }
    /**
     * constructor 
     */
    function __construct(
        $version_src) {
        if (is_array($version_src)) {
            $this->version = $version_src;
        } else {
            $this->version = self::parse($version_src); 
        }
    }

    /**
     * compare version
     */
    function compare($other) {
        $ver_len = min(count($this->version), count($other->version));
        $result = 0;
        for ($i = 0; $i < $ver_len; $i++) {
            $result = $this->version[$i] - $other->version[$i];
            if ($result) {
                break;
            }
        }
        if (!$result) {
           $result = count($this->version) - count($other->version); 
        }
        return $result;
    }
}

// vi: se ts=4 sw=4 et:
