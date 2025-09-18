<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<head>
    <title>Feedback</title>

    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <link rel="stylesheet" type="text/css" href="/css/support/feedback.css">

    <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
    <link rel="stylesheet" type="text/css" href="/css/global.css">

    <script src="/js/screen_correction.js" defer></script>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link
        href="https://fonts.googleapis.com/css2?family=Sansita:ital,wght@0,400;0,700;0,800;0,900;1,400;1,700;1,800;1,900&display=swap"
        rel="stylesheet">


    <style>
        :root {
            --theme-color: #794BB6;
            --primary-color: #1b1d24;
            --alert: #ff8989;
        }

        p{
            color: black;
            box-sizing: border-box;
            margin: auto;
            font-family: sarabun;
            font-size: 20px;
        }
        h1 {
            color: white;
            font-size:60px;
            padding-bottom: 50px;
            padding-top: 100px;
            font-family: Sansita;

        }

    </style>
</head>

<body>
    <jsp:include page="/components/main/nav-header.jsp">
            <jsp:param name="hasSession" value="${hasSession}" />
    </jsp:include>

    <div class="feedback-container">
        <h1>Feedback</h1>
        
        <div >
            <p><input type="text" placeholder="Search" class="feedback-input"/></p>
        </div>

        <select class="heading-topic">
            <option>
                Heading Topic
            </option>
        </select>

        
        <div class="feedback">  
            <a href="feedback_page?type=1">
                <div>
                    <p>Performance & Accessibility</p>
                </div>

                <div>
                    <p>{P&A Post}</p>
                </div>
            </a>

            <a href="feedback_page?type=2">
                <div>
                    <p>Features</p>
                </div>

                <div>
                    <p>{P&A Post}</p>
                </div>
            </a>

            <a href="feedback_page?type=3">
                <div>
                    <p>Overall Satisfaction</p>
                </div>

                <div>
                    <p>{P&A Post}</p>
                </div>
            </a>

        </div>
        

        <div style="padding-top: 100px; padding-bottom: 10px; ">
            <p style="color: white;">Not find, what you were looking for?</p>
        </div>  
        
        <div style="padding-top: 10px; padding-bottom: 10px;"></div>
            <a href="feedback_newpost" style="background-color: #e1c89d; padding: 13px 28px; border: none; border-radius: 10px; color: black;">
                New Post
            </a>
        </div>
    </div>

</body>

</html>
