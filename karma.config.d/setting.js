
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
  }
}

(config=>{
  const path = require('path');
  const configName = path.basename(__filename);
  if (configName == 'karma.config.js') {
    (new Setting()).setupKarma(config);
  }
})(config);


// vi: se ts=2 sw=2 et:
