
((config)=> {
  const FindFile = require('find-file-up');
 
  let libMod = FindFile.sync('src/jsMain/js/libraries.js');
  if (libMod) {
    config.entry.libs = libMod;
  }
})(config);

// vi: se ts=2 sw=2 et:
