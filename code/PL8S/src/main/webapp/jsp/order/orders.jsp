<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="WA001">
	<title>Orders</title>
	<link rel="icon" href="<c:url value="/media/logo.ico" />" type="image/x-icon">
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<script src="https://kit.fontawesome.com/9010b935e5.js" crossorigin="anonymous"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="<c:url value="/js/auth.js" />"></script>
</head>
<body>
	<div class="page">

		<%@include file="../navbar.jsp" %>
		
		<div class="content-container">
			<div id="content" class="content">
				<h1 class="title">Previous Orders</h1>
				<hr />
			</div>
		</div>
	</div>
	<script src="<c:url value="/js/order/orders.js" />"></script>
</body>
</html>