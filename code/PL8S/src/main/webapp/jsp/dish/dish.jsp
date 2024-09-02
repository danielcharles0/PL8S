<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="<c:url value="/css/main.css" />">
		<link rel="shortcut icon" href="<c:url value="/media/logo.ico" />" type="image/x-icon">
		<script src="<c:url value="/js/commons.js" />"></script>
		<script src="<c:url value="/js/auth.js" />"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="WA001">
		<title>Dish</title>
	</head>
	<body>
		<div class="page">

			<%@include file="../navbar.jsp" %>

			<div class="content-container">
				<div class="content">
					<c:choose>
						<c:when test="${not empty dish && !message.error}">
							<h1 class="title">Dish Statistics</h1>
							<hr>
							<article>
								<header class="button_container">
									<h2 class="section_title">Dish details :</h2>
									<a class="section_button" href="<c:url value="/dish/${dish.dish_id}/edit" />">Edit dish</a>
								</header>
								<div class="table_container">
									<table class="details">
										<thead>
											<tr>
												<th>Dish ID :</th>
												<th>Dish Name :</th>
												<th>Dish Price :</th>
												<th>Diet :</th>
												<th>Ingredients :</th>
												<th>Availability :</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><c:out value="${dish.dish_id}"/></td>
												<td><c:out value="${dish.name}"/></td>
												<td><c:out value="${dish.price}"/> &#8364;</td>
												<td><c:out value="${dish.diet}"/></td>
												<td>
													<c:out value="${dish.ingredients[0].name}"/><%--
												 	--%><c:forEach items="${dish.ingredients}" var="ing" begin="1"><%--
										         		--%><c:out value = ", ${ing.name}"/><%--
													--%></c:forEach>
												</td>
												<td>Available</td>
											</tr>
										</tbody>
									</table>
								</div>
							</article>
							<article>
								<header class="button_container">
									<h2 class="section_title">Orders of this dish :</h2>
									<input type="button" value="View orders" class="section_button" id="vieworders" />
								</header>
								<div class="table_container">
									<table class="list hidden" id="dishorderstable">
										<thead>
											<tr>
												<th>Order ID</th>
												<th>Customer ID</th>
												<th>Status</th>
												<th>Date</th>
												<th>Placed On</th>
											</tr>
										</thead>
										<tbody id="dishorders"></tbody>
									</table>
								</div>
							</article>
							<script src="<c:url value="/js/dish/dish.js" />"></script>
						</c:when>
						<c:otherwise>
							<!-- display the message -->
							<script>
								window.addEventListener("load", () => {
									const message = {
										"message": "<c:out value="${message.message}"/>",
										"error-code": "<c:out value="${message.errorCode}"/>",
										"error-details": "<c:out value="${message.errorDetails}"/>"
									};

									raiseError(message);
								});
							</script>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</body>
</html>