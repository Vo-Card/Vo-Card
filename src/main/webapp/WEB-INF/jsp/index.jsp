<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Vo Card</title>
    <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <script src="/js/screen_correction.js" defer></script>
    
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
            <p style="color: #999999; text-align: center;">A flashcard platform that helps users create, organize, and study digital flashcards. Ideal for students, teachers, and independent learners. <br>Supports custom decks, smart review features, and cross-device syncing.</p>
        </div>
        <div class="preview" id="card_preview">
            <div class="deck default a1" style="transform:rotate(-30deg) translate(100px,140px); z-index: 1;">
                <div class="background">
                    <p class="category">A1</p>
                    <div class="decorations">
                        <div>
                            <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
            <div class="deck default a2" style="transform:rotate(-10deg) translate(50px,0px); z-index: 2;">
                <div class="background">
                    <p class="category">A2</p>
                    <div class="decorations">
                        <div>
                            <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
            <div class="deck default b1" style="transform:rotate(0deg) translate(0,-50px); z-index: 3;">
                <div class="background">
                    <p class="category">B1</p>
                    <div class="decorations">
                        <div>
                            <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
            <div class="deck default b2" style="transform:rotate(10deg) translate(-50px,0px); z-index: 2;">
                <div class="background">
                    <p class="category">B2</p>
                    <div class="decorations">
                        <div>
                            <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
            <div class="deck default c1" style="transform:rotate(30deg) translate(-100px,140px); z-index: 1;">
                <div class="background">
                    <p class="category">C1</p>
                    <div class="decorations">
                        <div>
                            <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
        </div>
    </div>
</body>
</html>
