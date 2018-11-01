<#assign base=request.contextPath />
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title>金陀螺下载</title>
</head>
<style>
    .x1{
        width: 100vw;
        margin-top: -2vw;
    }
    .x2{
        width: 40vw;
        display: flex;
        flex-direction: row;
        margin-left: 30vw;
    }
    .x3{
        width: 40vw;
        height: 40vw;
        margin: 10vw 30vw 0vw 30vw;

    }
    .x4{
        font-size: 6vw;
        font-family: PingFangSC-Semibold;
        font-weight: bold;
        margin-left: 1vw;
    }
    .x5{
        color: white;
        margin-left: 2vw;
        border-radius: 0.5vw;
        width: 13vw;
        height: 6vw;
        background: #46A4FC;
        text-align: center;
        line-height: 6vw;
        font-size: 3.5vw;
        margin-top: 0.5vw;
    }
    .x6{
        width: 48vw;
        display: flex;
        flex-direction: row;
        margin-left: 26vw;
        font-size: 4.6vw;
        font-family:PingFangSC-Regular;
        color:rgba(150,155,157,1);
        margin-top: 2vw;
    }
    .x7{
        margin-left: 1vw;
    }
    .x8{
        margin-left: 3vw;
    }
    .x9{
        width: 36vw;
        display: flex;
        flex-direction: row;
        margin-left: 32vw;
        font-size: 4vw;
        font-family:PingFangSC-Regular;
        color:rgba(150,155,157,1);
        margin-top: 2vw;
    }
    .x10{
        margin-left: 10vw;
        background: #F6F6F6;
        width: 80vw;
        height: 0.3vw;
        margin-top: 4vw;
    }
    .x11{
        margin-left: 10vw;
        width: 80vw;
        margin-top: 4vw;
        font-size: 3.6vw;
        font-family:PingFangSC-Regular;
        color:rgba(157,157,157,1);
    }
    .x12{
        margin-left: 20vw;
        width: 60vw;
        margin-top: 10vw;
        font-size: 5vw;
        font-family:PingFangSC-Regular;
        color:white;
        height: 12vw;
        border-radius: 6vw;
        background: #28CAAD;
        text-align: center;
        line-height: 12vw;
    }
    .fd{
        width: 100vw;
        height: 100vh;
        position: fixed;
        z-index: 999;
        left: 0;
        top: 0;
        display: none;
    }
    .x13{
        width: 4vw;
        height: 6vw;
    }
</style>
<script language="javascript">
    function is_weixn(){
        var ua = navigator.userAgent.toLowerCase();
        if(ua.match(/MicroMessenger/i)=="micromessenger") {
            document.getElementById("btnshow").style.display="block";
        } else {
            window.open("http://www.tuoluo718.com/app/download")
        }
    }
    function noneimg() {
        document.getElementById("btnshow").style.display="none";
    }
</script>
<body>
<img class="x1" src="${base}/images/xiazai1.png">
<img class="x3" src="${base}/images/xiazai2.png">
<div class="x2">
    <img class="x13" src="${base}/images/xiazai3.png">
    <label class="x4">金陀螺</label>
    <label class="x5">正式版</label>
</div>
<div class="x6">
    <label >版本:  </label>
    <label class="x7">${versionInfo.versionCode}</label>
    <label class="x8">大小: </label>
    <label class="x7">6MB</label>
</div>
<div class="x9">
    <label >更新时间: </label>
    <label class="x7">${versionInfo.releaseTime?string('yyyy-MM')}</label>
</div>
<div class="x10"></div>
<div class="x11">软件介绍：金陀螺是一款高效、简便、实用、安全的账单管理APP，该产品通过移动端导入信用卡账单，还款提醒等功能保障用户个人征信，让用户轻松管理信用卡。珍爱信用记录，享受财富人生。</div>
<div onclick="is_weixn()" class="x12">点击安装</div><!--<a class="a" href="http://www.tuoluo718.com/app/download"></a>-->
<img id="btnshow" onclick="noneimg()" class="fd" src="${base}/images/xiazai4.png">
</body>
</html>