<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>Home</title>

        <style>
            * {
                margin: 0;
                padding: 0;
            }

            .flex {
                display: flex;
            }

            .px-2 {
                padding-left: .5rem;
                padding-right: .5rem;
            }

            .p-2 {
                padding: .5rem;
            }

            .spitPage {
                display: flex;
                min-height: 100svh;
                width: 100%;
            }

            .slideBar {
                background-color: rgb(138, 138, 138);
            }
        </style>
    </head>

    <body>
        <div class="spitPage">

            <div class="slideBar">
                <div class="header p-2" style="background-color: rgb(49, 49, 49);">
                    <a href="#">
                        <p>${username}</p>
                    </a>
                </div>

                <div class="home px-2">
                    <ul>
                        <li>Home</li>
                        <li>Dashboard</li>
                        <li>Inbox</li>
                        <li>Stat</li>
                    </ul>
                </div>

                <div class="deckSelection px-2">
                    <div class="deck">Deck</div>

                    <div class="historyDeck">
                        <!-- {viewed deck} example-->
                        <div class="viewed">A1</div>
                        <div class="viewed">A2</div>
                    </div>
                </div>

                <div class="group-content p-2">
                    <ul>
                        <li>setting</li>
                        <li>log-out</li>
                    </ul>

                </div>

            </div>

            <div class="namePage flex">asd</div>
        </div>

        </div>
    </body>

    </html>