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

            div input {
                background-color: rgb(62, 62, 62);
                border-style: none;
                border-radius: 5px;
                padding: 5px;
                cursor: pointer;
                transition: ease-in 0.35s;
            }

            .register div input:hover:not(input[type="submit"]) {
                background-color: rgb(116, 116, 116);
            }


            input::placeholder {
                color: white;
                opacity: 50%;
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
                color: white;
                background-color: #3b3b79;
            }

            input[type="submit"]:hover {
                color: white;
                background-color: #5050a1;
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

        <!-- register zone -->
        <div class="light"></div>
        <div class=" d-flex justify-content-center h-100 ">

            <div class="mx-auto my-auto p-5 register">
                <h2 style="color: white;">Register</h2>

                <form action="register" method="post">
                    <div>
                        <p>Display Name:</p>
                        <input name="display_name" class="transpa" type="text" placeholder="Display name"
                            required /><br />
                    </div>
                    <div>
                        <p class="py-1">Username:</p>
                        <input name="username" class="transpa" type="text" placeholder="Username" required /><br />
                    </div>
                    <div>
                        <p class="py-1">Password:</p>
                        <input name="password" class="transpa" type="password" placeholder="Password" required /><br />
                    </div>
                    <div>
                        <p class="py-1">Confirm Password:</p>
                        <input name="confirmPassword" class="transpa" type="password" placeholder="Confirm Password"
                            required /><br />
                    </div>
                    <div class="mt-3 transpa">
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