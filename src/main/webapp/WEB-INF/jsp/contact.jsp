<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Contact</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-7qAoOXltbVP82dhxHAUje59V5r2YsVfBafyUDxEdApLPmcdhBPg1DKg1ERo0BZlK"
            crossorigin="anonymous"></script>
    <script src="/js/screen_correction.js" defer></script>

    <style>
        :root {
            --theme-color: #1b1d24;
            --primary-color: rgba(23, 23, 23, 0.2);
            --alert: #ff8989;
            --sidebar-bg: #1b1d24;
            --text-color: #ffffff;
            --content-bg: #1b1d24; /* เพิ่มตัวแปรนี้ให้เหมือน About */
        }

        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            font-family: Arial, sans-serif;
            background-color: var(--content-bg);
            color: var(--text-color);
        }

        .light {
            position: absolute;
            width: 100%;
            height: 100%;
            background-image: radial-gradient(
                farthest-corner at left -30px,
                rgba(93,93,93,0.126) 20%,
                rgba(165,173,196,0.119) 30%,
                rgba(167,173,190,0.119) 35%,
                rgba(0,0,0,0.408) 65%,
                rgba(0,0,0,0.4) 65%
            );
        }

        .main-container {
            margin-left: 220px;
            margin-top: 60px;
            padding: 40px;
            position: relative;
            z-index: 3;
        }

        .form-box {
            background: transparent;
            text-align: left;
            padding: 40px;
            border-radius: 10px;
            max-width: 800px;
            margin: 0 auto;
        }

        h1 {
            margin-bottom: 30px;
            text-align: center;
        }

        label {
            display: block;
            margin-top: 20px;
            margin-bottom: 8px;
            font-weight: bold;
        }

        select, input, textarea {
            width: 100%;
            padding: 12px;
            background-color: #000000;
            color: white;
            border: none;
            border-radius: 6px;
            margin-bottom: 10px;
            font-size: 14px;
        }

        input::placeholder, textarea::placeholder {
            color: rgba(255, 255, 255, 0.6);
        }

        button {
            padding: 10px 24px;
            margin-top: 20px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }

        .submit-btn {
            background-color: #444;
            color: white;
            margin-right: 10px;
        }

        .cancel-btn {
            background-color: #ff6b6b;
            color: white;
        }

        /* Sidebar */
        .sidebar {
            width: 220px;
            background-color: var(--sidebar-bg);
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            z-index: 2;
            height: 100vh;
            overflow: auto;
        }

        .sidebar ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .sidebar ul li {
            padding: 12px 20px;
            cursor: pointer;
            margin-bottom: 10px;
        }

        .sidebar ul li:hover {
            background-color: #2c2c2c;
        }

        .sidebar-header {
            margin: 15px;
            font-size: 18px;
            font-weight: bold;
            text-align: center;
            cursor: pointer;
            display: flex;
            align-items: center;
            padding-left: 15px;
        }

        .sidebar-header::before {
            content: "";
            display: inline-block;
            width: 20px;
            height: 20px;
            margin-right: 6px;
            background-color: #fff;
            border-radius: 2px;
        }

        .menu-icon {
            display: inline-block;
            width: 20px;
            height: 20px;
            background-color: white;
            border-radius: 2px;
            margin-right: 10px;
        }

        /* Last reviewed section like About */
        .review-title {
            font-size: 18px;
            padding: 0 20px;
            color: var(--text-color);
            margin-top: 20px;
            margin-bottom: 5px;
        }
        .review-box {
            background-color: rgba(255,255,255,0.06);
            padding: 20px;
            border-radius: 10px;
            margin: 0 20px 30px 20px;
            max-height: 160px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }
        .level-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .level-item {
            padding-left: 10px;
            color: white;
            font-weight: 500;
        }
        .review-box::-webkit-scrollbar {
            width: 8px;
        }
        .review-box::-webkit-scrollbar-thumb {
            background-color: rgba(0,0,0,0.4);
            border-radius: 4px;
        }
        .review-box::-webkit-scrollbar-track {
            background: transparent;
        }

        /* Topbar */
        .topbar {
            height: 60px;
            background-color: var(--sidebar-bg);
            color: var(--text-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            position: fixed;
            top: 0;
            left: 220px;
            right: 0;
            z-index: 2;
            font-size: 20px;
            font-weight: bold;
        }

        .username-box {
            font-size: 14px;
            display: flex;
            align-items: center;
        }

        .username-box::before {
            content: "";
            display: inline-block;
            width: 20px;
            height: 20px;
            margin-right: 6px;
            background-color: #fff;
            border-radius: 2px;
        }

        .hamburger {
            display: inline-block;
            margin-right: 10px;
        }

        .hamburger div {
            height: 2px;
            background-color: white;
            margin: 3px 0;
            border-radius: 2px;
        }

        .hamburger div:nth-child(1) { width: 14px; }
        .hamburger div:nth-child(2) { width: 10px; }
        .hamburger div:nth-child(3) { width: 12px; }

        @media (max-width: 768px) {
            .sidebar { width: 60px; }
            .main-container { margin-left: 60px; padding: 20px; }
            .topbar { left: 60px; }
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <div class="sidebar-header">Vo-Card</div>
    <ul style="padding-top: 20px;">
        <li><span class="menu-icon"></span> Home</li>
        <li><span class="menu-icon"></span> Stat</li>
        <li><span class="menu-icon"></span> Learn</li>
        <li><span class="menu-icon"></span> Your deck</li>

        <h2 class="review-title">Last reviewed</h2>
        <div class="review-box">
            <div class="level-list">
                <div class="level-item">A1</div>
                <div class="level-item">A2</div>
                <div class="level-item">B1</div>
                <div class="level-item">B2</div>
            </div>
        </div>

        <li><span class="menu-icon"></span> Setting</li>
        <li><span class="menu-icon"></span> Contact</li>
        <li><span class="menu-icon"></span> About me</li>
        <li><span class="menu-icon"></span> Log out</li>
    </ul>
</div>

<!-- Topbar -->
<div class="topbar">
    <div>
        <span class="hamburger">
            <div></div>
            <div></div>
            <div></div>
        </span>
        Contact
    </div>
    <div class="username-box">(Username)</div>
</div>

<div class="light"></div>

<!-- Contact Form -->
<div class="main-container">
    <div class="form-box">
        <h1>Contact</h1>
        <form method="post" action="sendMessage">
            <label for="reason">Reason for contacting</label>
            <select id="reason" name="reason">
                <option>Hello</option>
                <option>Feedback</option>
                <option>Support</option>
                <option>Other</option>
            </select>

            <label for="subject">Subject</label>
            <input type="text" id="subject" name="subject" placeholder="Enter subject" required>

            <label for="description">Description</label>
            <textarea id="description" name="description" placeholder="Enter description" rows="5" required></textarea>

            <button type="submit" class="submit-btn">Submit</button>
            <button type="reset" class="cancel-btn">Cancel</button>
        </form>
    </div>
</div>

</body>
</html>
