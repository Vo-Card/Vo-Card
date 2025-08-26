<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Homepage</title>

            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <link rel="stylesheet" type="text/css" href="/css/card_theme.css">
            <link rel="stylesheet" type="text/css" href="/css/main.css">

            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
                integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
                crossorigin="anonymous">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
                crossorigin="anonymous"></script>
            <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/chart.js/dist/chart.umd.min.js"></script>
            <script src="/js/chartz.js" defer></script>

            <!-- temp -->
            <style>
                :root {
                    --theme-color: #1b1d24;
                    --primary-color: rgba(23, 23, 23, 0.2);
                    --alert: #ff8989;
                }
                a {
                    text-decoration: none;
                    color: var(--primary-font-color);
                }
            </style>
        </head>

        <body style="background-color: var(--menu-primary-color);">
                
                <jsp:include page="/components/menuHeader.jsp" />

                <div class="window"></div>
                
                <div class="workspace-container">
                    <jsp:include page="/components/menuBar.jsp" />

                    <div id="splitter">
                        <div class="splitter-move-bar"></div>
                    </div>

                    <!-- end silder -->
                    <div class="content-wrapper">
                        <div id="content">
                            <div class="grid-container">
                                <div class="greeting content-container">
                                    <p class="font-fixed">{daily_welcome_message}, {username}!</p>
                                    <p>Create your own flashcard decks, review with spaced repetition, and track
                                        your
                                        progess .</p>
                                    <div class="px-3 pb-2">
                                        <div class="d-flex gap-2">
                                            <a href="#">
                                                <p>[ Get Started ]</p>
                                            </a>
                                            <a href="#">
                                                <p>[ Create your own deck ]</p>
                                            </a>
                                        </div>
                                    </div>
                                </div>
    
                                <div class="daily content-container">
                                    <div class="pt-4  pb-2">
                                        <p class="font-fixed text-center">Your Today Card</p>
                                    </div>
    
                                    <div class="preview card-wel" id="card_preview">
                                        <div class="deck default a1" style="z-index: 1;">
                                            <div class="background">
                                                <p class="category">A1</p>
                                                <div class="decorations">
                                                    <div>
                                                        <span
                                                            style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                                                        <span
                                                            style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                                                        <span
                                                            style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                                                    </div>
                                                    <span
                                                        style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                                                </div>
                                            </div>
                                            <p class="word">Something</p>
                                        </div>
                                    </div>
                                </div>
    
                                <!-- chart recently 7 days -->
                                <section class="stats content-container">
                                    <div class="light"></div>
                                    <div class="p-2 pt-3 ps-2">
                                        <p class="font-fixed">Stat</p>
                                    </div>
                                    <div class="p-2 ps-3">
                                        <p>Showing your stat recently 7 days</p>
                                    </div>
                                    <!-- TODO: <Create charts> -->
                                    <canvas class="p-3 px-2 w-100" id="chartz" style="height: 300px;"></canvas>
                                </section>
    
                            </div>
                        </div>
                    </div>
                </div>
            <script src="/js/menu-splitter.js"></script>
        </body>

        </html>