<?php require(implode('/', array(__DIR__, 'config-less.php'))); ?>
#game_grid {
  width: 480px;
  height: 480px;
  margin: auto;
  display: block;
}

.icon-item {
  width: calc(99.96% / 6);
}


body {
   background-color: @env-color-background; 
}

#glyph_workarea {
    display: none;
}

.resource {
    display: none; 
}

.overlay {
    height: 100%;
    width: 100%;
    position: fixed;
    z-index: 1;
    top: 0;
    left: 0;
    background-color: rgba(0, 0, 0, 0.9); 
    overflow: hidden;
    transition: 0.5s;
}

.splash_text {
    color: @env-color-foreground;
}

.loading {
    position: absolute;
    bottom: 0px;
    right: 0px;
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

.setting .menu.item {
    width: 1.5em;
    height: 1.5em;
    display: flex;
    justify-content: center;
}
.setting .menu.item>i {
    margin: auto;
}

.setting .menu.item>svg {
    margin: auto;
}

.setting.menu.contents {
    display: none;
    cursor: default;
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

.icons {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
}

.icon.kind {
  margin-top: 0.5em;
  margin-bottom: 0.5em; 
}


ul.list.icons {
    list-style-type: none;    
    margin: 0;
    padding: 0;
}

li.icon-item div {
    display: flex;
    justify-content: center;
    border-radius: .25em;
    padding: 4px;
    border: solid #ffffff;
}

li.icon-item div:hover
{
    border:solid var(--secondary);
}


li.icon-item.selected div:hover,
li.icon-item.selected div
{
    border:solid var(--primary);
}


.pagination.container {
    display: flex;
    flex-direction: row;
    justify-content: center;  
    padding-top: 12px;
    padding-left: 0px;
    padding-right: 0px;
    padding-bottom: 0px;
}


li.icon-item div i {
    font-size: 36px;
}
li.icon-item div div {
    height: 36px;
    padding: initial;
    border: initial;
}

li.icon-item svg.svg-inline--fa {
     font-size: 36px;
}

.btn.page-number {
    width: 2.5em;
}    

li.icon-item svg.svg-inline--fa path {
    fill: #1B1B1B 
}



#color-selector {
    div.description {
        text-align: center;
    }
    div.colors.container {
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;

        div.color-scheme {
            border-radius: .2em;
            border-color: rgb(222, 226, 230);
            border-style: solid;
            border-width: thin;
            margin: .2em;
            width: 1em;
            height: 1em;
            &.selected:hover,
            &.selected {
                border-color: var(--primary);
            }
            &:hover {
                border-color: var(--secondary);
            }
        }
    }
    div.color-circle-container {
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    div.color-value-controller {
        width: 100%;
        display: flex; 
        justify-content: center;
        align-items: center;
        margin-top: 10px;

    }
    canvas.oc-color-circle {
        width: 160px;
        height: 160px;
    }
    .modal-footer {
        display: block;
        .left-side {
            float: left;
        }
        .right-side {
            float: right;
        }
    }
}
<?php
// vi: se ts=4 sw=4 et: