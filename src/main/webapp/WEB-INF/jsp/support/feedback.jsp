<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<head>
    <title>Feedback</title>

    <!-- CSS -->
    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
    <script src="/js/screen_correction.js" defer></script>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Sansita:wght@400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">


    <style>
        :root {
            --theme-color: #794BB6;
            --primary-color: #1b1d24;
            --alert: #ff8989;
        }

        * {
            box-sizing: border-box;
            font-family: 'Sansita', sans-serif;
            color: rgb(0, 0, 0);
        }
        p{
            box-sizing: border-box;
            font-family: 'Sansita', sans-serif;
            margin: auto;
            font-family: sarabun;
            font-size: 20px;
        }
        h1 , h4 {
            color: white;
        }

        body {
            margin: 0;
            padding: 0;
            background: linear-gradient(to bottom, #794BB6, #1b1d24);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .feedback-container {
            margin: auto;     
            width: 100%;
            max-width: 1200px;
            padding: 0 20px 80px;      
            text-align: center;
        }

        .feedback-container h1 {
            font-size:60px;
            padding-bottom: 50px;
            padding-top: 100px;
        }

        .feedback-container input[type="text"] {
            padding: 16px;
            padding-left: 50px;
            width: 100%;
            max-width: 1300px;   
            border-radius: 8px;
            border: none;
            display: block;
        }

        .feedback-container select {
            padding: 14px;
            
            width: 100%;
            max-width: 200px;   
            margin: 12px auto;
            margin-bottom: 120px;
            border-radius: 8px;
            border: none;
            display: block;
        }

        .feedback-buttons {
            display: flex;
            flex-wrap: nowrap;        
            justify-content: center;  
            gap: 30px;
            margin: 30px auto 25px;
            max-width: 600px;
        }

        .feedback-buttons button {
            display: flex;             
            flex-direction: column;   
            justify-content: space-between;  
            align-items: center;

            background-color: #e1c89d;
            padding: 25px 30px;
            border: none;
            border-radius: 12px;
            font-weight: bold;
            min-width: 250px;
            flex: 1 1 calc(33.333% - 30px);
            max-width: 200px;
            cursor: pointer;
            transition: transform 0.2s ease;

            
        }

        .feedback-buttons .btn-title {
            font-weight: 300;
            line-height: 1.4;
        }

        .feedback-buttons .btn-subtitle {
            font-weight: 200;
            padding-top: 17px;
        }


        .new-post-section {
            padding-top: 100px;
            padding-bottom: 10px;
        }



        .new-post-section button {
            background-color: #e1c89d;
            padding: 13px 28px;
            border: none;
            border-radius: 10px;
            font-weight: bold;
            cursor: pointer;
        }


        .search-box p{
            position: relative;
            width: 100%;
            max-width: 1000px;
            margin: 12px auto;
        }

        .search-box input [type="text"] {
            padding: 12px 12px 12px 30px; 
            width: 100%;
            border-radius: 8px;
            border: none;
            display: block;
        }

        .search-box .fa-search {
            position: absolute;
            top: 50%;
            left: 18px;
            transform: translateY(-50%);
            pointer-events: none;
        }
    

    </style>
</head>

<body>
    <jsp:include page="/components/main/nav-header.jsp">
        <jsp:param name="hasSession" value="${hasSession}" />
    </jsp:include>

    <div class="feedback-container">
        <h1>Feedback</h1>
        <div class="search-box">
            <p><i class="fa fa-search"></i><input type="text" placeholder="Search"/></p>
        </div>


        <select>
            <option>
                Heading Topic
            </option>
        </select>

        <div class="feedback-buttons">  
            <button>
                <div class="btn-title">
                    <p>Performance & Accessibility</p>
                </div>

                <div class="btn-subtitle">
                    <p>{P&A Post}</p>
                </div>
            </button>


            <button>
                <div class="btn-title">
                    <p>Features</p>
                </div>

                <div class="btn-subtitle">
                    <p>{P&A Post}</p>
                </div>
            </button>

            <button>
                <div class="btn-title">
                    <p>Overall Satisfaction</p>
                </div>

                <div class="btn-subtitle">
                    <p>{P&A Post}</p>
                </div>
            </button>

        </div>
        

        <div class="new-post-section">
            <p><h4>Not find, what you were looking for?</h4></p>
            
            <button>
                <p>New Post</p>
            </button>
        </div>
    </div>

</body>

</html>
