<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
<head>
    <title>Regist Card•</title>
</head>
<body>
<form action="https://fast.jfpays.com/rest/api/710002" method="post" id="openForm">
    <input id="requestTime" name="encryptData"  value="<%=request.getAttribute("encryptData") %>" /><br/>
    <input id="version" name="partnerNo"  value="<%=request.getAttribute("partnerNo") %>" /><br/>
    <input id="merchantNo" name="signData"  value="<%=request.getAttribute("signData") %>" /><br/>
    <input id="requestData" name="orderId"  value="<%=request.getAttribute("orderId") %>" /><br/>
</form>
</body>
<script type="text/javascript">
  //  document.all.openForm.submit();
</script>
</html>