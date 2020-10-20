<?php

/**
 *  get css contents
 */
function css_get_contents($css_name) {
    $result = NULL;
    if ('entry' ==  $css_name) {
        $result = implode('/', [__DIR__, 'entry-css.php']);
    }
    return $result;
}

function background_color($color_default) {
    global $color_scheme;
    $str_bg_color = $color_default;
    if (!is_null($color_scheme)) {
        $bg_color = $color_scheme['environment'][0];
        if (count($bg_color) > 3) {
            $str_bg_color = sprintf("rgba(%0, %1, %2, %3)",
                $bg_color[0], $bg_color[1], $bg_color[2],
                $bg_color[3] / 255);
        } else if (count($text_color) > 2) {
            $str_bg_color = sprintf("rgb(%0, %1, %2)",
                $bg_color[0], $bg_color[1], $bg_color[2]);
        }
    }
    echo $str_bg_color;
}


function text_color($color_default) {
    global $color_scheme;
    $str_text_color = $color_default;
    if (!is_null($color_scheme)) {
        $text_color = $color_scheme['environment'][1];
        if (count($text_color) > 3) {
            $str_text_color = sprintf("rgba(%0, %1, %2, %3)",
                $text_color[0], $text_color[1], $text_color[2],
                $text_color[3] / 255);
        } else if (count($text_color) > 2) {
            $str_text_color = sprintf("rgb(%0, %1, %2)",
                $text_color[0], $text_color[1], $text_color[2]);
        }
    }
    echo $str_text_color;
}

function text_color_fade($color_default, $fade_value) {
    global $color_scheme;
    $str_text_color = $color_default;
    if (!is_null($color_scheme)) {
        $text_color = $color_scheme['environment'][1];
        if (count($text_color) > 2) {
            $str_text_color = sprintf("rgba(%0, %1, %2, %3)",
                $text_color[0], $text_color[1], $text_color[2],
                $face_value / 100);
        }
    }
    echo $str_text_color;
}



// vi: se ts=4 sw=4 et:
