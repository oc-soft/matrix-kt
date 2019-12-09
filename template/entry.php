<!DOCTYPE html>
<html prefix="og: http://ogp.me/ns#">

<head>
  <meta charset="UTF-8">
  <title>Mine sweeper</title>
  <meta property="og:title" content="Mine sweeper" />
  <meta property="og:image" content="<?php echo $mswp_settings['siteUrl']; ?>/img/game-image.png" />
  <meta property="og:type" content="website" />
  <meta property="og:url" content="<?php echo $mswp_settings['siteUrl']; ?>/entry.php" />
  <meta name="viewport" content="width=device-width initial-scale=1">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js">
  </script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/2.8.1/gl-matrix-min.js">
  </script>

  <script src="https://kit.fontawesome.com/a7443310ec.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/webfont/1.6.26/webfont.js">
  </script>

  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <link rel="stylesheet" type="text/css" href="entry.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  <script>
    var mswpSettings = <?php echo json_encode($mswp_settings); ?> ;
  </script>
  <script data-main="entry" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.3.6/require.js">
  </script>

</head>

<body>
  <div id="splash_pane" class="overlay">
    <div class="loading text-white">
    <span><?php echo _("Loading...");?><div class="spinner-grow text-white" role="status"><span class="sr-only"></span></div></span>
    </div>
  </div>
  <div class="setting menu"><div class="menu item"><i class="fas fa-wrench"></i></div></div>
  <canvas id="game_grid" class=play-ground></canvas>
  <canvas id="glyph_workarea" width="256" height="256"></canvas>
  <div id="game_over_modal" class="modal" tabindx="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <p><?php echo _("Game over"); ?></p>
          <a href="index.php?doc=about"
          class="badge badge-primary"><?php 
            echo _("About this Project"); ?></a>
        </div>
        <div class="modal-footer">
          <button type="button"
            class="btn btn-primary"
            data-dismiss="modal"><?php
              echo _("Play again");
            ?></button>
        </div>
      </div>
    </div>
  </div>
  <div id="player_won_modal" class="modal" tabindx="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <p><?php echo _("You won the game"); ?></p>
          <a href="./about.php"
            class="badge badge-primary"><?php
              echo _("About this Project");
          ?></a>
        </div>
        <div class="modal-footer">
          <button type="button"
            class="btn btn-primary"
            data-dismiss="modal"><?php
              echo _("Play again");
            ?></button>
        </div>
      </div>
    </div>
  </div>

  <div id="icon_list" class="modal" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <div class="modal-title"><?php 
            echo _("Please select icons"); ?></div>
        </div>
        <div class="modal-body">
          <div class="input-group icon kind">
            <select class="custom-select" id="icon-kind-selector">
              <option value="ok" selected><?php echo _("OK"); ?></option>
              <option value="ng"><?php echo _("NG"); ?></option>
            </select>
            <div class="input-group-append" >
              <label class="input-group-text"
                for="icon-kind-selector"><?php echo _("Icon"); ?></label>
            </div>
          </div>
          <ul class="icons list">
          </ul>
          <div class="pagination container"></div>
        </div>
        <div class="modal-footer">
          <button type="button"
            class="btn btn-primary ok"><?php echo _("OK"); ?></button>
          <button type="button"
            data-dismiss="modal" 
            class="btn btn-secondary"><?php echo _("Cacel"); ?></button>
        </div>
      </div>
    </div>
  </div>
  <template id="synchronizing_icon">
    <i class="fa fa-sync-alt fa-spin"></i>
  </template>
  <template id="icon_item_tmpl">
    <li class="icon-item"><div><i></i></div></li>
  </template>
  <template id="blank_icon_item_tmpl">
    <li class="icon-item"><div><div></div></div></li>  
  </template>
  <template id="icons_paginating_full_tmpl">
    <div class="btn-group full" role="group">
      <button class="btn btn-light first">
        <i class="fas fa-angle-double-left"></i>
      </button>
      <button class="btn btn-light prev">
        <i class="fas fa-angle-left"></i>
      </button>
      <button class="btn btn-light next">
        <i class="fas fa-angle-right"></i>
      </button>
      <button class="btn btn-light last">
        <i class="fas fa-angle-double-right"></i>
      </button>
    </div>
  </template>
  <template id="icons_paginating_medium_tmpl">
    <div class="btn-group medium">
      <button class="btn btn-secondary prev">
        <i class="fas fa-angle-left"></i>
      </button>
      <button class="page-item next">
        <i class="fas fa-angle-right"></i>
      </button>
    </div>
  </template>
  <template id="icons_paginating_simple_tmpl">
    <ul class="pagination simple">
    </ul>
  </template>
  <template id="icons_paginating_item_tmpl">
    <button class="btn btn-light page-number"></button>
  </template>
</body>

</html>
