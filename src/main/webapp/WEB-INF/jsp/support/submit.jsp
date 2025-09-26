<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

<head>
    <title>Submit</title>

        <link rel="icon" type="image/x-icon" href="/imgs/icon.svg">
        <link rel="stylesheet" type="text/css" href="/css/style.css">
        <link rel="stylesheet" type="text/css" href="/css/global.css">
    
        <script src="/js/screen_correction.js" defer></script>
        <script src="/js/support_form.js"></script>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link
            href="https://fonts.googleapis.com/css2?family=Sansita:ital,wght@0,400;0,700;0,800;0,900;1,400;1,700;1,800;1,900&display=swap"
            rel="stylesheet">

    <style>
        :root {
            --theme-color: #bc4a6b;
            --primary-color: #1b1d24;
            --alert: #ff8989;
        }

        p {
            margin: 0;
            font-family: sarabun;
            font-size: 20px;
        }

        .Submit {
            text-align: center;
            padding-top: 5px;
            padding-bottom: 25px;
            margin-left: 18%;
            margin-right: 18%;
        }

        .text-decoration {
            font-size: 64px;
            font-family: Sansita;
            background: linear-gradient(90deg, #999999 15%, #ffffff 50%, #999999 75%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            color: transparent;
            padding-bottom: 15px;
        }

        .custom-helps-page-text{
            color: white;
            font-size: large;
        }

        .custom-select {
            font-size: medium;
            height: 40px;
            width: 800px;          
            border-radius: 5px;
            color: black !important;          
            padding: 5px 10px;         
        }

        .custom-select option{
            color: black !important;
            background-color: white;
        }

        .hidden {
            display: none;
        }

        input{
            font-size: medium;
            height: 40px;
            width: 800px;          
            border-radius: 5px;
            color: black !important;          
            padding: 5px 10px;
            margin-top: 5px;
        }

        textarea {
            resize: none;
            font-size: medium;
            height: 124px;
            width: 800px;
            color: black !important;
            padding: 5px 10px;
            margin-top: 5px;   
        }

        .submit-bt{
            font-size: large;
            height: 44px;
            width: 156px; 
            color: black !important;
            border-radius: 7px;
            padding: auto;
            float: right;
        }

    </style>
</head>
<body>
    <jsp:include page="/components/main/nav-header.jsp">
            <jsp:param name="hasSession" value="${hasSession}" />
    </jsp:include>

    <div class="container">
        <div class="Submit">
            <p class="text-decoration">Submit Request</p>
        </div>

        <form action="/action_page.php">
            <p style="font-size: large; font: white;">What can we help you?</p><br>
            <label for="Helps"></label>
            <select name="helpsOpsList" id="helpsOptions" class="custom-select">
                <option value="none">-</option>
                <option value="Help1">Help & Supports</option>
                <option value="Help2">Bug Report</option>
                <option value="Help3">Developer Supports</option>
            </select><br><br>
        

            <div class="hidden" id="helpSupport-form">
                <p>
                    <label for="email">Your email address</label><br>
                    <input type="email" id="email" name="Email" placeholder="Your Email@example.com"><br><br>

                    <label for="lname">Type of question</label><br>
                    <input type="text" id="question" name="Question" placeholder="Question"><br><br>

                    <label for="lname">Subject</label><br>
                    <input type="text" id="subject" name="Subject" placeholder="Subject"><br><br>

                    <label for="lname">Description</label><br>
                    <textarea id="description" name="Description" placeholder="Description"></textarea><br><br>

                    <input type="submit" id="submit" class="submit-bt" value="submit button">
                </p>
            </div>

            <div class="hidden" id="bugSupport-form">
                <p>
                    <label for="email">Your email address</label><br>
                    <input type="email" id="email" name="Email" placeholder="Your Email@example.com"><br><br>

                    <label for="lname">Description</label><br>
                    <textarea id="description" name="Description" placeholder="Description"></textarea><br><br>

                    <input type="submit" id="submit" class="submit-bt" value="submit button">
                </p>
            </div>

            <div class="hidden" id="devSupport-form">
                <p>
                    <label for="email">Your email address</label><br>
                    <input type="email" id="email" name="Email" placeholder="Your Email@example.com"><br><br>

                    <label for="lname">What can we help you?</label><br>
                    <input type="text" id="comment" name="Comment" placeholder="Comment"><br><br>

                    <label for="lname">Description</label><br>
                    <textarea id="description" name="Description" placeholder="Description"></textarea><br><br>

                    <input type="submit" id="submit" class="submit-bt" value="submit button">
                </p>
            </div>

        </form>
        
    </div>

</body>
</html>
