<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Courses" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses" class="active">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>My Courses</h2>
    </div>
    
    <div class="card">
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Description</th>
                    <th>Credits</th>
                    <th>Enrolled/Max</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.courseCode}</td>
                        <td>${course.courseName}</td>
                        <td>${course.description}</td>
                        <td>${course.credits}</td>
                        <td>${course.enrolledCount} / ${course.maxStudents}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/teacher/courses/students?courseId=${course.courseId}" 
                               class="btn btn-info btn-sm">Manage Students</a>
                            <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}" 
                               class="btn btn-success btn-sm">💬 Forum</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty courses}">
                    <tr>
                        <td colspan="6" style="text-align: center;">No courses assigned yet.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
    .btn-sm {
        padding: 5px 10px;
        font-size: 12px;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
