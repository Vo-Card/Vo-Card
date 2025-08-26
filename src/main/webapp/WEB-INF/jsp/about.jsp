<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>About</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-7qAoOXltbVP82dhxHAUje59V5r2YsVfBafyUDxEdApLPmcdhBPg1DKg1ERo0BZlK"
            crossorigin="anonymous"></script>
    <script src="/js/screen_correction.js" defer></script>

    <style>
        /* ===== Root Variables ===== */
        :root {
            --theme-color: #1b1d24;
            --primary-color: rgba(23, 23, 23, 0.2);
            --alert: #ff8989;
            --sidebar-bg: #1b1d24;
            --text-color: #ffffff;
        }

        /* ===== Global ===== */
        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            font-family: Arial, sans-serif;
            background-color: var(--content-bg);
            color: var(--text-color);
        }

        /* ===== Background / Light ===== */
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

        /* ===== Main Container / Content Box ===== */
        .main-container {
            margin-left: 220px;
            margin-top: 60px;
            padding: 40px;
            position: relative;
            z-index: 3;
        }
        .content-box {
            background: transparent;
            text-align: center;
            padding: 40px;
            border-radius: 10px;
        }

        /* ===== Typography ===== */
        h1 { margin-bottom: 20px; }
        p { margin-bottom: 1rem; line-height: 1.5; }

        /* ===== Topbar ===== */
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

        /* Username + small white box */
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

        /* ===== Sidebar ===== */
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
        .sidebar ul { list-style: none; padding: 0; margin: 0; }
        .sidebar ul li {
            padding: 12px 20px;
            cursor: pointer;
            margin-bottom: 10px;
        }
        .sidebar ul li:hover { background-color: #2c2c2c; }

        /* ===== Vo-Card ===== */
        .sidebar-header {
            margin: 15px;
            font-size: 18px;
            font-weight: bold;
            text-align: center;
            cursor: pointer;
            display: flex;
            align-items: center;
            padding-left: 15px; /* เพิ่มเพื่อเลื่อนข้อความจากขอบ */
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
        .sidebar-header:hover { color: #aaa; }

        /* Menu icons */
        .menu-icon {
            display: inline-block;
            width: 20px;
            height: 20px;
            background-color: white;
            border-radius: 2px;
            margin-right: 10px;
        }

        /* ===== Hamburger ===== */
        .hamburger {
            display: inline-block;
            margin-right: 10px;
        }
        .hamburger div {
            height: 2px; /* เล็กลง */
            background-color: white;
            margin: 3px 0;
            border-radius: 2px;
        }
        .hamburger div:nth-child(1) { width: 14px; }
        .hamburger div:nth-child(2) { width: 10px; }
        .hamburger div:nth-child(3) { width: 12px; }

        /* ===== Review Box ===== */
        .review-title { font-size: 18px; padding: 0 20px; }
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
        .level-list { display: flex; flex-direction: column; gap: 10px; }
        .level-item {
            padding-left: 10px;
            color: white;
            font-weight: 500;
        }

        /* Scrollbar */
        .review-box::-webkit-scrollbar { width: 8px; }
        .review-box::-webkit-scrollbar-thumb {
            background-color: rgba(0,0,0,0.4);
            border-radius: 4px;
        }
        .review-box::-webkit-scrollbar-track { background: transparent; }

        /* ===== Media Queries ===== */
        @media (max-width: 768px) {
            .sidebar { width: 60px; }
            .main-container { margin-left: 60px; padding: 20px; }
            .content-box { padding: 20px; }
            .topbar { left: 60px; }
        }

    </style>
</head>
<body>

<!-- 🔶 Sidebar -->
<div class="sidebar">
    <div class="sidebar-header">Vo-Card</div>
    <ul style="padding-top: 20px;">
        <li><span class="menu-icon"></span> Home</li>
        <li><span class="menu-icon"></span> Stat</li>
        <li><span class="menu-icon"></span> Learn</li>
        <li><span class="menu-icon"></span> Your deck</li>

        <h2 class="review-title">Last reviewed</h2>
        <div class="review-box ">
            <div class="level-list">
                <div class="level-item">A1</div>
                <div class="level-item">A2</div>
                <div class="level-item">B1</div>
                <div class="level-item">B2</div>
                <div class="level-item">C1</div>
                <div class="level-item">C2</div>
                <div class="level-item">D1</div>
                <div class="level-item">D2</div>
            </div>
        </div>

        <li><span class="menu-icon"></span> Setting</li>
        <li><span class="menu-icon"></span> Contact</li>
        <li><span class="menu-icon"></span> About me</li>
        <li><span class="menu-icon"></span> Log out</li>
    </ul>
</div>

<!-- 🔷 Topbar -->
<div class="topbar">
    <div>
        <span class="hamburger">
            <div></div>
            <div></div>
            <div></div>
        </span>
        About me
    </div>
    <div class="username-box">(Username)</div>
</div>

<div class="light"></div>
<div class="main-container">
    <div class="content-box">
        <div class="about-header">
            <h1>About Me</h1>
        </div>

        <p>
            This project is a flashcard web application inspired by Anki, developed as part of my Year 2 course work
            in Software Engineering. The goal is to help users study and memorize using a spaced repetition system (SRS).
            Users can create their own decks, publish for others to import.
        </p>
        <p>
            We created this project to demonstrate our understanding of object-oriented programming, database integration,
            and web development frameworks.
        </p>
    </div>
</div>
</body>
</html>
