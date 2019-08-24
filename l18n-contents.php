<?php

/**
 * get file contents accoding with localization.
 */
function l18n_contents_get_by_file($accept_lang, $dir, $file, $ext) {
	$contents = NULL;		
	$saved_error_level = error_reporting(0);	

	l18n_contents_iterate($accept_lang, 
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

/**
 * iterate accept languages higher request order.
 */
function l18n_contents_iterate($accept_lang, $iter) {
	if (isset($accept_lang)) {
		$elems = explode(',', $accept_lang);
		$q_lang_map = array('1' => array());
		foreach ($elems as $elem) {
			$lang_q = explode(';', $elem);
			if (count($lang_q) > 1) {
				preg_match('/q=(.+)/', $lang_q[1], $matches);	
				if (count($matches) > 1) {
					$q_val = (float)$matches[1];
				} else {
					$q_val = 0.0;
				}
				if (!isset($q_lang_map[(string)$q_val])) {
					$q_lang_map[(string)$q_val] = array();
				}	
				$langs = $q_lang_map[(string)$q_val];
			} else {
				$q_val = 1.0;
				$langs = $q_lang_map[(string)$q_val];		
			}
			
			$langs[] = $lang_q[0];
			$q_lang_map[(string)$q_val] = $langs;
		}
		krsort($q_lang_map);	
		foreach($q_lang_map as $key0 => $langs) {
			foreach($langs as $key1 => $lang) {
				$do_stop = call_user_func($iter, $lang);
				if ($do_stop) {
					break;
				}
			}
			if (isset($do_stop) && $do_stop) {
				break;
			}
		}
	}	
}


?>
