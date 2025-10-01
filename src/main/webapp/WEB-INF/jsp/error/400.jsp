<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <html>

        <head>
            <title>400 Bad Request</title>

            <link rel="stylesheet" type="text/css" href="/css/style.css">
            <link rel="stylesheet" type="text/css" href="/css/main.css">

            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
                integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
                crossorigin="anonymous">

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-7qAoOXltbVP82dhxHAUje59V5r2YsVfBafyUDxEdApLPmcdhBPg1DKg1ERo0BZlK"
                crossorigin="anonymous"></script>
            <script src="/js/screen_correction.js" defer></script>
            <script src="/js/mpa-reloader.js"></script>

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

                .box {
                    border: 2px dashed #eaeaea;
                    border-radius: 3px;
                    width: 120px;
                    height: 160px;
                    margin-left: 150px;
                    margin-right: 30px;
                }

                .text-block {
                    text-align: left;
                    align-items: flex-start;
                    width: 900px;
                    max-width: 100%;
                }

                .text-block h1 {
                    font-size: 36px;
                    margin: 0;
                }

                .text-block p {
                    font-size: 16px;
                    color: #aaa;
                    margin-top: 8px;
                    max-width: 100%;
                }

                .line {
                    border-top: 1px solid #eaeaea;
                    margin-top: 79px;
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
            <div style="padding: 20px;">
                <div style="display: flex;
                        justify-content: center;
                        align-items: center;
                        ">


                    <svg width="275" height="225" viewBox="0 0 364 307" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <rect x="164.401" y="227.948" width="12.2989" height="71.4085"
                            transform="rotate(102.083 164.401 227.948)" fill="#D9D9D9" />
                        <circle cx="189.406" cy="170.521" r="12.8227" transform="rotate(-19.535 189.406 170.521)"
                            fill="#D9D9D9" />
                        <circle cx="88.6692" cy="162.784" r="10.6793" transform="rotate(-19.535 88.6692 162.784)"
                            fill="#D9D9D9" />
                        <path
                            d="M188.534 102.148C223.604 102.148 252.034 130.578 252.034 165.648C252.034 200.718 223.604 229.148 188.534 229.148C153.464 229.148 125.034 200.718 125.034 165.648C125.034 130.578 153.464 102.148 188.534 102.148ZM188.034 112.148C158.763 112.148 135.034 135.877 135.034 165.148C135.034 194.419 158.763 218.148 188.034 218.148C217.305 218.148 241.034 194.419 241.034 165.148C241.034 135.877 217.305 112.148 188.034 112.148Z"
                            fill="#D9D9D9" />
                        <rect x="194.034" y="232.654" width="29" height="9" transform="rotate(-15 194.034 232.654)"
                            fill="#D9D9D9" />
                        <rect x="214.727" y="239.148" width="66.8539" height="9" transform="rotate(75 214.727 239.148)"
                            fill="#D9D9D9" />
                        <path
                            d="M83.2354 62.3441C136.537 43.4325 194.129 62.8537 225.901 106.266C216.914 100.474 206.515 96.6835 195.335 95.5248C166.485 71.604 126.137 63.0382 88.252 76.4799C30.9979 96.794 1.05157 159.676 21.3652 216.93C41.6794 274.184 104.561 304.131 161.815 283.817C179.62 277.499 194.783 267.064 206.616 253.963L211.131 270.811C198.81 282.524 183.888 291.901 166.832 297.953C101.77 321.037 30.314 287.008 7.22949 221.947C-15.8546 156.885 18.1741 85.4286 83.2354 62.3441ZM248.357 200.68C244.827 221.765 235.93 241.634 222.616 258.292L217.602 239.58C218.194 238.657 218.773 237.725 219.337 236.785L226 234.999L224.107 227.939C224.73 226.649 225.325 225.346 225.896 224.033C235.082 218.114 242.793 210.107 248.357 200.68Z"
                            fill="#D9D9D9" />
                        <circle cx="271.534" cy="140.648" r="8.5" fill="#D9D9D9" />
                        <path
                            d="M282.895 102.091C286.555 102.328 300.52 107.586 308.269 106.496C317.238 105.235 325.633 101.341 332.39 95.309C339.147 89.2766 343.963 81.376 346.23 72.6062C348.497 63.8364 348.112 54.5914 345.125 46.0402C342.138 37.4889 336.682 30.0156 329.447 24.5652C322.212 19.1149 313.524 15.9322 304.48 15.4198C295.437 14.9074 286.444 17.0881 278.64 21.6864C270.836 26.2846 264.571 33.0938 260.637 41.2528"
                            stroke="#D9D9D9" stroke-width="15" stroke-linecap="round" stroke-linejoin="round" />
                        <path d="M282.895 102.091C282.318 102.054 276.855 121.588 276.855 121.845" stroke="#D9D9D9"
                            stroke-width="15" stroke-linecap="round" />
                    </svg>

                    <div class="text-block">
                        <h1 class="m-1">400 Bad Request</h1>
                        <p>What are you doing here?</p>
                        <div class="line w-100"></div>
                    </div>
                </div>
            </div>
        </body>

        </html>