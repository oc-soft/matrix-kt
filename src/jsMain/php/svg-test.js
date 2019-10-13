requirejs.config({
    paths: {
        kotlin: '../../../../prg/kotlin/1.3.41/kotlin',
        'kotlinx-html-js': '../../../../prg/kotlinx-html-js/0.6.12/kotlinx-html-js',
        mswp: '../../../../prg/mswp',
        jquery: "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min",
        webfontloader: 'https://ajax.googleapis.com/ajax/libs/webfont/1/webfont'
    }
});

requirejs(['jquery'], 
function(jqueryLib) {
    require(['mswp'], function(mswpLib) {
        mswpLib.net.ocsoft.svg.Test1.Companion.run(test1Setting);
    });
});



