


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

(config => {
  const path = require('path');
  const confName = path.basename(__filename);
  if (confName == 'karma.conf.js') {
    (new Karma()).setupKarma(config);
  }
})(config);


/* vi: se ts=2 sw=2 et: */
