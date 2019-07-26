requirejs.config({
    paths: {
        kotlin: 'prg/kotlin/1.3.41/kotlin',
        'kotlinx-html-js': 'prg/kotlinx-html-js/0.6.12/kotlinx-html-js',
        mswp: 'prg/mswp'
    }
});


requirejs(['mswp'], function(mswpLib) {
});
