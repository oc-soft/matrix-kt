
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
        const programDir = GradleBuild.config.programDir;
        result = `${programDir}/${result}-[contenthash].js`;
        return result;
      }
    }
  }

  /**
   *  setup karma webpack preprocessor 
   */
  setupKarmaWebpack(config) {
    const path = require('path')
    const pathInfo = path.parse(__filename)
    if (pathInfo.name == 'karma.conf') {
      config.output = config.output || {}
      config.output.libraryTarget = GradleBuild.config.libraryTarget
    }
  }
}

((config) => {

 const output = new Output()
 output.setupWebpack(config)
 output.setupKarmaWebpack(config)
})(config);
// vi: ts=2 sw=2 et:
