
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
    if (!config.externals) {
      config.externals = [];
    } 
    config.externals.push('bootstrap');
    config.externals.push('@popperjs/core');
    config.externals.push('@fortawesome/fontawesome-free');
    config.externals.push('@fortawesome/fontawesome-svg-core');
    config.externals.push('@fortawesome/fontawesome-free/css/fontawesome.css');
    config.externals.push('@fortawesome/fontawesome-free/css/solid.css');
    config.externals.push('@fortawesome/fontawesome-free/js/solid.js');
    config.externals.push(
      function(contextRequest, cb) {
        let context = undefined;
        let request = undefined;
        if (contextRequest.request) {
          context = contextRequest.context;
          request = contextRequest.request;
        } else {
          context = contextRequest;
          request = cb; 
          cb = arguments[2];
        }
        if (request == 'jquery') {
          cb(undefined, 'jQuery', 'window');
        } else if (request == 'webfontloader') {
          cb(undefined, 'WebFont', 'window');
        } else {
          cb();
        }
      });
  }
}

((config)=> {
  const path = require('path');
  const confName = path.basename(__filename);
  if (confName == 'webpack.config.js') {
    (new Modules()).setupWebpack(config);
  }

})(config);

/* vi: se ts=2 sw=2 et: */
