<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Home</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <script src="/js/bg_update_correction.js" defer></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Sansita:ital,wght@0,400;0,700;0,800;0,900;1,400;1,700;1,800;1,900&display=swap" rel="stylesheet">
</head>
<body>
    <nav>
        <ul class="left">
            <div class="icon"></div>
            <li><a href="/home" style="font-size: 25px; border-right: solid white 2px; padding-right: 20px;">Vo-Card</a></li>
            <li><a href="/explore">Explore</a></li>
            <li><a href="/products">Products</a></li>
            <li><a href="/aboutus">About Us</a></li>
            <li><a href="/contact">Contact</a></li>
            <li><a href="/support">Support</a></li>
        </ul>
        <ul class="right">
             <c:choose>
                <c:when test="${not empty username}">
                    <li><a href="/u/dashboard" class="opeb_btn">Open Vo-Card</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="/login" class="lgn_btn">Login</a></li>
                    <li><a href="/register" class="reg_btn">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>

    <div class="container">
        <div class="slogan">
            <h1 style="color: white;">Turn your <span style="color: #dd4949;">English</span> goals into reality <br> discover the power of smart <span style="color: #4d6dd8;">flashcards</span></h1>
            <p style="color: #999999;">Flashcard description place holder</p>
        </div>
    </div>
</body>
</html>
