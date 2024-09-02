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
	<script src="https://kit.fontawesome.com/9010b935e5.js" crossorigin="anonymous"></script>
	<script src="<c:url value="/js/dish/common.js" />"></script>
	<title>Edit dish</title>
</head>
<body>
	<div class="page">

		<%@include file="../navbar.jsp" %>

		<div class="content-container">
			<div class="content">
				<c:choose>
					<c:when test="${not empty dish && !message.error}">
						<script>
							const restaurant_id = ${dish.restaurant};
							console.log("Restaurant: " + restaurant_id);
						</script>
						<article class="body">
							<header class="dish button_container">
								<h2 class="dish section_title">Dish edit</h2>
								<input type="submit" form="disheditform" value="Save" class="section_button" />
								<input id="deletedish" type="button" value="Delete" class="section_button side_button" />
							</header>
							<hr />
							<form class="edit" action="#" method="PUT" id="disheditform">
								<div class="field">
									<span>Name :</span>
									<input required name="name" value="<c:out value="${dish.name}"/>" type="text" />
								</div>
								<div class="field">
									<span>Price :</span>
									<input required name="price" value="<c:out value="${dish.price}"/>" type="number" step="0.01" />
								</div>
								<div class="field">
									<span>Diet :</span>
									<select id="dishdiet" disabled>
										<option <c:if test="${dish.diet == 'vegan'}">selected</c:if> value="1">vegan</option>
										<option <c:if test="${dish.diet == 'vegetarian'}">selected</c:if> value="2">vegetarian</option>
										<option <c:if test="${dish.diet == 'carnivorous'}">selected</c:if> value="3">carnivorous</option>
									</select>
								</div>
								<div class="field ingredient">
									<span>Ingredients :</span>
									<ul id="ingredients">
									 	<c:forEach items="${dish.ingredients}" var="ing" begin="0">
						         			<li>
												<input type="text" class="ingredient" required name="ingredients" value="<c:out value = "${ing.name}"/>" /><%--
												--%><select required name="diets" class="ingredient">
													<option <c:if test="${ing.diet == 'vegan'}">selected</c:if> value="vegan">vegan</option>
													<option <c:if test="${ing.diet == 'vegetarian'}">selected</c:if> value="vegetarian">vegetarian</option>
													<option <c:if test="${ing.diet == 'carnivorous'}">selected</c:if> value="carnivorous">carnivorous</option>
												</select><%--
												--%><i class="fa-solid fa-trash"></i>
											</li>
										</c:forEach>
									</ul>
									<input id="addingredient" type="button" value="add" class="section_button ingredient" />
								</div>
							</form>
						</article>
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
	<script src="<c:url value="/js/dish/edit.js" />"></script>
</body>
</html>