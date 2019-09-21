const { src, dest, parallel } = require('gulp');
const settings = require('./config');

/**
 * generate css files
 */
function css() {
    const tasks = settings.projects.map(function(elem) {
        const res = function(cb) {
            const less = require('gulp-less');
            let st = src(settings[elem].css.pipe.src);
            st = st.pipe(less());
            st = st.pipe(dest(settings[elem].css.pipe.dest));
            return st;
        }
        return res;
    }); 
    console.log(parallel);
    return parallel.apply(null, tasks); 
}

exports.css = css();
