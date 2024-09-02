<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<link rel="icon" href="<c:url value="/media/logo.ico" />" type="image/icon type">
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="Page for viewing the dishes served by a specific restaurant.">
	<meta name="author" content="WA001">
	<title>Dishes</title>
</head>
<body>
	<div class="page">
		<%@include file="../navbar.jsp" %>
		<div class="content-container">
			<div class="content">
				<header class="with-back-button">
					<h1 class="form_title" id="restaurant_name"></h1>
				</header>
				<hr class="full">
				<section class="search_bar_section">
					<form class="search-form" onsubmit="return false;">
						<input type="search"  name="name" id="search-box" placeholder="Search for a dish" class="search_input">
						<input type="submit" value="Search" class="section_button no_margin_button" id="search_button"/>
					</form>
					<form class="search-advanced-option">
						<div class="diet-radio-block">
							<label class="important">Diet:</label>
							<input type="radio" id="diet_all" name="diet" value="all" checked="checked">
							<label for="diet_all">All</label>
							<input type="radio" id="diet_vegan" name="diet" value="vegan">
							<label for="diet_vegan">Vegan</label>
							<input type="radio" id="diet_vegetarian" name="diet" value="vegetarian">
							<label for="diet_vegetarian">Vegetarian</label>
						</div>
						<div>
							<label class="important">Sort by name</label>
							<a class="icon-buttons" onclick="changeIcon(this)" id="sort_button">
								<i class="fa-solid fa-arrow-up-a-z sort-icon"></i>
							</a>
						</div>
					</form>
				</section>
				<section class="card-container" id="card_container">

				</section>
			</div>
		</div>
	</div>
	<script src="<c:url value="/js/sort_by_name.js" />"></script>
	<script src="<c:url value="/js/dish/dish_search.js" />"></script>
</body>
</html>

