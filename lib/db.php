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
            if ($client->error) {
                var_dump($client->error);
            }
        }
    }
    /**
     * query expired record
     */
    function query_expired_records() {
    	$client = $this->get_sql_client();
        global $db_settings;
        $prefix = $db_settings['prefix'];
        global $db_select_expired_records; 
        $session_query = $db_select_expired_records['session'];
        global $session_setting;
        $query = sprintf(
            $session_query['query'], $prefix);
        $stmt = $client->prepare($query);
        $stmt->bind_param($session_query['params'][0],
            $session_setting['option']['expires']);

        $stmt->execute();

        $stmt->bind_result($res);
        $result = array();
        while ($stmt->fetch()) {
          $result[] = $res;
        }
        $stmt->close();

        return $result;
    }

    /**
     * remove expired records
     */
    function remove_expired_records() {
        $expired_ids = $this->query_expired_records();
        if (count($expired_ids)) {
            $expired_str_ids = array_map(function($item) {
                return sprintf('\'%s\'', $item);
            }, $expired_ids);
            $client = $this->get_sql_client();
            global $db_settings;
            $prefix = $db_settings['prefix'];
            global $db_remove_expired_records; 
            

            $expired_idstr = implode(',', $expired_str_ids);
            
            $queries = $db_remove_expired_records;
            foreach ($queries as $key => $query_tmp) {
                $query = sprintf($query_tmp, $prefix, $expired_idstr); 
                $client->query($query);
            }
        }
    }
}

DB::$instance = new DB();

?>
