<?php
require_once(implode('/', array(dirname(__DIR__), 'config.php'))); 
require_once(implode('/', array(dirname(__DIR__), 'site', 'config.php'))); 
require_once(implode('/', array(__DIR__, 'session.php')));  

/**
 * handle very early processing phase.
 */
class Begin0 {
    /**
     * constructor
     */
    function __construct() {
    }

    /**
     * do the jobs
     */
    function run() {
    }
}
/**
 * do initial processing
 */
(new Begin0())->run();
?>
