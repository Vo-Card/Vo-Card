<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register</title>
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

            form {
                width: 250px;
            }

            input {
                width: 100%;
            }

            @media (min-width: 768px) {
                form {
                    width: 435px;
                }

                input {
                    padding: 2px;
                }
            }

            input[type="submit"] {
                color: black;
            }

            p {
                margin-bottom: 0.5rem;
                color: white;
            }

            .register {
                background-color: #0D0D0D;
                border-radius: 20px;
                z-index: 1;
            }

            .light {
                position: absolute;
                background-image: radial-gradient(farthest-corner at left -30px,
                        rgba(93, 93, 93, 0.126) 5%,
                        rgba(192, 192, 192, 0.119) 15%,
                        rgba(0, 0, 0, 0.408) 100%);
                width: 100%;
                height: 100%;
            }
        </style>
    </head>

    <body>

        <header class="w-full d-flex align-items-center gap-2 shrink-0 top-0 sticky">
            <div class="d-flex align-items-center gap-2 px-4">
                <div class="d-flex align-items-center py-1">{current_page}</div>
            </div>
        </header>

        <!-- register zone -->
        <div class="light"></div>
        <div class=" d-flex justify-content-center h-100 ">

            <div class="mx-auto my-auto p-5 register">
                <h2 style="color: white;">Register</h2>

                <form action="register" method="post">
                    <p>Display Name:</p><input name="display_name" type="text" required /><br />
                    <p class="py-1">Username:</p><input name="username" type="text" required /><br />
                    <p class="py-1">Password:</p><input name="password" type="password" required /><br />
                    <p class="py-1">Confirm Password:</p><input name="confirmPassword" type="password" required /><br />
                    <div class="mt-3">
                        <input type="submit" value="Register" />
                    </div>
                </form>
            </div>
        </div>
        <c:if test="${not empty error}">
            <p style="color:red">${error}</p>
        </c:if>
    </body>

    </html>