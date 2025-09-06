<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>example</title>
            <link rel="stylesheet" type="text/css" href="/css/style.css">

            <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <script src="/js/screen_correction.js" defer></script>

            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link
                href="https://fonts.googleapis.com/css2?family=Sansita:ital,wght@0,400;0,700;0,800;0,900;1,400;1,700;1,800;1,900&display=swap"
                rel="stylesheet">

            <!-- EXAMPLE dont do anything -->

            <style>
                :root {
                    --theme-color: #d8974d;
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
            </style>
        </head>

        <body>
            <jsp:include page="/components/main/nav-header.jsp">
                <jsp:param name="hasSession" value="${hasSession}" />
            </jsp:include>
        </body>

        </html>