<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Select Course for Attendance" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/semesters/list">Manage Semesters</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance" class="active">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/admin/view">Student GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📋 Attendance Management (All Courses)</h2>
    </div>

    <div class="card">
        <h3>Select a Course</h3>
        <p>Choose a course to view or record attendance:</p>
        
        <div class="course-grid">
            <c:forEach var="course" items="${courses}">
                <div class="course-card">
                    <h4>${course.courseCode}</h4>
                    <p>${course.courseName}</p>
                    <div class="course-info">
                        <span>👨‍🏫 ${course.teacherName}</span>
                        <span>📍 ${course.roomNumber}</span>
                        <span>👥 ${course.maxStudents} students</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/attendance/take?courseId=${course.courseId}" 
                       class="btn btn-primary btn-block">Take Attendance</a>
                </div>
            </c:forEach>
            <c:if test="${empty courses}">
                <p class="text-center">No courses found.</p>
            </c:if>
        </div>
    </div>
</div>

<style>
.course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.course-card {
    background: white;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    padding: 20px;
    transition: box-shadow 0.3s;
}

.course-card:hover {
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.course-card h4 {
    color: #667eea;
    margin-bottom: 10px;
}

.course-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin: 15px 0;
    font-size: 14px;
    color: #6c757d;
}

.btn-block {
    width: 100%;
    margin-top: 10px;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
