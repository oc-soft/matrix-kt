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

$db_select_expired_records = array(
    'session' => array(
        'query' => 'SELECT ID FROM %ssession '
        . 'WHERE ACCESS < NOW() - INTERVAL ? SECOND',
        'params' => array('i')
    )
);

$db_remove_expired_records = array(
    'icon' => 'DELETE FROM %sicon WHERE ID in (%s)',
    'session' => 'DELETE FROM %ssession WHERE ID in (%s)'
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


$using_mapping = array(
	'polyfill' => '<script src="https://polyfill.io/v3/polyfill.min.js'
		. '?features=es6">'
		. '</script>',
	'mathjax' => '<script id="MathJax-script" async '
		. 'src="https://cdn.jsdelivr.net/npm/mathjax@3/'
		. 'es5/tex-mml-chtml.js"></script>',
	'fontawesome' => '<script src="https://kit.fontawesome.com/a7443310ec.js">'
		. '</script>'

);
?>
