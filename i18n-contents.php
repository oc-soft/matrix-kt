<?php
require_once implode('/', array(__DIR__, 'i18n.php'));

/**
 * get file contents accoding with localization.
 */
function i18n_contents_get_by_file($accept_lang, $dir, $file, $ext) {
	$contents = NULL;		
	$saved_error_level = error_reporting(0);	

	i18n_iterate_accept_languages($accept_lang, 
		function($lang) use($dir, $file, $ext, &$contents) {
		$file_path = implode('/', array($dir, $file . '-' . $lang . $ext));
		$f_contents = file_get_contents($file_path);
		if ($f_contents) {
			$contents = $f_contents;
			$result = true;
		} else {
			$result = false;
		}
		return result;
	});
	error_reporting($saved_error_level);

	if (!isset($contents)) {
		$file_path = implode('/', array($dir, $file . $ext));
		$contents = file_get_contents($file_path);
	}
	$result = $contents;
	return $result;
}
/* vi: se ts=4 sw=4 et: */
?>
