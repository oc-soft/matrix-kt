<?php

$path = pathinfo($_SERVER["SCRIPT_FILENAME"]);
if ($path['extension'] == 'wasm') {
    header("Content-Type: application/wasm");
    readfile($_SERVER["SCRIPT_FILENAME"]);
} else {
    return FALSE;
}

/* vi: se ts=4 sw=4 et: */
