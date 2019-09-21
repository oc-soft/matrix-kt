
const settings = {
    projects: ['game'],
    game: {
        css: {
            pipe: { 
                src: ['entry.less'],
                dest: './'
            } 
        }
    }
};

module.exports = settings;
