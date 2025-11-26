<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Admin Dashboard</h2>
    </div>
    
    <div class="stats-grid">
        <div class="stat-card">
            <h3>${totalUsers}</h3>
            <p>Total Users</p>
        </div>
        <div class="stat-card">
            <h3>${adminCount}</h3>
            <p>Administrators</p>
        </div>
        <div class="stat-card">
            <h3>${teacherCount}</h3>
            <p>Teachers</p>
        </div>
        <div class="stat-card">
            <h3>${studentCount}</h3>
            <p>Students</p>
        </div>
        <div class="stat-card">
            <h3>${totalCourses}</h3>
            <p>Total Courses</p>
        </div>
    </div>
    
    <div class="card">
        <h3>Quick Actions</h3>
        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">Add New User</a>
            <a href="${pageContext.request.contextPath}/admin/courses/add" class="btn btn-success">Add New Course</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-info">View All Users</a>
            <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-warning">View All Courses</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
