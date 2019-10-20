<?php

/**
 * database class
 */
class DB {
    /**
     * instance
     */
    static $instance = NULL;

    private $sql_client = NULL;
    /**
     * constructor
     */
    function __construct() {
    } 
    
    /**
     * get sql client
     */
    function get_sql_client() {
        if (!isset($this->sql_client)) {
            global $db_settings;
            if (isset($db_settings['port'])) {
                $port = $db_settings['port'];
            } else {
                $port = ini_get('mysqli.default_port');
            }
            if (isset($db_settings['socket'])) {
                $socket = $db_settings['socket'];
            } else {
                $socket = ini_get('mysqli.default_socket');
            }
            $this->sql_client = new mysqli($db_settings['host'],
                $db_settings['user'], $db_settings['password'],
                $db_settings['db'],
                $port, $socket);
        }
        return $this->sql_client;
    }
    
    /**
     * create tables
     */
    function create_tables() {
        $client = $this->get_sql_client();
        global $db_settings;
        $prefix = $db_settings['prefix'];
        global $db_table_creation;
        foreach ($db_table_creation as $key => $value) {
            $query = sprintf($value, $prefix);
            $client->query($query);
            var_dump($client->error);
        }
    }
}

DB::$instance = new DB();

?>
