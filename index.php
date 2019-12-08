<?php
	
if (count($_GET)) {
	if (isset($_GET['doc'])) {
		require('doc-function.php');
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
	}
}
if (!isset($contents)) {
	$contents = 'entry.php';
}

include($contents);		

?>
