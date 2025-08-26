<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Document</title>
            <link rel="stylesheet" type="text/css" href="/css/card_theme.css">
            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <link rel="stylesheet" type="text/css" href="/css/main.css">

            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
                integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
                crossorigin="anonymous">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
                crossorigin="anonymous"></script>
            <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/chart.js/dist/chart.umd.min.js"></script>

            <style>
                :root {
                    --theme-color: #1b1d24;
                    --primary-color: rgba(23, 23, 23, 0.2);
                    --alert: #ff8989;
                }

                body {
                    background-color: #1b1d24;
                    color: white;
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

                <div class="content-wrapper">
                    <div id="content">
                        <!-- main page -->
                        <div class="">
                            
                        </div>
                    </div>
                </div>
                <script src="/js/menu-splitter.js"></script>
        </body>

        </html>