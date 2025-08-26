<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>404 Not Found</title>

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
            }

            body {
                margin: 0;
                font-family: Arial, sans-serif;
                background: #1a1a1a;
                color: #f0f0f0;
            }
            .notfound-container {
                display: flex;
                align-items: center;
                padding: 40px;
            }
            .box {
                border: 2px dashed #eaeaea;
                border-radius: 5px;
                width: 120px;
                height: 160px;
                margin-right: 20px;
            }

            .text-block {
                text-align: left;
                align-items: flex-start;
            }

            .text-block h1 {
                font-size: 36px;
                margin: 0;
            }
            .text-block p {
                font-size: 16px;
                color: #aaa;
                margin-top: 8px;
            }
            .line {
                border-top: 1px solid #eaeaea;
                margin: 0 40px;
            }

            .light {
                position: absolute;
                background-blend-mode: normal, overlay, normal, normal;
                background-image: radial-gradient(farthest-corner at left -30px,
                        rgba(93, 93, 93, 0.126) 20%,
                        rgba(165, 173, 196, 0.119) 30%,
                        rgba(167, 173, 190, 0.119) 35%,
                        rgba(0, 0, 0, 0.408) 65%,
                        rgba(0, 0, 0, 0.4) 65%);
                width: 100%;
                height: 100%;
            }
        </style>
    </head>
      <body>
          <div class="light"></div>

          <div class="d-flex flex-column align-items-start pt-4">
              <div class="d-flex align-items-center notfound-container">
                  <div class="box"></div>
                  <div class="text-block">
                      <h1>404 Not Found :C</h1>
                      <p>The page you are looking for doesn’t exist.</p>
                  </div>
              </div>
              <div class="line w-100"></div>
          </div>
      </body>

    </html>