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
            margin: 180px auto 30px;     /* ✅ ขยับขึ้นบนโดยกำหนด margin-top */
            width: 100%;
            max-width: 1200px;
            padding: 0 20px 80px;       /* ✅ เอา padding-top ออก */
            text-align: center;
        }


        .feedback-container h1 {
            font-size:85px;
            margin-bottom: 50px;
        }

        .feedback-container input[type="text"] {
            padding: 16px;
            width: 100%;
            max-width: 1300px;   /* ✅ เพิ่มความยาวช่อง Search */
            margin: 14px auto;
            border-radius: 8px;
            border: none;
            font-size: 30px;
            display: block;
            color: black;
        }

        .feedback-container select {
            padding: 18px;
            
            width: 100%;
            max-width: 300px;   /* ✅ ลดความยาวช่อง Heading Topic */
            margin: 14px auto;
            margin-bottom: 150px;
            border-radius: 8px;
            border: none;
            font-size: 30px;
            display: block;
            color: black;
        }

        .feedback-buttons {
            display: flex;
            flex-wrap: nowrap;        /* ✅ ไม่ให้ปุ่มล้นแล้วขึ้นบรรทัดใหม่ */
            justify-content: center;  /* ✅ จัดให้อยู่ตรงกลาง */
            gap: 30px;
            margin: 60px auto 50px;
            max-width: 900px;
        }

        .feedback-buttons button {
            display: flex;             /* ✅ ใช้ Flexbox */
            flex-direction: column;    /* ✅ เรียงจากบนลงล่าง */
            justify-content: space-between;  /* ✅ ดัน subtitle ไปล่างสุด */
            align-items: center;

            background-color: #e1c89d;
            color: black;
            padding: 60px 70px;
            border: none;
            border-radius: 12px;
            font-weight: bold;
            font-size: 22px;
            min-width: 500px;
            flex: 1 1 calc(33.333% - 30px);
            max-width: 400px;
            cursor: pointer;
            transition: transform 0.2s ease;

            
        }

        .feedback-buttons button:hover {
            transform: scale(1.05);
        }
        .feedback-buttons .btn-title {
            font-size: 44px;
            font-weight: 700;
            line-height: 1.4;
            color: black;
        }

        .feedback-buttons .btn-subtitle {
            font-size: 22px;
            font-weight: 400;
            color: black;
            margin-top: 17px;
        }


        .new-post-section {
            margin-top: 200px;
        }

        .new-post-section p {
            margin-bottom: 14px;
            font-size: 30px;
        }

        .new-post-section button {
            background-color: #e1c89d;
            color: black;
            padding: 26px 56px;
            border: none;
            border-radius: 10px;
            font-size: 22px;
            font-weight: bold;
            cursor: pointer;
        }

        @media (max-width: 900px) {
            .feedback-buttons {
                flex-direction: column;
                align-items: center;
            }

            .feedback-buttons button {
                width: 100%;
                max-width: 400px;
            }
        }

        .search-box {
            position: relative;
            width: 100%;
            max-width: 1300px;
            margin: 14px auto;
        }

        .search-box input[type="text"] {
            padding: 16px 16px 16px 50px; /* ✅ เผื่อที่ด้านซ้ายให้ไอคอน */
            width: 100%;
            border-radius: 8px;
            border: none;
            font-size: 30px;
            color: black;
            display: block;
        }

        .search-box .fa-search {
            position: absolute;
            top: 50%;
            left: 18px;
            transform: translateY(-50%);
            color: black;
            font-size: 22px;
            pointer-events: none;
        }

    </style>
</head>

<body>

    <!-- Navbar -->
    <jsp:include page="/components/main/nav-header.jsp">
        <jsp:param name="hasSession" value="${hasSession}" />
    </jsp:include>

    <!-- Feedback Content -->
    <div class="feedback-container">
        <h1>Feedback</h1>

        <div class="search-box">
            <i class="fa fa-search"></i>
            <input type="text" placeholder="Search" />
        </div>



        <select>
            <option>Heading Topic</option>
            <!-- More options can be added -->
        </select>

        <div class="feedback-buttons">
            
            <button>
                <div class="btn-title">Performance & Accessibility</div>
                <div class="btn-subtitle">{P&A Post}</div>
            </button>


            <button>
                <div class="btn-title">Features</div>
                <div class="btn-subtitle">{P&A Post}</div>
            </button>

            <button>
                <div class="btn-title">Overall Satisfaction</div>
                <div class="btn-subtitle">{P&A Post}</div>
            </button>

        </div>
        

        <div class="new-post-section">
            <p>Not find, what you were looking for?</p>
            <button>New Post</button>
        </div>
    </div>

</body>

</html>
