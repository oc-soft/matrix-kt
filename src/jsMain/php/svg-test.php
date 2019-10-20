<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Edge svg data parsing test</title>
  <script>
    var test1Setting = <?php echo $config_contents; ?>;
  </script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/2.8.1/gl-matrix-min.js">
  </script>
  <script data-main="svg-test" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.3.6/require.js">
  </script>
</head>
<body>
<ul id="test-result">
</ul>
<template id="test-result-item">
<li>
  <div class="test path-data"></div>
  <canvas class="test region" width="126" height="126"></canvas>
</li>
</template>
</body>
</html>
