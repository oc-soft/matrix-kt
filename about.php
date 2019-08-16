<html>
<head>
  <meta charset="UTF-8">
  <title>About this projects</title>
  <meta name="viewport" content="width=device-width initial-scale=1">

  <script
    src="https://code.jquery.com/jquery-3.4.1.min.js"
    integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
    crossorigin="anonymous"></script>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  <link rel="stylesheet" type="text/css" href="about.css">

</head> 
<body>
<div class="main">
<h1>動機</h1>
<p>2019の子供の夏休みの課題に則ってMine sweeperの開発をしました。子供と共に夏休みを過ごしてみようとの思い付きです。欲張って、以下の課題をクリアしたいと始めました。</p>
<ol>
<li>androidの開発を見据えてkotlinを使いならすこと。</li>
<li>gradleによる開発サイクルに慣れること。</li>
<li>groogyでgradleのスクリプトを拡張して、自分の望んだjavascriptアプリを作成する。</li>
<li>webglによるUIの可能性を調べること。</li>
<li>open glのレンダリングの詳細を理解すること。</li>
</ol>
<h1>課題の達成方法</h1>
<p>実践的な小さいプログラムを作成する。プログラムの作成中に、仕様を実現する方法を模索することで、課題をクリアする。</p>
<h1>予想</h1>
<p>googleがandroid開発において、kotlinを推奨しているので、kotlinには十分な開発環境が整っている。kotlinのマルチプラットフォーム開発は、多くの部分で共通利用できるライブラリがそろっている。</p>
<h1>開発の経過</h1>
<p>以下の表のように開発が進んだ。</p>
<table class=table>
<thead><tr><th scope="col" colspan="2">期間</th><th scope="col">作業と感想</th></tr></thead>
<tbody>
<tr scope="row"><td>2019/7/14</td><td>2019/7/16</td><td>gradleの仕組みと、groovyの関係を理解した。java環境でも使いやすいスクリプト言語があると思った。</td></tr>
<tr scope="row"><td>2019/7/17</td><td>2019/7/25</td><td>webglのサンプルコードを参考に仕様に合致するように簡単なuiパーツを作成した。domノードによるguiは、十分に汎用性が高いことを再認識した。</td></tr>
<tr scope="row"><td>2019/7/26</td><td>2019/7/28</td><td>gradleが生成するjsモジュールがjarにアーカイブ化されてるいることが判明。jarからjsのソースをサーバー側で実行時に取り出すことを考えた。jarの扱いなので、servletを利用しようと考える。sevletにすると、tomcatの構築から始めるので、時間的にあきらめた。build.gradleを拡張して、js変換されたファイルを配備ディレクトリに展開するようにした。</td></tr>
<tr scope="row"><td>2019/7/29</td><td>2019/8/3</td><td>mac環境の開発に移行して、safariで動作確認させると、ボタンのクリックの処理の部分でwebglがエラーを出力した。詳細を調べると、オフスクリーンレンダリングからオブジェクトのクリック判定用の色取得が16bitであることが問題とわかる。いったん32bitのrgbaとして取得し、それを16bitに変換することで回避する。</td></tr>
<tr scope="row"><td>2019/8/4</td><td>2019/8/14</td><td>ボタンに番号を表示するためのテクスチャを組み込む。Webfontを利用して、テクスチャを生成しようとしたが、fontデータがクライアントに届く前に、テクスチャの生成が始まってしまう。fontデータが確実に読み込まれた後にテクスチャを生成する方法を検討したが、保留とした。htmlは、情報を確実に届けるという本来的な思想があると考えると、fontが読み込まれるまで情報を表示しないというのは、優先度の低い開発案件であると理解した。</td></tr>
<tr scope="row"><td>2019/8/15</td><td>2019/8/16</td><td>ゲームロジックを完成させる。responsiveの対応を行う。子供たちに動作をみてもらうと、いろいろな提案があった。拡張案件として対応していく約束をした。</td></tr>
</tbody>
</table>
<h1>開発の結果</h1>
<p>kotlinはコンパイルが通ると、実行時のエラーがほとんど出ない。マルチプラットフォーム用のbuild.gradleは、拡張の余地が残っている。chrome、safariはともにwebkitがベースであったが、webglでは、対応状況が異なっていた。子供たちにはプログラムについて、身近に感じてもらいたいと思ったが、プログラムに主体的にかかわるほどの魅力を発揮することが出来なかった。</p>
<h1>考察</h1>
<p>kotlinは静的型志向言語であるので、ちょっとしたデータの受け渡しにおいても、classを使う。NullPointerExceptionを減らそうとする言語使用は、初期フィールドの値をnullにさせないようにする。実行時に動的にオブジェクトが連結したときに、結果を出すような処理を作成する場合、フィールドを仮のオブジェクトで埋める場合がある。</p>
<h1>まとめ</h1>
<p>静的型志向言語は、言語習得レベルが異なる開発者が多数集まるような大規模開発に向いているといえる。開発後半になって、プログラムが具体的になるほど、子供たちから拡張の提案でてくるようになってきた。</p>
<h1>感想</h1>
<p>タイムを計ったり、ゲームの難易度を設定したり、子供たちからのアイディアを反映させていきたい。</p>
</div>
</body>
</html>
