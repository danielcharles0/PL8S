<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="author" content="WA001">
	<link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="shortcut icon" href="<c:url value="/media/logo.ico" />" type="image/x-icon">
	<script src="https://kit.fontawesome.com/9010b935e5.js" crossorigin="anonymous"></script>
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="<c:url value="/js/auth.js" />"></script>
	<title>User list</title>
</head>
<body>
	<div class="page">
		<%@include file="../navbar.jsp" %>
		<div class="content-container">
			<div id="content" class="content">
				<header class="button_container search_container">
					<form id="usersearch" action="#">
						<input class="section_search noleftmargin" type="search" name="email" placeholder="SEARCH FOR A USER BY EMAIL" />
						<input type="submit" value="Search" class="section_button search_button" />
					</form>
				</header>
				<article>
					<header class="button_container">
						<h2 class="section_title">Manager List</h2>
						<input id="addmanager" type="button" value="Add" class="section_button" />
					</header>
					<hr />
					<div class="table_container">
						<table class="list">
							<thead>
								<tr>
									<th>First Name</th>
									<th>Last Name</th>
									<th>Email</th>
									<th>Stripe Id</th>
									<%-- <th></th> --%>
									<th></th>
								</tr>
							</thead>
							<tbody id="managers"></tbody>
						</table>
					</div>
				</article>
				<article>
					<header class="button_container">
						<h2 class="section_title">Customer List</h2>
					</header>
					<hr />
					<div class="table_container">
						<table class="list">
							<thead>
								<tr>
									<th>First Name</th>
									<th>Last Name</th>
									<th>Email</th>
									<th>Stripe Id</th>
									<th></th>
								</tr>
							</thead>
							<tbody id="customers"></tbody>
						</table>
					</div>
				</article>
				<script src="<c:url value="/js/user/userlist.js" />"></script>
			</div>
		</div>
	</div>
</body>
</html>