
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
    let libMod = './libraries.js';
    config.entry = config.entry || { };

    config.entry['fontawesome-free'] = '@fortawesome/fontawesome-free'
    config.entry.bootstrap = 'bootstrap'
    config.entry.kotlin = 'kotlin'
    // to run this, you have to run webpack which has version haigher than 5.0
    // this.setupMainEntry(config)
  }
  /**
   * setup main etry with dedepency 
   */
  setupMainEntry(config) {
    if (config.entry.main) {
      let mainEntry
      if (Array.isArray(config.entry.main)) {
        mainEntry = {
          import: config.entry.main
        }
        config.entry.main = mainEntry
      } else if (typeof config.entry.main === 'string') {
        importEntry = config.entry.main
        mainEntry = {
          import: config.entry.main
        }
        config.entry.main = mainEntry
      } else if (typeof config.entry.main === 'object'){
        mainEntry = config.entry.main 
      }
      let dependOn
      if (mainEntry) {
        if (Array.isArray(mainEntry.dependOn)) {
          dependOn = mainEntry.dependOn
        } else if (typeof mainEntry.dependOn === 'string') {
          dependOn = [mainEntry.dependOn]
          mainEntry.dependOn = dependOn
        } else {
          dependOn = []
          mainEntry.dependOn = dependOn
        }
      }
      if (dependOn) {
        dependOn.unshift('fontawesome-free', 'bootstrap', 'kotlin')
      }
    }
  }
}

((config)=> {
  const path = require('path');
  (new Entries()).setupWebpack(config);
})(config);

// vi: se ts=2 sw=2 et:
