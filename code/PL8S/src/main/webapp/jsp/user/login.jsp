<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="icon" href="<c:url value="/media/logo.ico" />" type="image/icon type">
    <script src="<c:url value="/js/commons.js" />"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Page for logging in the user.">
    <meta name="author" content="WA001">
    <title>Login</title>
</head>
<body class="login_register">
    <div class="background_box_login">
        <h1 class="form_title_login"> Login </h1>
        <form class="input_form">

            <div class="text_block">
                <p class="instruction_text"> Email: </p>
                <input type="email" placeholder="Insert here..." class="text_input" id="email" autocomplete="on">
            </div>

            <div class="text_block">
                <p class="instruction_text"> Password: </p>
                <input type="password" placeholder="Insert here..." class="text_input" id="password" autocomplete="on">
                <span id="toggler" class="fa-solid fa-eye" aria-hidden="true">️</span>
            </div>
        </form>
        <div class="footer_login">
            <input type="button" value="Login" class="login_button" id="login_button">
            <p class="register_link">Don't have an account? <a href="register">Register</a></p>
        </div>
    </div>
    <script src="<c:url value="/js/user/login.js" />"></script>
</body>
</html>