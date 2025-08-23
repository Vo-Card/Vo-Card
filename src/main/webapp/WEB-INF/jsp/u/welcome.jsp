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
            <link rel="stylesheet" type="text/css" href="/css/commonUse.css">

            <!-- temp -->
            <style>
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

                ul {
                    list-style: none;
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

                .h-fit {
                    height: fit-content;
                }

                .card-wel {
                    transform: scale(0.8);
                }
            </style>
        </head>

        <body>
            <div class="d-flex cover">
                <!-- slidebar -->
                <div class="d-block hiddable slide-bar px-2 z-9999">
                    <div class="d-flex p-2 gap-3 align-items-center">
                        <div class="icon" style="width: 28px; height: 28px; color: white;">
                        </div>
                        <div class="" style="font-size: 16px;">Vo-Card</div>
                    </div>

                    <div class="p-2 d-flex flex-column w-full min-w-0 relative gap-1">
                        <div class="p-2 align-items-center d-flex">Home</div>

                        <ul class="gap-2 w-full flex-column min-w-0 w-full d-flex ">
                            <li class="d-flex gap-2 align-items-center">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Home</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Stat</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Learn</span>
                                </a>
                            </li>
                        </ul>

                        <div class="px-2 align-items-center d-flex p-2">Deck</div>

                        <ul class="gap-2 w-full flex-column min-w-0 w-full d-flex">
                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Decks</span>
                                </a>
                            </li>
                        </ul>

                        <div class="align-items-center d-flex p-2">recently reviewed</div>
                        <ul class="gap-1 w-full flex-column min-w-0 w-full d-flex py-2">
                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <span>A1</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <span>A2</span>
                                </a>
                            </li>
                        </ul>

                        <div class=" align-items-center d-flex p-2">Setting</div>
                        <ul class="gap-2 w-full flex-column min-w-0 w-full d-flex ">
                            <li class="d-flex gap-2 ">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Setting</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>About me</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Contact</span>
                                </a>
                            </li>

                            <li class="d-flex gap-2">
                                <a href="#" class="d-flex gap-2 relative overflow-hidden p-1 w-full min-w-0">
                                    <div class="fakeIcon"></div>
                                    <span>Log-out</span>
                                </a>
                            </li>
                        </ul>

                    </div>
                </div>
                <div class="window"></div>

                <div class="d-flex w-full flex-column min-h-svh flex-1 relative">
                    <header class="w-full d-flex align-items-center gap-2 shrink-0 top-0 sticky compo-color z-9999">
                        <div class="d-flex align-items-center gap-2 px-4">
                            <button class="hamburger-button justify-content-center d-inline -ml-1">
                                <img src="/imgs/white-hamburger.png" alt="" style="width: 24px; height: 24px;">
                            </button>
                            <div class="d-flex align-items-center py-1">{current_page}</div>
                        </div>

                        <div class="ml-auto d-flex align-items-center justify-content-center gap-1">
                            <div class="usr-prof d-flex justify-content-center align-items-center gap-2 px-2">
                                <div class=""><img src="/imgs/profileTemplate.png" alt=""
                                        style="width: 24px; height: 24px;">
                                </div>
                                <div>{username}</div>
                            </div>
                        </div>
                    </header>

                    <main class="relative z-0 sm-pb-0 pb-8 px-2">
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


                            <section class="item3 h-full p-2">
                                <div class="p-2">
                                    <p class="font-fixed">Stat</p>
                                </div>
                                <div class="p-2">
                                    <p>Showing your stat recently 7 days</p>
                                </div>

                                <div class="charts" style="width: 100%; height: 100%; min-width: 0;"></div>
                            </section>

                        </div>
                    </main>
                </div>


            </div>
        </body>

        </html>