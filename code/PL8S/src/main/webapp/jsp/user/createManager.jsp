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
  <meta name="description" content="Page for creating a new manager.">
  <meta name="author" content="WA001">
  <title>Create Manager</title>
</head>
<body>
  <div class="page">

    <%@include file="../navbar.jsp" %>

    <div class="content-container">
      <div class="content">
        <header class="with_buttons">
          <h1 class="form_title">Create Manager</h1>
          <div class="multiple_buttons">
            <input type="button" value="Create" class="section_button " id="create_button">
          </div>
        </header>
        <hr>
        <div class="info_panel">
          <form class="input_form">
            <div class="input_block">
              <div class="text_block">
                <p class="instruction_text"> First Name: </p>
                <input type="text" placeholder="Insert here..." class="text_input" id="firstname">
              </div>
            </div>
            <div class="input_block">
              <div class="text_block">
                <p class="instruction_text"> Last Name: </p>
                <input type="text" placeholder="Insert here..." class="text_input" id="lastname">
              </div>
            </div>
            <div class="input_block">
              <div class="text_block">
                <p class="instruction_text"> Email: </p>
                <input type="email" placeholder="Insert here..." class="text_input" id="email" autocomplete="off">
              </div>
            </div>
            <div class="input_block">
              <div class="text_block">
                <p class="instruction_text"> Password: </p>
                <input type="password" placeholder="Insert here..." class="text_input" id="password" autocomplete="new-password">
                <span id="toggler" class="fa-solid fa-eye" aria-hidden="true">️</span>
              </div>
            </div>
            <div class="input_block">
              <div class="text_block">
                <p class="instruction_text"> Repeat Password: </p>
                <input type="password" placeholder="Insert here..." class="text_input" id="repeatPassword" autocomplete="new-password">
                <span id="repeatToggler" class="fa-solid fa-eye" aria-hidden="true"></span>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
  <script src="<c:url value="/js/user/createManager.js" />"></script>
</body>
</html>