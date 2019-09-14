<?php

require_once('config.php');
require_once('site/config.php');
require_once('lib/db.php');


$db = new DB();
$db->create_tables();

?>
