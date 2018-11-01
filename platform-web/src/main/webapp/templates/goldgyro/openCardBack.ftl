<!DOCTYPE html>

<html>
<head>
    <title>Regist Card</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"> 
</head>
<body>
<#--<form action="">
    <input id="activateStatus" name="activateStatus"  value="${activateStatus}" /><br/>
    <input id="openOrderId" name="openOrderId"  value="${openOrderId}" /><br/>
    <input id="activateStatus" name="activateStatus"  value="${activateStatus}" /><br/>
    <a href="javascript:window.opener=null;window.close();">关闭</a>
</form>-->
<center>
    <h3>信息已提交，<span id="num">5</span>秒钟自动关闭窗口...</h3>
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