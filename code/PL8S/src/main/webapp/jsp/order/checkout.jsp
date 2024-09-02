<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <title>Accept a payment</title>
    <meta name="description" content="A demo of a payment on Stripe" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="author" content="WA001">
	<link rel="icon" href="../../media/logo.ico" type="image/x-icon">
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
	<link rel="stylesheet" href="<c:url value="/css/checkout.css" />" />
    <script src="https://js.stripe.com/v3/"></script>
    <script src="<c:url value="/js/checkout.js" />" defer></script>
	<script src="<c:url value="/js/commons.js" />"></script>
	<script src="<c:url value="/js/auth.js" />"></script>
  </head>
  <body>
    <!-- Display a payment form -->
    <form id="payment-form">
		<h3 id="checkout"></h3>
		<div id="payment-element">
			<!--Stripe.js injects the Payment Element-->
		</div>
		<button id="submit">
			<div class="spinner hidden" id="spinner"></div>
			<span id="button-text">Pay now</span>
		</button>
		<div id="payment-message" class="hidden"></div>
    </form>
  </body>
</html>