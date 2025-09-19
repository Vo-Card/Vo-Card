<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<head>
    <title>Feedback</title>

    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <link rel="stylesheet" type="text/css" href="/css/support/feedback_newpost.css">
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

        h3  {
            color: rgb(255, 255, 255);
            box-sizing: border-box;
            padding-top: 5px; 
            padding-bottom: 10px; 
            text-align: left ;
            margin: auto;
            font-family: sarabun;
        }
        p {
            color: white;
            padding-top: 5px; 
            padding-bottom: 10px; 
            text-align: left ;
            margin: auto;
            font-family: sarabun;
            font-size: 16px;
            
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
        

        <h3>What your post is about?</h3>

        <p>Title</p>
        <div >
            <input type="text" placeholder="Title" class="input-text"/>
        </div>


        <p>Description</p>
        <div >
            <textarea placeholder="Description" class="textarea"></textarea>
        </div>


        <p>Topic</p>
        <div >
            <input type="text" placeholder="Topic" class="input-text"/>
        </div>

        

        

          
        
        <div style="padding-top: 50px; padding-bottom: 10px;"></div>
            <a href="feedback" class="submit">
                Submit
            </a>
        </div>
    </div>

</body>

</html>
