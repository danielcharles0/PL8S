<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="WA001">
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="shortcut icon" href="<c:url value="/media/logo.ico" />" type="image/x-icon">
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="<c:url value="/js/auth.js" />"></script>
	<script src="<c:url value="/js/dish/common.js" />"></script>
	<script src="https://kit.fontawesome.com/9010b935e5.js" crossorigin="anonymous"></script>
	<title>Edit dish</title>
</head>
<body>
	<div class="page">

		<%@include file="../navbar.jsp" %>

		<div class="content-container">
			<div class="content">
				<article class="body">
					<header class="dish button_container">
						<h2 class="dish section_title">Dish edit</h2>
						<input type="submit" form="disheditform" value="Save" class="section_button" />
					</header>
					<hr />
					<form class="edit" action="#" method="PUT" id="disheditform">
						<div class="field">
							<span>Name :</span>
							<input required name="name" type="text" />
						</div>
						<div class="field">
							<span>Price :</span>
							<input required name="price" type="number" step="0.01" />
						</div>
						<div class="field">
							<span>Diet :</span>
							<select id="dishdiet" disabled>
								<option value="1">vegan</option>
								<option value="2">vegetarian</option>
								<option value="3">carnivorous</option>
							</select>
						</div>
						<div class="field ingredient">
							<span>Ingredients :</span>
							<ul id="ingredients"></ul>
							<input id="addingredient" type="button" value="add" class="section_button ingredient" />
						</div>
					</form>
				</article>
			</div>
		</div>
	</div>
	<script src="<c:url value="/js/dish/add.js" />"></script>
</body>
</html>