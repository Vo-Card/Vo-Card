<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Homepage</title>

            <link rel="stylesheet" type="text/css" href="/css/global.css">
            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <link rel="stylesheet" type="text/css" href="/css/workspace.css">

            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
                integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
                crossorigin="anonymous">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
                crossorigin="anonymous"></script>
            <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/chart.js/dist/chart.umd.min.js"></script>
            <script src="/js/chartz.js" defer></script>
            <script type="module" src="/js/workspace/getPageInformation.js"></script>
            <!-- temp -->
            <style>
                :root {
                    --theme-color: #1b1d24;
                    --alert: #ff8989;
                }

                a {
                    text-decoration: none;
                    color: var(--primary-font-color);
                }
            </style>
        </head>

        <body style="background-color: var(--menu-primary-color);">

            <div id="backdrop">
                <div id="popup-container">
                    <div id="popup-title" style="display: flex;">
                        <h1 id="popup-title-text" style="font-size: 20px;"></h1>
                        <i class="nf nf-fa-close"></i>
                    </div>
                    <div id="popup-content-container">

                    </div>
                </div>
            </div>

            <jsp:include page="/components/layout/workspace-header.jsp" />

            <div class="workspace-container">
                <jsp:include page="/components/layout/workspace-sidebar.jsp" />

                <div id="splitter">
                    <div class="splitter-move-bar"></div>
                </div>

                <!-- end silder -->
                <div class="content-wrapper">
                    <div id="content">

                    </div>
                </div>

                <div id="information-container">
                    <!-- 
                        Information of the selection here
                        It will load from information component which contain informations
                        -->
                    <jsp:include page="/components/layout/workspace-rightbar.jsp" />
                </div>

            </div>
        </body>

        </html>