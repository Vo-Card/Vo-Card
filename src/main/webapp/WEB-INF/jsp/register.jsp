<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
</head>
<body>
    <h2>Register</h2>
    <form action="register" method="post">
        DisplayName: <input name="display_name" type="text" required/><br/>
        Username: <input name="username" type="text" required/><br/>
        Password: <input name="password" type="password" required/><br/>
        Confirm Password: <input name="confirmPassword" type="password" required/><br/>
        <input type="submit" value="Register"/>
    </form>
    <c:if test="${not empty error}">
        <p style="color:red">${error}</p>
    </c:if>
</body>
</html>