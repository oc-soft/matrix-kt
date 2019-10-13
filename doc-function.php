<?php
require_once 'config.php';
require_once implode('/', array('site', 'config.php')); 

/**
 * get body contents and header settings.
 */
function doc_get_contents($accept_lang, $doc_name) {
	$saved_error_level = error_reporting(0);

	$config_base = file_get_contents(implode('/', 
			array('doc', $doc_name . '.rc')));
	error_reporting($saved_error_level);
	if ($config_base) {
		require_once 'l18n-contents.php';
		$doc_contents = l18n_contents_get_by_file($accept_lang, 
			'doc', $doc_name, '.txt');
		if (isset($doc_contents)) {
			$config_base_obj = json_decode($config_base, TRUE);
			$config_lang = l18n_contents_get_by_file($accept_lang,
				'doc', $doc_name, '.rc');
			if (isset($config_lang)) {
				$config_lang_obj = json_decode($config_lang, TRUE);
				$config = array_replace_recursive($config_base_obj,
					$config_lang_obj);
			} else {
				$config = $config_base;
			}	
			$result = array(
				'body' => $doc_contents,
				'config' => $config,
				'doc_name' => $doc_name);
		}
	}
	return $result;
}

function doc_render_head($setting) {	

	$config = $setting['config'];

	if (isset($config)) {
		if (isset($config['head'])) {
			$head = $config['head'];
			if (isset($head['title'])) {	
				echo '<title>' . $head['title'] . '</title>';
			}
			if (isset($head['og'])) {
				global 	$mswp_settings;
				$og = $head['og'];
				if (isset($og['title'])) {
					$str = '<meta property="og:title" ';
					$str .= 'content="' . $og['title'] . '">';
  					echo $str; 
				}
				if (isset($og['image'])) {
					$str = '<meta property="og:image" ';
					$str .= 'content="' . $mswp_settings['siteUrl'];
		   			$str .= $og['image'] . '">';
  					echo $str; 
				}
				if (isset($og['type'])) {
					$str = '<meta property="og:type" ';
					$str .= 'content="' . $og['type'] . '">';
  					echo $str; 
				}
				if (isset($setting['doc_name'])) {
					$str = '<meta property="og:url" ';
					$str .= 'content="' . $mswp_settings['siteUrl'];
		   			$str .= '/index.php?doc=' . $setting['doc_name'] . '">';
  					echo $str; 
				}
			}
			if (isset($head['using'])) {
				global $using_mapping;
				foreach($head['using'] as $val) {
					if (is_string($val)) {
						if (isset($using_mapping[$val])) {
							echo $using_mapping[$val];
						}		
					}	
				}
			}
		}
	}
}

/**
 * render html body.
 */
function doc_render_body($setting) {
	require __DIR__ . '/vendor/autoload.php';
	$parser = new \cebe\markdown\Markdown();
	$body_contents = $parser->parse($setting['body']);
	echo $body_contents;
}

?>
