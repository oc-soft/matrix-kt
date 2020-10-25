
/**
 * setting up karma
 */
class Setting {

  /**
   * constructor
   */
  constructor() {
  }

  /**
   * setup karuma
   */
  setupKarma(config) {
    const path = require('path')

    config.customDebugFile = path.join(
      GradleBuild.config.jsTestSourceDir,
      'karma', 'debug.html')
  }
}

(config=>{
  (new Setting()).setupKarma(config)
})(config);


// vi: se ts=2 sw=2 et:
