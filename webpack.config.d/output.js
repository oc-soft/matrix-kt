
/**
 * manage output
 */
class Output {


  /**
   * get css output 
   */
  static get cssOutput() {
    
  }


  /**
   * constructor
   */
  constructor() {
  }


  /**
   * setup webpack configuration
   */
  setupWebpack(config) {
    if (config.output) {
      config.output.filename = (chunkData) => {
        let result = undefined;
        if (chunkData.chunk.name === 'main') {
          result = 'mswp';
        } else {
          result = chunkData.chunk.name;
        }
        result = `prg/${result}-[contenthash].js`;
        return result;
      };
    }
  }
}

((config) => {
  const path = require('path');
  const confName = path.basename(__filename);
  if (confName == 'webpack.config.js') {
    (new Output()).setupWebpack(config);
  }
})(config);
// vi: ts=2 sw=2 et:
