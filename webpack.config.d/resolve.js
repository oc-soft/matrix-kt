
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
    this.setupFontAwesome(config);
  }


  /**
   * setup fontawesome 
   */
  setupFontAwesome(config) {
    const FindPackage = require('find-package');
    const pathLib = require('path');
    const pkgInfo = 
      (new FindPackage).find('@fortawesome/fontawesome-free');
    const submodules = [
      'css/fontawesome.css',
      'css/solid.css',
      'js/fontawesome.js',
      'js/solid.js',
    ];
    const prefix = '@fortawesome/fontawesome-free';

    submodules.forEach(submodule=> {
      config.resolve.alias[`${prefix}/${submodule}`]
        = pathLib.join(pkgInfo.pathInfo.dir, submodule);
    });
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
