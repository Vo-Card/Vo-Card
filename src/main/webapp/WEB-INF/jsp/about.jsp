<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>About Us</title>
            <link rel="stylesheet" type="text/css" href="/css/style.css">

            <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <link rel="stylesheet" type="text/css" href="/css/main.css">

            <script src="/js/screen_correction.js" defer></script>

            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link
                href="https://fonts.googleapis.com/css2?family=Sansita:ital,wght@0,400;0,700;0,800;0,900;1,400;1,700;1,800;1,900&display=swap"
                rel="stylesheet">

            <style>
                :root {
                    --theme-color: #648744;
                    --primary-color: #1b1d24;
                    --alert: #ff8989;
                }

                * {
                    color: white;
                }

                p {
                    margin: 0;
                    font-family: sarabun;
                    font-size: 20px;
                }

                .about-us {
                    text-align: center;
                    padding-top: 5px;
                    padding-bottom: 25px;
                    margin-left: 18%;
                    margin-right: 18%;
                }

                .text-decoration {
                    font-size: 64px;
                    font-family: Sansita;
                    background: linear-gradient(90deg, #999999 15%, #ffffff 50%, #999999 75%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    background-clip: text;
                    color: transparent;
                    padding-bottom: 15px;
                }
            </style>
        </head>

        <body>
            <jsp:include page="/components/main/nav-header.jsp">
                <jsp:param name="hasSession" value="${hasSession}" />
            </jsp:include>

            <div class="container">
                <div class="about-us">
                    <p class="text-decoration">About VoCard</p>
                    <p>Vo-Card is a web application designed to help users practice English using flashcards.</p>
                </div>
                <div class="about-us">
                    <p class="text-decoration">The VoCard’s Background</p>
                    <p>This project is a flashcard web application inspired by Anki, developed as part of my Year 2
                        course in Software- Engineer
                        The goal is to help users study and memorize using a spaced repetition system (SRS)
                        Users can create their own decks, publish for others to import.</p>
                </div>
            </div>
        </body>

        </html>