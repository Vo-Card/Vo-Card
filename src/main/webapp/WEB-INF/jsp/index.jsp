<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="/css/style.css">

        <style>
            .header {
                background-color: rgb(39, 39, 39);
                padding-left: 50px;
                padding-right: 50px;
                padding-top: 20px;
                padding-bottom: 10px;
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
            }

            .leftContainer {
                display: flex;
                flex-direction: row;
                gap: 10px;
            }

            .inner {
                display: flex;
                gap: 25px;
                flex-direction: row;
                list-style-type: none;
                text-align: center;
                color: white;
            }

            .mainContentOne {
                display: block;
                margin-left: auto;
                margin-right: auto;
            }
        </style>
    </head>

    <body>
        <div class="header">
            <div class="leftContainer">
                <div class="picture"><img src="/resources/profileTemplate.png" alt="" style="width: 20px;"></div>
                <div class="text" style="color: white;">Vo-Card</div>
            </div>

            <div class="rightContainer">
                <div>
                    <ul class="inner">
                        <li>Explore</li>
                        <li>Product</li>
                        <li>Sign in</li>
                        <li>Log in</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="mainContentOne">
            <div class="cards"></div>
            <div class="exploreContainer">
                <div class="exploreInner">
                    <div>
                        <h1>ello</h1>
                    </div>
                </div>
            </div>
        </div>

    </body>

    </html>