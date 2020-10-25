
/**
 * webpack working directory
 */
class Clean {

  /**
   * constructor
   */
  constructor() {
  }


  /**
   * setup webpack configuration
   */
  setupWebpack(config) {
    const { CleanWebpackPlugin } = require('clean-webpack-plugin');
    config.plugins.push(new CleanWebpackPlugin());
  }
}

(function(config) {
  const path = require('path');
  (new Clean()).setupWebpack(config);
})(config);

/* vi: se ts=2 sw=2 et: */
