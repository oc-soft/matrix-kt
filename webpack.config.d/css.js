
/**
 * handle css 
 */
class Css {


  /**
   * constructor
   */
  constructor() {
  }

  /**
   * setup webpack configuration.
   */
  setupWebpack(config) {
    const ExtractCss = require('mini-css-extract-plugin');
    config.module.rules.push({
      test: /\.css$/i,
      use: [
        {
          loader: ExtractCss.loader
        },
        'css-loader'
      ]
    });
    config.module.rules.push({
      test: /\.less$/i,
      use: [
        { 
          loader: ExtractCss.loader,
        },
        'css-loader',
        'less-loader'
      ],
    });
    config.plugins.push(
      new ExtractCss({
        filename: 'css/[name]-[contenthash].css',
        chunkFilename: '[id].css'
      }));
  }
}


(config=>{

  const path = require('path');
  const configName = path.basename(__filename);
  if (configName == 'webpack.config.js') {
    (new Css()).setupWebpack(config);
  }

})(config);

// vi: se ts=2 sw=2 et:
