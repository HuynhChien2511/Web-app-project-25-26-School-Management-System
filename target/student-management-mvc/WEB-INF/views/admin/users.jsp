<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Manage Users" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users" class="active">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Manage Users</h2>
        <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">Add New User</a>
    </div>
    
    <c:if test="${param.success == 'added'}">
        <div class="alert alert-success">User added successfully!</div>
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <div class="alert alert-success">User updated successfully!</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">User deleted successfully!</div>
    </c:if>
    
    <div class="card">
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>User Type</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.username}</td>
                        <td>${user.fullName}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                        <td>${user.userType}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.userId}" class="btn btn-warning btn-sm">Edit</a>
                            <form action="${pageContext.request.contextPath}/admin/users/delete" method="post" style="display:inline;" 
                                  onsubmit="return confirm('Are you sure you want to delete this user?');">
                                <input type="hidden" name="id" value="${user.userId}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<style>
    .btn-sm {
        padding: 5px 10px;
        font-size: 12px;
        margin-right: 5px;
    }
    .content-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
