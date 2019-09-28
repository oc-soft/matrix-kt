<?php

$db_table_creation = array(
    'session' => 'CREATE TABLE IF NOT EXISTS %ssession '
        . '('
        . 'ID CHAR(23),'
        . 'ACCESS TIMESTAMP,'
        . 'PRIMARY KEY (ID)'
        . ')',
    'icon' => 'CREATE TABLE IF NOT EXISTS %sicon '
        . '('
        . 'ID CHAR(23),'
        . 'DATA CHAR(255),'
        . 'PRIMARY KEY (ID)'
        . ')'
);

$db_access_update = array(
    'update' => array(
        'query' => 'INSERT INTO %ssession (ID) VALUES(?) '
        . 'ON DUPLICATE KEY UPDATE ACCESS = NULL',
        'params' => array('s')
    )
);
$db_icon_manipulation = array(
    'write' => array(
        'query' => 'INSERT INTO %sicon (ID,DATA) VALUES(?,?) '
            . 'ON DUPLICATE KEY UPDATE DATA = VALUES(DATA)',
        'params'=> array('ss')
    ),
    'read' => array(
        'query' => 'SELECT DATA FROM %sicon WHERE ID = ?',
        'params' => array('s')
    )
);
?>
