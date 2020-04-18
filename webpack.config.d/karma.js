


/**
 * setup karma
 */
class Karma {


  /**
   * constructor
   */
  constructor() {
  }

  /**
   * set karma config
   */
  setupKarma(config) {
  }


  /**
   * setup webpack cofiguration
   */
  setupWebpack(config) {
    let entries = [
      new Clean(),
      new Modules(),
      new Output(),
      new HtmlWebpack(),
      new Entries(),
      new Mode(),
      new Css(),
      new Resolve(),
      new Fonts()
    ];
    entries.forEach(it => it.setupWebpack(config)); 
  }
}

/**
 * create webpack config
 */
var createWebpackConfig = (function(superFunc) {
  return function() {
    let result = undefined;
    if (typeof superFunc === 'function') {
      result = superFunc();
      (new Karma()).setupWebpack(result);
    }
    return result;
  }
})(createWebpackConfig);

(config => {
  const path = require('path');
  const confName = path.basename(__filename);
  if (confName == 'karma.config.js') {
    (new Karma()).setupKarma(config);
  }
})(config);


/* vi: se ts=2 sw=2 et: */
