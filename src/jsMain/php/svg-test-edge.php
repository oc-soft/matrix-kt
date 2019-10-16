<?php
    $config_contents = file_get_contents("svg-test-config.js");
    require implode('/', array(__DIR__, 'svg-test.php'));
?>
