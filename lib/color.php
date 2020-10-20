<?php

/**
 * color object 
 */
class Color {
    /**
     *  constructor 
     */
    function __construct($rgba) {
        $this->rgba = array(0, 0, 0, 255);
        for ($i = 0; $i < min(count($this->rgba), count($rgba)); $i++) {
            $this->rgba[$i] = $rgba[$i];
        }  
    } 

    function toRgbStr() {
        return sprintf('rgb(%d, %d, %d)',
            $this->rgba[0], $this->rgba[1],
            $this->rgba[2], $this->rgba[3]);
     }

    /**
     * convert to css hex string
     */
    function toHexStr() {
        return sprintf('#%02x%02x%02x%02x',
            $this->rgba[0], $this->rgba[1],
            $this->rgba[2], $this->rgba[3]);
    }
}
/* vi: se ts=4 sw=4 et: */
