<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <html>

    <head>
        <title>HelloWorld</title>
    </head>

    <body>
        <h1>Hello World! You ${username}</h1>
        <!-- if else -->
        <c:choose>
            <c:when test="${not empty username}">
                <p>Welcome back, ${username}!</p>
            </c:when>
            <c:otherwise>
                <p>Please log in or register.</p>
            </c:otherwise>
        </c:choose>
    </body>

    </html>