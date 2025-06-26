<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>Home</title>

        <link rel="stylesheet" type="text/css" href="/css/style.css">
        <style>
            li {
                color: white;
            }
        </style>
    </head>

    <body>
        <div class="spitPage">

            <div class="slideBar">
                <div class="username p-2">
                    <ul class="menu">
                        <li class="menuItem">
                            <a href="#" class=" flex gap-2">
                                <div><img src="/resources/profileTemplate.png" alt="" class="profilePicture" height="20px"></div>
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
                        <!-- {viewed deck} example-->
                        <ul class="menu">
                            <li class="menuItem">A1</li>
                            <li class="menuItem">A2</li>
                        </ul>
                    </div>
                </div>

                <div class="slideBottom p-2">
                    <ul class="menu">
                        <li class="menuItem">setting</li>
                        <li class="menuItem">log-out</li>
                        <li class="menuItem">${username}</li>
                    </ul>
                </div>

            </div>

            <header class="namePage flex p-2" style="background-color: rgb(40, 40, 40);">
                <div class="">
                    <ul class="topBar">
                        <button>
                            <li>sub</li>
                        </button>
                        <!-- {Current Page name} -->
                        <li style="color: white;">Home</li>
                    </ul>
                </div>

            </header>
        </div>

        </div>
    </body>

    </html>