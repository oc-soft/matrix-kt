

/**
 * handle html webpack
 */
class HtmlWebpack {

  /**
   * constructor
   */
  constructor() {
  }


  /**
   * setup webpack configuration
   */
  setupWebpack(config) {
    this.setupHtmlPlugin(config);
    this.setupCdnPlugin(config);
  }

  /**
   * set html-webpack-plugin up
   */
  setupHtmlPlugin(config) {
    const HtmlWebpackPlugin = require('html-webpack-plugin');
    const htmlPluginConfig = {
      title: 'Mine sweeper',
      inject: false,
      filename: 'entry.php',
      template: 'src/mswp.ejs'
    };
    const path = require('path');
    htmlPluginConfig.template = path.join(
      GradleBuild.config.projectDir, htmlPluginConfig.template)

    config.plugins.push(new HtmlWebpackPlugin(htmlPluginConfig));

  }


  /**
   * set webpack-cdn-plugin up.
   */
  setupCdnPlugin(config) {
    const WebpackCdnPlugin = require('webpack-cdn-plugin');
    
    const cdnPluginConfig = {
      modules: [
        {
          name: 'webfontloader'
        },
        {
          name: 'jquery'
        },
        {
          name: 'less',
          path: 'dist/less.min.js'
        },
        {
          name: 'bootstrap',
          styles: [
            'dist/css/bootstrap.min.css'
          ]
        },
        {
          name: '@popperjs/core',
          prodUrl: '//unpkg.com/:name@:version'
        },
        {
          name: '@fortawesome/fontawesome-free',
          cdn: 'font-awesome',
          paths: [
            'js/fontawesome.min.js',
            'js/solid.js'
          ],
          prodUrl: '//cdnjs.cloudflare.com/ajax/libs/:name/:version/:path',
          styles: [
            'css/fontawesome.css',
            'css/solid.css'
          ]
        },
        {
          name: '@fortawesome/fontawesome-svg-core',
          prodUrl: '//unpkg.com/:name@:version'
        }
      ]
    };
    cdnPluginConfig.pathToNodeModules = GradleBuild.config.projectDir 
    config.plugins.push(new WebpackCdnPlugin(cdnPluginConfig));
  }

}

(function(config) {
  const path = require('path')
  const pathInfo = path.parse(__filename)
  if (pathInfo.name != 'karma.conf') {
    const htmlWebpack = new HtmlWebpack()
    htmlWebpack.setupWebpack(config)
  }
})(config)



// vi: se ts=2 sw=2 et:
