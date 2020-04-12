
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
   
    let libMod = FindFile.sync('src/jsMain/js/libraries.js');
    config.entry = config.entry || { };
    if (libMod) {
      config.entry.libs = libMod;
    }
    config.entry.kotlin = require.resolve('kotlin');

    config.entry.main.push(require.resolve('bootstrap'));
    config.entry.main.push((new FindStyle()).find('bootstrap'));
    config.entry.main.push(FindFile.sync('entry.less'));
  }
    
}

((config)=> {
  const path = require('path');
  const confName = path.basename(__filename);
  if (confName == 'webpack.config.js') {
    (new Entries()).setupWebpack(config);
  }
})(config);

// vi: se ts=2 sw=2 et:
