<?php
require_once implode('/', array(__DIR__, 'lib', 'begin0.php'));
require_once implode('/', array(__DIR__, 'lib', 'color_scheme.php'));
require_once implode('/', array(__DIR__, 'lib', 'color.php'));

Session::$instance->start();

$sid = Session::$instance->get_id();

$color_scheme = ColorScheme::$instance->get_color_scheme($sid);

$version_keys = array_keys($color_scheme);

$color_scheme = $color_scheme[end($version_keys)];

$env_colors = array(
    new Color($color_scheme['environment'][0]),
    new Color($color_scheme['environment'][1]));

require 'template/entry-less.php';
// vi: se ts=4 sw=4 et:
