requirejs.config({
    paths: {
        kotlin: 'build/js/packages_imported/kotlin/1.3.41/kotlin',
        mswp: 'build/classes/kotlin/js/main/mswp'
    }
});


requirejs(['mswp'], function(mswpLib) {
});
