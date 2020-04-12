
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
    config.externals.push('gl-matrix');
    config.externals.push('bootstrap');
    config.externals.push('@popperjs/core');
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
