<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link rel="icon" href="<c:url value="/media/logo.ico" />" type="image/icon type">
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="<c:url value="/js/auth.js" />"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="WA001">
	<title>Add Restaurant</title>
</head>
<body>
<div class="page">
	<%@include file="../navbar.jsp" %>
	<div class="content-container">
		<div class="content">
			<header class="with_buttons" >
				<h1 class="form_title">Add Restaurant</h1>
				<div class="multiple_buttons">
					<input type="button" value="Add" id="add_button" class="section_button no_margin_button" />
				</div>
			</header>
			<%@include file="form-section.jsp" %>
		</div>
	</div>
	<script src="<c:url value="/js/restaurant/edit_add-restaurant.js" />"></script>
</div>

</body>
</html>
