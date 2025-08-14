<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Homepage</title>
            <link rel="stylesheet" type="text/css" href="/css/style.css">

            <!-- temp -->
            <style>
                .d-block {
                    display: block;
                }

                .d-flex {
                    display: flex;
                }

                .gap-10 {
                    gap: 10px;
                }

                .voCardMain {
                    color: white;
                }

                .sideBar {
                    width: 180px;
                    overflow: hidden;
                    background-color: #242424;
                    align-items: center;
                    padding: 5px;
                }

                .voCardHead {
                    background-color: #242424;
                    padding: 5px;
                    justify-content: space-between;
                }

                .mainTopBar {
                    background-color: #242424;
                    width: 100%;
                    padding: 5px;
                    justify-content: space-between;
                }

                .topBarPage {
                    padding: 5px;
                }

                .user {
                    gap: 10px;
                    padding: 5px;
                }

                .slideOpener {
                    width: 5px;
                    height: 100vh;
                    background-color: red;
                }

                .sideItem {
                    padding: 7px;
                    gap: 10px;
                }

                .fakeIcon {
                    width: 18px;
                    height: 18px;
                    background-color: white;
                }

                .deckList {
                    padding: 5px;
                }
            </style>
        </head>

        <body>
            <!-- Component silde bar -->
            <div class="voCardMain d-flex">
                <div class="sideBar hidden">

                    <div class="d-block">
                        <div class="gap-10 d-flex voCardHead">
                            <div class="voCardHeadItems d-flex gap-10">
                                <div style="width: 20px; height: 20px; background-color: white;" class="sideImg"></div>
                                <div style="text-align: center;">Vo-Card</div>
                            </div>

                            <div class="hideButton"><img src="/imgs/whiteHamburger.png" alt="" width="24" height="24">
                            </div>
                        </div>

                        <ul class="homeList">
                            <li class="sideItem d-flex">
                                <div class="fakeIcon"></div>
                                <div>Home</div>
                            </li>
                            <li class="sideItem d-flex">
                                <div class="fakeIcon"></div>
                                <div>Dashboard</div>
                            </li>
                            <li class="sideItem d-flex">
                                <div class="fakeIcon"></div>
                                <div>Stat</div>
                            </li>
                            <li class="sideItem d-flex">
                                <div class="fakeIcon"></div>
                                <div>Learn</div>
                            </li>
                        </ul>

                        <div class="deckList">
                            <p>Decks</p>
                            <div class="historyDeck">

                                <ul class="historyMenu">
                                    <li class="history">A1</li>
                                    <li class="history">A2</li>
                                </ul>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="slideOpener"></div>
                <!-- Slide bar line end -->

                <div class="mainTopBar d-flex">
                    <div class="topBarPage">{currentPage}</div>
                    <div class="user d-flex">
                        <div><img src="/imgs/profileTemplate.png" alt="" class="profilePicture" height="20px"></div>
                        <div>${username}</div>
                    </div>
                </div>
            </div>

        </body>

        </html>