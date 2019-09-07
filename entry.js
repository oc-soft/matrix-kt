requirejs.config({
    paths: {
        kotlin: 'prg/kotlin/1.3.41/kotlin',
        'kotlinx-html-js': 'prg/kotlinx-html-js/0.6.12/kotlinx-html-js',
        mswp: 'prg/mswp',
    'fontawesome' : 'prg/fontawesome-free/js/fontawesome',
    'fontawesome-solid' : 'prg/fontawesome-free/js/solid',
        webfontloader: 'https://ajax.googleapis.com/ajax/libs/webfont/1/webfont'
    }
});

requirejs(['mswp', 'fontawesome', 'fontawesome-solid'], 
function(mswpLib, fa, fas) {
    mswpLib.net.ocsoft.mswp.Context.mainPage.run(mswpSettings);    
});

