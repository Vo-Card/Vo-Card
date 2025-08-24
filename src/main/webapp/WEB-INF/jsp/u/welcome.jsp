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
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
                integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
                crossorigin="anonymous">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
                crossorigin="anonymous"></script>

            <!-- temp -->
            <style>
                :root {
                    --theme-color: #1b1d24;
                    --primary-color: rgba(23, 23, 23, 0.2);
                    --alert: #ff8989;
                }

                body {
                    background-color: #494949;
                }

                ul {
                    list-style-type: none;
                }

                a {
                    text-decoration: none;
                    color: white;
                }

                div {
                    color: white;
                }

                .cover {
                    background-color: #242424;
                    width: 100%;
                    min-height: 100svh;
                    color: white;
                }

                .hamburger-button {
                    letter-spacing: 0;
                    font-weight: 500;
                    font-size: .875rem;
                    line-height: 1.25rem;
                    border-radius: .25rem;
                    white-space: nowarp;
                    white-space-collapse: collapse;
                    text-wrap-mode: nowrap;
                    width: 2.5rem;
                    height: 2.5rem;
                    background-color: rgba(0, 0, 0, 0%);
                    letter-spacing: 0;
                    border: 0;
                }

                .hamburger-button:hover {
                    background-color: rgba(152, 152, 152, 0.25);
                }

                .usr-prof {
                    width: fit-content;
                    height: 2.5rem;
                }

                .slide-bar {
                    background-color: #1F1F1F;
                    width: 260px;
                }

                .compo-color {
                    background-color: #1F1F1F;
                }

                .window {
                    background-color: red;
                    width: 5px;
                }

                .fakeIcon {
                    height: 24px;
                    width: 24px;
                    background-color: white;
                }

                .grid-container {
                    display: grid;
                    grid-template-columns: repeat(12, minmax(0, 1fr));
                    gap: 1.5rem;
                }

                .item1 {
                    grid-column: 2 / 7 span;
                    background-color: #0D0D0D;

                }

                .item2 {
                    grid-column: 9 / 3 span;
                    background-color: #0D0D0D;

                }

                .item3 {
                    grid-column: 2 / 10 span;
                    background-color: #0D0D0D;
                }

                .font-fixed {
                    font-size: 28px;
                }

                .card-wel {
                    transform: scale(1);
                }
            </style>
        </head>

        <body>
            <div class="d-flex h-100">
                <!-- slidebar component
                FIXME: <it's not sticky> 
                -->
                <div class="d-block hiddable slide-bar h-100 px-2 z-5">
                    <div class=" d-flex p-2 mx-2 gap-3 align-items-center">
                        <div class="icon" style="width: 28px; height: 28px; color: white;">
                        </div>
                        <div class="" style="font-size: 16px;">Vo-Card</div>
                    </div>

                    <div class="ps-1 d-flex flex-column overflow-auto w-100 min-w-0 position-relative gap-1"
                        style="flex: 1 1 0%;">
                        <div class="p-1 align-items-center d-flex">Home</div>

                        <ul class="gap-3 w-100 flex-column min-w-0 w-100 d-flex ps-1 mb-1">
                            <li class="d-flex  gap-2 align-items-center">
                                <a href="#"
                                    class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0 w-100">
                                    <div class="fakeIcon"></div>
                                    <span>Home</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Stat</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Learn</span>
                                </a>
                            </li>
                        </ul>

                        <div class="px-2 align-items-center d-flex p-2">Deck</div>

                        <ul class="gap-3 flex-column min-w-0 ps-1 w-100 d-flex">
                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Decks</span>
                                </a>
                            </li>
                        </ul>

                        <div class="align-items-center d-flex p-2">recently reviewed</div>
                        <ul class="gap-1 flex-column min-w-0 ps-1 w-100 d-flex py-2">
                            <li class="d-flex gap-2">
                                <a href="#"
                                    class="d-flex gap-2 position-position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <span>A1</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#"
                                    class="d-flex gap-2 position-position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <span>A2</span>
                                </a>
                            </li>
                        </ul>

                        <div class=" align-items-center d-flex p-2">Setting</div>
                        <ul class="gap-3 ps-1 flex-column min-w-0 w-100 d-flex ">
                            <li class="d-flex gap-2 ">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Setting</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>About me</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Contact</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 position-relative overflow-hidden p-1 w-100 min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Log-out</span>
                                </a>
                            </li>
                        </ul>

                    </div>
                </div>
                <div class="window"></div>

                <div class="d-flex w-100 flex-column min-h-svh flex-1 position-relative">
                    <header
                        class="w-100 d-flex align-items-center gap-2 shrink-0 top-0 sticky-top compo-color justify-content-between z-5">
                        <div class="d-flex align-items-center gap-2 px-4">
                            <button class="hamburger-button justify-content-between d-inline -ml-1">
                                <img src="/imgs/white-hamburger.png" alt="" style="width: 24px; height: 24px;">
                            </button>
                            <div class="d-flex align-items-center py-1">{current_page}</div>
                        </div>

                        <div class="ml-auto d-flex align-items-center justify-content-between gap-1">
                            <div class="usr-prof d-flex justify-content-between align-items-center gap-2 px-3">
                                <div class=""><img src="/imgs/profileTemplate.png" alt=""
                                        style="width: 24px; height: 24px;">
                                </div>
                                <div>${username}</div>
                            </div>
                        </div>
                    </header>
                    <!-- end silder -->

                    <main class="position-relative z-0 sm-pb-0 pb-8 px-2">
                        <div class="something grid-container my-4 mx-auto w-full">
                            <div class="item1">
                                <div class="pt-4 px-3 pb-2">
                                    <p class="font-fixed">{daily_welcome_message}, {username}!</p>
                                </div>
                                <div class="pb-2 px-3">
                                    <p>Create your own flashcard decks, review with spaced repetition, and track your
                                        progess .</p>
                                </div>
                                <div class="px-3 pb-2">
                                    <div class="d-flex gap-2">
                                        <a href="#">
                                            <p>Get Started</p>
                                        </a>
                                        <a href="#">
                                            <p>Create your own deck</p>
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <div class="item2 gap-2">
                                <div class="pt-4 px-3 pb-2">
                                    <p class="font-fixed">Your Today Card</p>
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
                            <section class="item3 h-100 p-2">
                                <div class="p-2">
                                    <p class="font-fixed">Stat</p>
                                </div>
                                <div class="p-2">
                                    <p>Showing your stat recently 7 days</p>
                                </div>
                                <!-- TODO: <Create chartzs> -->
                                <div class="p-4 px-2 justify-conten-between w-100 h-100"
                                    style="height: 250px; min-width: 0;">
                                    <div class="chartz">
                                        <canvas id="recent-chartz"></canvas>
                                    </div>
                                </div>
                            </section>

                        </div>
                    </main>
                </div>

            </div>
        </body>

        </html>