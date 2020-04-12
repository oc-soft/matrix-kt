
/**
 * manage webpack resolve
 */
class Resolve {

  /**
   * constructor
   */
  constructor() {
  }

  /**
   * set up webpack configuration.
   */
  setupWebpack(config) {
    config.resolve = config.resolve || {};
    config.resolve.alias = config.resolve.alias || {};
    // config.resolve.alias['popper.js'] = require.resolve('@popperjs/core');
    config.resolve.alias['popper.js'] = '@popperjs/core';
  }
}

(config=>{
  const path = require('path');
  const configName = path.basename(__filename);
  if (configName == 'webpack.config.js') {
    (new Resolve()).setupWebpack(config);
  }

})(config);


// vi: se ts=2 sw=2 et:
