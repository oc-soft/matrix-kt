<?php
	
function mswp_is_debug() {
	return isset($_GET['mswp_dbg']);
}
if (count($_GET)) {
	if (isset($_GET['doc'])) {
		require 'doc-function.php';
		if (isset($_GET['doc'])) {
			$doc_name = $_GET['doc'];
		}

		if (isset($_GET['lang'])) {
			$lang = $_GET['lang'];
		}
		$accept_lang = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
		if (isset($lang)) {
			$accept_lang = $lang . ',' . $accept_lang;	
        } else {
            $lang = NULL;
        }
		$doc_setting = doc_get_contents($accept_lang, $doc_name, $lang);
		if (isset($doc_setting) && count($doc_setting)) {
			$contents = 'doc-template.php';
		}
    } else if (isset($_GET['css'])) {
        require 'css-function.php';
	    $contents = css_get_contents($_GET['css']); 
    } else if (isset($_GET['less'])) {
        if ($_GET['less'] == 'entry') {
            $contents = 'entry-less.php';     
        }
    }
}
if (!isset($contents)) {
	$contents = 'entry.php';
}

include($contents);		

/* vi: se ts=4 sw=4 et: */
?>
