<!DOCTYPE html>

<html>
<head>
    <title>Regist Card</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"> 
</head>
<body>
<#--表单隐藏-->
<form action="https://fast.jfpays.com/rest/api/710002" method="post" id="openForm">
    <input type="hidden" id="requestTime" name="encryptData"  value="${encryptData}" /><br/>
    <input type="hidden" id="version" name="partnerNo"  value="${partnerNo}" /><br/>
    <input type="hidden" id="merchantNo" name="signData"  value="${signData}" /><br/>
    <input type="hidden" id="requestData" name="orderId"  value="${orderId}" /><br/>
</form>
</body>
<script type="text/javascript">
    document.all.openForm.submit();
</script>
</html>