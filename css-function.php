<?php
require_once implode('/', array(__DIR__, 'lib', 'color-scheme-response.php'));

/**
 *  get css contents
 */
function css_get_contents($css_name) {
    $result = implode('/', [__DIR__, 'template', $css_name . '.php']);
    return $result;
}

// vi: se ts=4 sw=4 et:
