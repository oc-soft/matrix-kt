
/**
 * handle configuration entries
 */
class Entries {

  /**
   * constructor
   */
  constructor() {
  }


  /**
   * set webpack configuration
   */
  setupWebpack(config) {
    const FindFile = require('find-file-up');
    const FindStyle = require('find-style');
   
    let libMod = './libraries.js';
    config.entry = config.entry || { };
    if (libMod) {
      config.entry.libs = libMod;
    }
    config.entry.kotlin = ['kotlin'];
  }
}

((config)=> {
  const path = require('path');
  (new Entries()).setupWebpack(config);
})(config);

// vi: se ts=2 sw=2 et:
