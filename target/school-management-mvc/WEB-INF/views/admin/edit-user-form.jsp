<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Edit User" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users" class="active">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/semesters/list">Manage Semesters</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/admin/view">Student GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Edit User</h2>
    </div>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <div class="card">
        <form action="${pageContext.request.contextPath}/admin/users/update" method="post">
            <input type="hidden" name="userId" value="${user.userId}">
            
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" value="<c:out value='${user.username}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" value="<c:out value='${user.password}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input type="text" id="fullName" name="fullName" value="<c:out value='${user.fullName}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="text" id="phone" name="phone" value="<c:out value='${user.phone}'/>">
            </div>
            
            <div class="form-group">
                <label for="userType">User Type *</label>
                <select id="userType" name="userType" required>
                    <option value="ADMIN" <c:if test="${user.userType == 'ADMIN'}">selected</c:if>>Admin</option>
                    <option value="TEACHER" <c:if test="${user.userType == 'TEACHER'}">selected</c:if>>Teacher</option>
                    <option value="STUDENT" <c:if test="${user.userType == 'STUDENT'}">selected</c:if>>Student</option>
                </select>
            </div>
            
            <div style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary">Update User</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-danger">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
