<?php
    $config_contents = file_get_contents("svg-test-config.js");
    $config = json_decode($config_contents, TRUE);
    $config['debugInChrome'] = TRUE;
    $config_contents = json_encode($config);
    require implode('/', array(__DIR__, 'svg-test.php'));
?>
