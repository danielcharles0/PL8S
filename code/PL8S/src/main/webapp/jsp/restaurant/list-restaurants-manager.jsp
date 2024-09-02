<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="/css/main.css" />">
    <link rel="stylesheet" href="<c:url value="/css/custom-table.css" />">
    <link rel="icon" href="<c:url value="/media/logo.ico" />" type="image/icon type">
    <script src="<c:url value="/js/commons.js" />"></script>
    <script src="<c:url value="/js/auth.js" />"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Page for viewing restaurants relative to a specific manager.">
    <meta name="author" content="WA001">
    <title>Restaurants</title>
</head>
<body>
<div class="page">
    <%@include file="../navbar.jsp" %>
    <div class="content-container">
        <div class="content">
            <header class="with_buttons" >
                <h1 class="form_title">Restaurants</h1>
                <div class="multiple_buttons" id="button_container">
                    <input type="button" value="Add" class="section_button joined_button" id="add_button"/>
                </div>
            </header>
            <hr>
            <article>
                <table class="list">
                    <thead>
                    <tr>
                        <th id="name_column">Name</th>
                        <th>Hours</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody id="table_body">

                    </tbody>
                </table>
            </article>
        </div>
    </div>
</div>
<script src="<c:url value="/js/restaurant/list_restaurants_manager.js" />"></script>
</body>
</html>