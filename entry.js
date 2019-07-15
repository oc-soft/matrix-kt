requirejs.config({
    paths: {
        kotlin: 'build/js/packages_imported/kotlin/1.3.41/kotlin',
        'kotlinx-html-js': 'build/js/packages_imported/kotlinx-html-js/0.6.12/kotlinx-html-js',
        mswp: 'build/classes/kotlin/js/main/mswp'
    }
});


requirejs(['mswp'], function(mswpLib) {
});
