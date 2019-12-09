<?php 
/**
 * Game main page.
 */
require_once implode('/', array(__DIR__, 'lib', 'begin0.php')); 
require_once implode('/', array(__DIR__, 'i18n.php'));
Session::$instance->start();
Session::$instance->update_session();
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

require implode('/', array(__DIR__, 'template', 'entry.php'));
?>

