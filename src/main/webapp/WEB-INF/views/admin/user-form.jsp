<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="${empty user ? 'Add User' : 'Edit User'}" scope="request"/>
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
        <h2><c:choose><c:when test="${empty user}">Add New User</c:when><c:otherwise>Edit User</c:otherwise></c:choose></h2>
    </div>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <div class="card">
        <form action="${pageContext.request.contextPath}/admin/users/<c:choose><c:when test='${empty user}'>add</c:when><c:otherwise>update</c:otherwise></c:choose>" method="post">
            <c:if test="${not empty user}">
                <input type="hidden" name="userId" value="${user.userId}">
            </c:if>
            
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" value="<c:out value='${empty user ? "" : user.username}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" value="<c:out value='${empty user ? "" : user.password}'/>" <c:if test="${empty user}">placeholder="Enter password"</c:if> required>
            </div>
            
            <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input type="text" id="fullName" name="fullName" value="<c:out value='${empty user ? "" : user.fullName}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" value="<c:out value='${empty user ? "" : user.email}'/>" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="text" id="phone" name="phone" value="<c:out value='${empty user ? "" : user.phone}'/>">            </div>
            
            <div class="form-group">
                <label for="userType">User Type *</label>
                <select id="userType" name="userType" required>
                    <option value="" <c:if test="${empty user}">selected</c:if>>-- Select User Type --</option>
                    <option value="ADMIN" <c:if test="${not empty user && user.userType == 'ADMIN'}">selected</c:if>>Admin</option>
                    <option value="TEACHER" <c:if test="${not empty user && user.userType == 'TEACHER'}">selected</c:if>>Teacher</option>
                    <option value="STUDENT" <c:if test="${not empty user && user.userType == 'STUDENT'}">selected</c:if>>Student</option>
                </select>
            </div>
            
            <div style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary"><c:choose><c:when test="${empty user}">Add</c:when><c:otherwise>Update</c:otherwise></c:choose> User</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-danger">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
