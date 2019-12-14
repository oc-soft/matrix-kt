<?php


/**
 * setup text domain for gettext
 */
function i18n_bind_textdomain($accept_lang = NULL, $dommain = NULL) {
    if (!isset($domain)) {
        $domain = 'messages';
    }
    if (!isset($accept_lang)) {
        $lang = NULL;
        if (isset($_REQUEST['lang'])) {
            $lang = $_REQUEST['lang'];
        }
        $accept_lang = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
        if (isset($lang)) {
            $accept_lang = $lang . ',' . $accept_lang;  
        }
    }
    i18n_bind_textdomain_i($accept_lang, $domain);
}

/**
 * setup text domain for gettext
 */
function i18n_bind_textdomain_i($accept_lang, $domain) {
    $langs = array();
    $lang_iter = function($lang) use(&$langs) {
        $langs[] = $lang;
        return FALSE;
    };
    i18n_iterate_accept_languages($accept_lang, $lang_iter);

    if (count($langs)) {
        $state = i18n_bind_textdomain_i0($langs[0], $domain);
        if (!$state) { 
            $match_state = preg_match('/([a-zA-Z0-9]+)(_|-)(\w+)/',
                $langs[0], $matches);
            if ($match_state) {
                $state = i18n_bind_textdomain_i0($matches[1], $domain);
            }
            if (function_exists(mswp_is_debug) && mswp_is_debug()) { 
                var_dump($state);
            }
        }
    }
}

/**
 * setup text domain for gettext
 */
function i18n_bind_textdomain_i0($lang, $domain) {

    $result = FALSE;
    if (defined('LC_MESSAGES')) {
        i18n_set_locale(LC_MESSAGES, $lang); 
        putenv(sprintf('LC_MESSAGES=%s', $lang));
    } else {
        i18n_set_locale(LC_ALL, $lang); 
        putenv(sprintf('LC_ALL=%s', $lang));
    }
    $result = i18n_bindtextdomain(
        $domain, implode('/', array(__DIR__, 'i18n')));
    i18n_bind_textdomain_codeset($domain, 'UTF-8');
    return $result;
}


/**
 * bindtextdomain wrapper.
 * some php system is not installed gettext library.
 */
function i18n_bindtextdomain($domain, $directory) {
    $result = FALSE;
    if (function_exists('bindtextdomain')) {
        $result = bindtextdomain($domain, $directory);
        if (function_exists(mswp_is_debug) && mswp_is_debug()) { 
            var_dump($result);
        }
    } else {
        if (!function_exists('gettext')) {
            function gettext($str) {
                return $str;
            }
            function _($str) {
                return $str;
            }
        }
    }
    return $result;
}

/**
 * bind_textdomain_codeset wrapper.
 * some php system is not installed gettext library.
 */
function i18n_bind_textdomain_codeset($domain, $codeset) {
    if (function_exists('bind_textdomain_codeset')) {
        bind_textdomain_codeset($domain, codeset);
    }
}


/**
 * set locale with LOCPATH environement.
 */
function i18n_set_locale($category, $locale) {
    $result = setlocale($catetory, $locale);
    if (!$result) {
        putenv(sprintf('LOCPATH=%s',
            implode('/', array(__DIR__, 'locale'))));
        $result = setlocale($category, $locale);
        putenv(sprintf('LOCPATH'));
    }
    return $result;
}


/**
 * create q value sorted language map.
 */
function i18n_create_qval_lang_map($accept_lang) {
    $result = NULL;
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
        $result = $q_lang_map;
    }   
    return $result;
}


/**
 * iterate accept languages higher request order.
 */
function i18n_iterate_accept_languages($accept_lang, $iter) {
    $q_lang_map = i18n_create_qval_lang_map($accept_lang);
    if (isset($q_lang_map)) {
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
