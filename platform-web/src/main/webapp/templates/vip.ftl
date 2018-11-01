<!DOCTYPE html>

<html>
<head>
    <title>支付状态窗口</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"> 
</head>
<body>
<center>
    <h3>${status}，<span id="num">5</span>秒钟自动关闭窗口...</h3>
</center>
<script>
    var time = document.getElementById("num");
    var count = 5;
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1;
    setInterval(function(){
            if(count>0){
                count--;
                time.innerHTML = count;
            }else{
                // window.open("","_self").close();
                /**
                 * app端提供的方法
                 */
                if(isAndroid){
                    App.close();
                }else{
                    /**
                     * apple
                     */
                }
            }
        },1000);
</script>
</body>
</html>