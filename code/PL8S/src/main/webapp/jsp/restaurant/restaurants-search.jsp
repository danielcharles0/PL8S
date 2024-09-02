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
	<meta name="description" content="Page for viewing the restaurants present at the festival.">
	<meta name="author" content="WA001">
	<title>Restaurants</title>
</head>
<body>
	<div class="page">
		<%@include file="../navbar.jsp" %>
		<div class="content-container">
			<div class="content">
				<header>
					<h1 class="form_title">Restaurants</h1>
				</header>
				<hr class="full">
				<section>
					<form class="search-form" onsubmit="return false;">
						<input type="search"  name="name" id="search-box" placeholder="Search for a restaurant" class="search_input">
						<input type="button" value="Search" class="section_button no_margin_button" id="search_button"/>
					</form>
					<form class="search-advanced-option">
						<div>
							<label class="important" for="select_cuisine_type">Cuisine Types:</label>
							<select name="type" id="select_cuisine_type"  class="input_element">
								<option value="" disabled selected>Pick a type</option>
								<option value="">All</option>
							</select>
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
	<script src="<c:url value="/js/restaurant/restaurant_search.js" />"></script>
</body>
</html>

