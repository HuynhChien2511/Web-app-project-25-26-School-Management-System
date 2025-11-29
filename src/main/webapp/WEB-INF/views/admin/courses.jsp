<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Manage Courses" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses" class="active">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Manage Courses</h2>
        <a href="${pageContext.request.contextPath}/admin/courses/add" class="btn btn-primary">Add New Course</a>
    </div>
    
    <c:if test="${param.success == 'added'}">
        <div class="alert alert-success">Course added successfully!</div>
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <div class="alert alert-success">Course updated successfully!</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">Course deleted successfully!</div>
    </c:if>
    
    <div class="card">
        <table class="table">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Course Name</th>
                    <th>Credits</th>
                    <th>Teacher</th>
                    <th>Schedule</th>
                    <th>Room</th>
                    <th>Enrolled/Max</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.courseCode}</td>
                        <td>${course.courseName}</td>
                        <td>${course.credits}</td>
                        <td>${course.teacherName != null ? course.teacherName : 'Unassigned'}</td>
                        <td>
                            <c:if test="${not empty course.scheduleDays}">
                                <div style="font-size: 12px;">${course.scheduleDays}</div>
                            </c:if>
                            <c:if test="${not empty course.scheduleTime}">
                                <div style="font-size: 11px; color: #666;">${course.scheduleTime}</div>
                            </c:if>
                            <c:if test="${empty course.scheduleDays && empty course.scheduleTime}">
                                <span style="color: #999;">TBA</span>
                            </c:if>
                        </td>
                        <td>${not empty course.roomNumber ? course.roomNumber : 'TBA'}</td>
                        <td>${course.enrolledCount} / ${course.maxStudents}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/courses/edit?id=${course.courseId}" class="btn btn-warning btn-sm">Edit</a>
                            <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}" class="btn btn-success btn-sm">💬 Forum</a>
                            <form action="${pageContext.request.contextPath}/admin/courses/delete" method="post" style="display:inline;"
                                  onsubmit="return confirm('Are you sure you want to delete this course?');">
                                <input type="hidden" name="id" value="${course.courseId}">
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
