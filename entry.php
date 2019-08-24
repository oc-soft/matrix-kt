<!DOCTYPE html>
<html prefix="og: http://ogp.me/ns#">
<head>
<?php
include 'config.php';
$texture_text = file('TextureText.txt', 
    FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
$mswp_settings['textureText'] = implode('', $texture_text);
?>
  <meta charset="UTF-8">
  <title>Mine sweeper</title>
  <meta property="og:title" content="Mine sweeper"/>
  <meta property="og:image" 
    content="<?php echo $mswp_settings['siteUrl']; ?>/img/game-image.png"/>
  <meta property="og:type" content="website"/>
  <meta property="og:url"
    content="<?php echo $mswp_settings['siteUrl']; ?>/entry.php"/>
  <meta name="viewport" content="width=device-width initial-scale=1">
  <script
    src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js">
  </script>
  <script
    src="https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/2.8.1/gl-matrix-min.js">
  </script>
  <script
    src="https://ajax.googleapis.com/ajax/libs/webfont/1.6.26/webfont.js">
  </script>

  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <link rel="stylesheet" type="text/css" href="entry.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
 <script>
    var mswpSettings = <?php echo json_encode($mswp_settings) ?>;
  </script>  
  <script data-main="entry"
    src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.3.6/require.js">
  </script>

</head>

<body>
<div id="splash_pane" class="overlay">
<div class="loading text-white"><span>Loading...
<div class="spinner-grow text-white" role="status"<span class="sr-only"></span></div></span>
</div>
</div>
<canvas id="game_grid" class=play-ground></canvas>
<canvas id="font_test" width="256" height="256"></canvas>
<div id="game_over_modal" class="modal" tabindx="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-body">
          <p>Game over</p>
	  <a href="./about.php" class="badge badge-primary">About this Project</a>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary"
            data-dismiss="modal">Play again</button>
        </div>
    </div>
  </div>
</div>
<div id="player_won_modal" class="modal" tabindx="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-body">
          <p>You won the game</p>
	  <a href="./about.php" class="badge badge-primary">About this Project</a>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary"
            data-dismiss="modal">Play again</button>
        </div>
    </div>
  </div>
</div>
</body>

</html>
