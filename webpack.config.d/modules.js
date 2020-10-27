
/**
 * handle modules
 */
class Modules {
  /**
   * constructor
   */
  constructor() {
  }

  /**
   * setup webpack
   */
  setupWebpack(config) {
    const path = require('path');
    const fileInfo = path.parse(__filename)
    if (fileInfo.name != 'karma.conf') {
      this.setupExternals(config)
    }
  }

  setupExternals(config) {
    if (!config.externals) {
      config.externals = [];
    } 
    config.externals.push('bootstrap');
    config.externals.push({
      '@popperjs/core': {
        commonjs: '@popperjs/core',
        commonjs2: '@popperjs/core',
        root: 'Popper'
      }
    });
    config.externals.push('@fortawesome/fontawesome-free');
    config.externals.push('@fortawesome/fontawesome-free/css/fontawesome.css');
    config.externals.push('@fortawesome/fontawesome-free/css/solid.css');
    config.externals.push('@fortawesome/fontawesome-free/js/solid.js');
    config.externals.push({
      jquery:{
        commonjs: 'jquery',
        commonjs2:'jquery',
        root: 'jQuery'
      }
    });
    config.externals.push({
      webfontloader: {
        commonjs: 'webfontloader',
        commonjs2: 'webfontloader',
        root: 'WebFont'
      }
    });
    config.externals.push({
      less: 'less'
    })
  }
}

((config)=> {
  (new Modules()).setupWebpack(config);
})(config);

/* vi: se ts=2 sw=2 et: */
