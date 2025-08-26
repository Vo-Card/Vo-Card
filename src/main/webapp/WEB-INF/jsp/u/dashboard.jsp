<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
    <style>
        li { color: white; }
    </style>
</head>
<body>
<jsp:include page="header.jsp" />

<div class="spitPage">
    <div class="slideBar">
        <div class="username p-2">
            <ul class="menu">
                <li class="menuItem">
                    <a href="#" class="flex gap-2">
                        <div>
                            <img src="<c:url value='/imgs/profileTemplate.png'/>" 
                                 alt="" class="profilePicture" height="20px">
                        </div>
                        <div class="profileName">{hdiosahsahdsaihiaodshsda}</div>
                    </a>
                </li>
            </ul>
        </div>

        <div class="home p-2">
            <ul class="menu">
                <li class="menuItem">Home</li>
                <li class="menuItem">Dashboard</li>
                <li class="menuItem">Inbox</li>
                <li class="menuItem">Stat</li>
            </ul>
        </div>

        <div class="deckSelection p-2">
            <div class="deck">Deck</div>
            <div class="historyDeck p-2">
                <ul class="menu">
                    <li class="menuItem">A1</li>
                    <li class="menuItem">A2</li>
                </ul>
            </div>
        </div>

        <div class="slideBottom p-2">
            <ul class="menu">
                <li class="menuItem">Setting</li>
                <li class="menuItem">Log-out</li>
                <li class="menuItem">Username : ${username}</li>
            </ul>
        </div>
    </div>

    <header class="namePage flex p-2" style="background-color: rgb(40, 40, 40);">
        <div>
            <ul class="topBar">
                <button><li>sub</li></button>
                <li style="color: white;">Dashboard</li>
            </ul>
        </div>
    </header>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>
