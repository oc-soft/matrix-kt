
class Mode {

  /**
   * constructor
   */
  constructor() {
  }

  /**
   * setup webpack configuration
   */
  setupWebpack(config) {
    config.mode = 'development';
  }

}

if (typeof createWebpackConfig === 'function') {
  

}

(function(config) {
  const path = require('path');
  (new Mode()).setupWebpack(config);
})(config);
// vi: se ts=2 sw=2 et:
