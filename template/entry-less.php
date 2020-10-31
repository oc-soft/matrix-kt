<?php require(implode('/', array(__DIR__, 'config-less.php'))); ?>

body {
   background-color: @env-color-background; 
}

.splash_text {
    color: @env-color-foreground;
}

@keyframes color-to-transparent-1 {
    0%, 20% { 
        color: @env-color-foreground; 
    }
    80%, 100% {
        color: fade(@env-color-foreground, 20);
    }
}



div.menu-bar {
    display: flex;
    justify-content: space-between;
    div.setting.menu {
        color: @text-color;
    }
    div.flags {
        color: @text-color; 
        .blink-color {
            animation-name: color-to-transparent-1;
            animation-duration: 2s;
            animation-timing-function: linear;
            animation-iteration-count: infinite;
            animation-direction: alternate;
        }
        span {
            cursor: default;
        }
    }
}



.setting.menu.contents {
    visibility: hidden;
    pointer-event: none;
    cursor: default;
    color: @text-color; 
    --popper-placement: bottom-start;
    --popper-modifiers-offset: 3 3;
}



#back-to-main {
    display: none;
    position: absolute;
    top: 0px;
    left: 0px;
    svg {
        color: @env-color-foreground;
    }
}



<?php
// vi: se ts=4 sw=4 et:
