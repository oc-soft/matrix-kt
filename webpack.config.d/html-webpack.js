
(function(config) {

  const HtmlWebpackPlugin = require('html-webpack-plugin');
  const htmlPluginConfig = {
    title: 'Mine sweeper',
    filename: 'entry.php',
    template: 'src/mswp.ejs'
  };
  const FindFile = require('find-file-up');
  htmlPluginConfig.template = FindFile.sync(htmlPluginConfig.template);

  config.plugins.push(new HtmlWebpackPlugin(htmlPluginConfig));

})(config);


(function(config) {

  const WebpackCdnPlugin = require('webpack-cdn-plugin');
  
  const cdnPluginConfig = {
    modules: [
      {
        name: 'jquery'
      }
    ]
  };
  const FindFile = require('find-file-up');
  const path = require('path');
  let yarnLockPath = FindFile.sync('yarn.lock');
  let result = undefined;

  if (yarnLockPath) {
    const pathInfo = path.parse(yarnLockPath);
    cdnPluginConfig.pathToNodeModules = pathInfo.dir;
  }
  config.plugins.push(new WebpackCdnPlugin(cdnPluginConfig));

})(config);



// vi: se ts=2 sw=2 et:
