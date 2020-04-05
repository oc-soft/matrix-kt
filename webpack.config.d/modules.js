


((config)=> {
  if (!config.externals) {
    config.externals = [];
  } 
  config.externals.push(
    function(contextRequest, cb) {
      let context = undefined;
      let request = undefined;
      if (contextRequest.request) {
        context = contextRequest.context;
        request = contextRequest.request;
      } else {
        context = contextRequest;
        request = cb; 
        cb = arguments[2];
      }
      if (request == 'jquery') {
        cb(undefined, 'jQuery', 'window');
      } else {
        cb();
      }
    });

})(config);

/* vi: se ts=2 sw=2 et: */
