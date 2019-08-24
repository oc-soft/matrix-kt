<?php
	
if (count($_GET)) {
	if (isset($_GET['doc'])) {
		require('doc-function.php');
		$doc_name = $_GET['doc'];
		$lang = $_GET['lang'];
		$accept_lang = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
		if (isset($lang)) {
			$accept_lang = $lang . ',' . $accept_lang;	
		}
		$doc_setting = doc_get_contents($accept_lang, $doc_name);
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
