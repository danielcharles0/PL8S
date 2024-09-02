<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="icon" href="../../media/logo.ico" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="WA001">
    <script src="<c:url value="/js/commons.js" />"></script>
    <script src="<c:url value="/js/auth.js" />"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <title>Cart</title>
</head>
<body>
<div class="page">
    <%@include file="../navbar.jsp" %>
    <div class="content-container">
        <div class="content" id="cart-container">
            <h1>Your Cart</h1>
            <hr>
        </div>
    </div>
</div>
<script src="<c:url value="/js/order/cart.js" />"></script>
</body>
</html>

