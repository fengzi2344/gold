<#assign base=request.contextPath />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>邀请有奖</title>
    <style>
        html, body {
            width: 100%;
            height: 100%;
        }

        .mui-content {
            background: url("${base}/images/lottery.jpg") bottom center no-repeat #efeff4;
            background-size: 100% 100%;
            width: 100%;
            height: 100%;
        }

        #first {
            position: absolute;
            top: 40.7%;
            left: 37%;
            font-size: 250px;
            color: #203261;
        }

        #second {
            position: absolute;
            top: 40.7%;
            left: 47%;
            font-size: 250px;
            color: #203261;
        }

        #third {
            position: absolute;
            top: 40.7%;
            left: 57%;
            font-size: 250px;
            color: #203261;
        }

        .btn {
            position: absolute;
            top: 86%;
            left: 48%;
            font-size: 60px;
            background: transparent;
            color: #ffffff;
            font-family: "Microsoft Yahei", "STHeiti", "SimSun", "Arail", "Verdana", "Helvetica", "sans-serif";
            cursor: pointer;
        }


    </style>
    <script src="${base}/js/jquery-3.3.1.min.js"></script>
    <script>
        var b = 140;
        window.onload = function () {
            if ('${num}') {
                b = '${num}' * 1;
            }
            fontS();
        }
        var n1, n2, n3;
        var t = null;

        function startR() {
            t = setInterval(function () {
                var num = Math.floor(Math.random() * b);
                n1 = Math.floor(num/100);
                n2 = Math.floor(num/10)%10;
                n3 = Math.floor(num%10);
                $("#first").html(n1);
                $("#second").html(n2);
                $("#third").html(n3);
            }, 50);
        }

        function stopR() {
            clearTimeout(t);
        }

        function sos(obj) {
            if ($(obj).text() == "开始") {
                $(obj).text("停止");
                startR();
            } else {
                $(obj).text("开始");
                stopR();
            }
        }

        var html = document.documentElement;

        // 封装一个函数  
        function fontS() {
            debugger;
            var hW = html.offsetWidth;
            var hS = hW / 7.68;
            $(".number").css("fontSize", hS + "px");
            var bhs = hW / 32;
            $(".btn").css("fontSize", bhs + "px");
        }

        // 当窗口大小改变时执行函数   
        window.onresize = function () {
            fontS();
        }
    </script>
</head>
<body>
<div class="mui-content">
    <div class="number" id="first" readonly="readonly">0</div>
    <div class="number" id="second" readonly="readonly">0</div>
    <div class="number" id="third" readonly="readonly">0</div>
    <div onclick="sos(this);" class="btn">开始</div>
</div>
</body>
</html>