<?php 
/**
 * Game main page.
 */
require_once implode('/', array(__DIR__, 'lib', 'begin0.php')); 
require_once implode('/', array(__DIR__, 'i18n.php'));
require_once implode('/', array(__DIR__, 'lib', 'icon.php'));
require_once implode('/', array(__DIR__, 'lib', 'version.php'));
Session::$instance->start();
Session::$instance->update_session();
$sid = Session::$instance->get_id();
require 'config.php';
$texture_text = file(
    'TextureText.txt', 
    FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES
);
$mswp_settings['ui'] = array(
  'icon_list' => json_decode(
    file_get_contents(
      implode('/', array(__DIR__, 'specs', 'icon_list.js')),
      FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES
    ), TRUE));
$mswp_settings['textureText'] = implode('', $texture_text);

i18n_bind_textdomain();

$flag_icon_class = 'fas fa-flag';
$flag_icon = Icon::$instance->get_flag_icon(
    Icon::$instance->read_as_array($sid));
if ($flag_icon) {
    $flag_icon_class = sprintf('%s fa-%s', 
        $flag_icon['prefix'], 
        $flag_icon['iconName']);

}
$flag_count = 6;

require implode('/', array(__DIR__, 'template', 'entry.php'));
// vi: se ts=4 sw=4 et:
