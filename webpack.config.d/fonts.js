
/**
 * handle font resource
 */
class Fonts {

  /**
   * constructor
   */
  constructor() {
  }

  /**
   * setup webpack config
   */
  setupWebpack(config) {
    config.module.rules.push({
      test: /fontawesome.+\/webfonts\/.+\.(woff|ttf|woff2|svg|eot)$/i,
      use: 'null-loader' 
    });
  }
}


(config=>{

  const path = require('path');
  const configName = path.basename(__filename);
  if (configName == 'webpack.config.js') {
    (new Fonts()).setupWebpack(config);
  }

})(config);


// vi: se ts=2 sw=2 et:
