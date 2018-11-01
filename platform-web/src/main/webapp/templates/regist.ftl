<#assign base=request.contextPath />
<!DOCTYPE html>
<html class="bgs">
<head>
    <meta charset="UTF-8">
    <title>邀请有奖</title>
    <style>
        .bg_div{
            width: 100vw;
        }
        .bg_img{
            position: absolute;
            width: 100vw;
            top: 30vw;
            z-index: 1;
            left: 0px;
            margin: 0 auto;
        }
        .context_sp{
            position: absolute;
            top: 56vw;
            width: 100vw;
            height: 10vw;
            text-align: center;
            z-index: 4;
            background: transparent;
            font-size: 4vw;
            color: #F75048;
            border: none;
            margin: 0 auto;
        }
        .context_phone{
            position: absolute;
            width: 70vw;
            height: 10vw;
            z-index: 4;
            font-size: 4.5vw;
            border-radius: 1vw;
            border: 2px ridge#D83F3D;
            top: 68vw;
            left: 15vw;
            padding-left: 2vw;
            margin: 0 auto;
        }
        .context_code{
            position: absolute;
            width: 40vw;
            height: 10vw;
            z-index: 4;
            font-size: 4.5vw;
            border-radius: 1vw;
            border: 2px ridge#D83F3D;
            top: 84vw;
            left: 15vw;
            padding-left: 2vw;
            margin: 0 auto;
        }
        .context_getcode{
            position: absolute;
            width: 27vw;
            height: 10vw;
            z-index: 4;
            font-size: 4.5vw;
            border-radius: 1vw;
            border: 2px ridge#D83F3D;
            top: 84vw;
            left: 60vw;
            text-align: center;
            line-height: 10vw;
            text-align: center;
            margin: 0 auto;
        }
        .context_pass{
            position: absolute;
            width: 70vw;
            height: 10vw;
            z-index: 4;
            font-size: 4.5vw;
            border-radius: 1vw;
            border: 2px ridge#D83F3D;
            top: 100vw;
            left: 15vw;
            padding-left: 2vw;
            margin: 0 auto;
        }
        .bgs{
            position: absolute;
            left: 0vw;
            padding: 0px;
            width: 100vw;
            margin: 0 auto;
            background: #FE805F;
        }
        .context_submit{
            position: absolute;
            width: 64vw;
            height: 9vw;
            z-index: 4;
            font-size: 4.5vw;
            border-radius: 1vw;
            border: 2px ridge#FE6968;
            background: #FE6968;
            top: 136vw;
            left: 18vw;
            color: white;
            font-weight: 500;
            margin: 0 auto;
        }
        .context_dunwlod{
            position: absolute;
            width: 30vw;
            left: 35vw;
            z-index: 4;
            font-size: 3vw;
            top: 148vw;
            text-align: center;
            margin: 0 auto;
        }

    </style>
    <script src="${base}/js/jquery-3.3.1.min.js"></script>
    <script>
        var count = 60; //间隔函数，1秒执行
        function getcode() {
            if (count != 60) {
                return
            }
            var phone = document.getElementById('ph').value;
            if (phone == '') {
                alert('请输入手机号');
                return;
            }
            ;
            ajx();
        }

        function time() {
            var l = document.getElementById('la');
            l.innerHTML = count + "S";
            count--;
            if (count == -1) {
                count = 60;
                l.innerHTML = '重新发送';
            } else {
                setTimeout(function () {
                    time();
                }, 1000);
            }
        }

        function ajx() {
            var phone = document.getElementById('ph').value;
            if (window.XMLHttpRequest)//如果有XMLHttpRequest，那就是非IE6浏览器。()里面加window的原因下面会有描述。
            {
                var oAjax = new XMLHttpRequest();//创建ajax对象
            }
            else//如果没有XMLHttpRequest，那就是IE6浏览器
            {
                var oAjax = new ActiveXObject("Microsoft.XMLHTTP");//IE6浏览器创建ajax对象
            }
            oAjax.open("POST", "http://www.tuoluo718.com/identify?loginIdent=" + phone + "&checkMobile=false", true);//加上t='+new Date().getTime()"的目的是为了消除缓存，每次的t的值不一样。
            //3.发送请求
            oAjax.send();
            //客户端和服务器端有交互的时候会调用onreadystatechange
            oAjax.onreadystatechange = function () {
                if (oAjax.readyState == 4) {
                    alert("验证码已发送");
                    time();
                }
            };
        };
        window.onload = function(){
            var u = navigator.userAgent, app = navigator.appVersion;
            var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
            $(".context_submit").bind('click',function(){
                var validator = check();
                if(validator){
                    $.ajax({
                        type: "POST",
                        url: "http://www.tuoluo718.com/cust/save",
                        dataType: 'json',
                        data: {
                            custMobile: $("#ph").val(),
                            inviterId: $("#fph").val(),
                            identifyingCode: $("#code").val(),
                            custPassword: $("#pass").val(),
                            web:"web"
                        },
                        success: function(data){
                            if(data.code == 1){
                                alert("注册成功！");
                                if (isAndroid) {
                                    window.location.href = "http://www.tuoluo718.com/toDownload";
                                }else{
                                    window.location.href = "https://itunes.apple.com/cn/app/金陀螺/id1435024102?mt=8";
                                }
                            }else {
                                if(data.message){
                                    alert(data.message);
                                }else {
                                    alert("注册失败！");
                                }
                            }
                        }
                    });
                }
            });
            $(".context_dunwlod").bind('click',function () {
                if (isAndroid) {
                    window.location.href = "http://www.tuoluo718.com/toDownload";
                }else{
                    window.location.href = "https://itunes.apple.com/cn/app/金陀螺/id1435024102?mt=8";
                }
            });
        }
        function check() {
            var phone = $("#ph").val();
            var code = $("#code").val();
            var pass = $("#pass").val();
            var fphone = $("#fph").val();

            if (phone == "" || code == "" || pass == "" || fphone == "") {
                alert("请认真填写资料");
                return false;
            }
            if(!isPoneAvailable(phone)){
                alert("手机号码格式错误!");
                return false;
            }
            return true;
        }
        function isPoneAvailable(phoneNo) {
            var myreg=/^[1][3,4,5,7,8,9][0-9]{9}$/;
            if (!myreg.test(phoneNo)) {
                return false;
            } else {
                return true;
            }
        }

    </script>
</head>
<body class="bgs">
<div class="bg_div">
    <img class="bg_div" src="${base}/images/123456.png">
    <img class="bg_img" src="${base}/images/654321.png">
    <#if inviteMobile?length==11>
    <div class="context_sp"> 邀请人手机号：${inviteMobile?substring(0,3)}****${inviteMobile?substring(7,11)}</div>
    <#else>
    <div class="context_sp"> 邀请人手机号：${inviteMobile}</div>
    </#if>

    <input class="context_sp" type="hidden" id="fph"  name="inviterId" value="${inviteMobile}"/>
    <input class="context_phone" id="ph" name="custMobile" type="number" placeholder="请输入手机号">
    <label class="context_getcode" id="la" onclick="getcode()" >获取验证码</label>
    <input class="context_code" id="code" name="identifyingCode" type="number" placeholder="请输入验证码">
    <input class="context_pass" id="pass" name="custPassword" type="password" placeholder="请输入密码">
    <button class="context_submit"">接受邀请</button>
    <div class="context_dunwlod"> 已有账号，<u>立即下载</u></div>
</div>
<#--<img class="i1" src="${base}/images/Group.png">
    <div class="f1">
        <img class="i2" src="${base}/images/bg.png">
        <input class="p1" id="ph" name="custMobile" type="number" placeholder="请输入手机号">
        <label class="btn1" id="la" onclick="getcode()" >获取验证码</label>
        <input class="p2" id="code" name="identifyingCode" type="number" placeholder="请输入验证码">
        <input class="p3" id="pass" name="custPassword" type="password" placeholder="请输入密码">
        <input class="p4" id="pass1" type="password" placeholder="请确认密码">
        <input class="p6" id="fph"  name="inviterId" value="${inviteMobile}"/>
        <button class="btn2"">立即注册</button>
    </div>
<div class="div1"> 已有账号，<u>立即下载</u></div>
<div class="b1">
    <img src="${base}/images/111.png" class="b2"/>
    <label>金陀螺</label>
</div>
<div class="b3">珍爱信用记录 享受财富人生</div>-->
</body>
</html>