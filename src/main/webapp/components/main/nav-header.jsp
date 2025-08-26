<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav>
    <ul class="left">
        <div class="icon"></div>
        <li><a href="/home"
                style="font-size: 25px; border-right: solid white 2px; padding-right: 20px;">Vo-Card</a>
        </li>
        <li><a href="/explore">Explore</a></li>
        <li><a href="/products">Products</a></li>
        <li><a href="/about">About Us</a></li>
        <li><a href="/contact">Contact</a></li>
        <li><a href="/support">Support</a></li>
    </ul>
    <ul class="right">
        <c:set var="username" value="${param.username}" />
        <c:choose>
            <c:when test="${not empty username}">
                <li><a href="/workflow/home" class="opeb_btn">Open Vo-Card</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="/login" class="lgn_btn">Login</a></li>
                <li><a href="/register" class="reg_btn">Register</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>