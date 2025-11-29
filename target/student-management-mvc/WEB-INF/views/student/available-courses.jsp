<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Available Courses" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available" class="active">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Available Courses</h2>
    </div>
    
    <c:if test="${param.error == 'already_enrolled'}">
        <div class="alert alert-error">You are already enrolled in this course!</div>
    </c:if>
    <c:if test="${param.error == 'course_full'}">
        <div class="alert alert-error">This course is already full!</div>
    </c:if>
    <c:if test="${param.error == 'enrollment_failed'}">
        <div class="alert alert-error">Failed to enroll in the course. Please try again.</div>
    </c:if>
    <c:if test="${param.error == 'schedule_conflict'}">
        <div class="alert alert-error">⚠️ Schedule Conflict! This course overlaps with another course you are already enrolled in. Please check your timetable.</div>
    </c:if>
    
    <div class="card">\n        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Credits</th>
                    <th>Teacher</th>
                    <th>Schedule</th>
                    <th>Room</th>
                    <th>Available Seats</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.courseCode}</td>
                        <td>${course.courseName}</td>
                        <td>${course.credits}</td>
                        <td>${course.teacherName != null ? course.teacherName : 'TBA'}</td>
                        <td style="font-size: 12px;">
                            <c:if test="${not empty course.scheduleDays}">
                                <div>${course.scheduleDays}</div>
                            </c:if>
                            <c:if test="${not empty course.scheduleTime}">
                                <div style="color: #666;">${course.scheduleTime}</div>
                            </c:if>
                            <c:if test="${empty course.scheduleDays && empty course.scheduleTime}">
                                <span style="color: #999;">TBA</span>
                            </c:if>
                        </td>
                        <td>${not empty course.roomNumber ? course.roomNumber : 'TBA'}</td>
                        <td>${course.maxStudents - course.enrolledCount} / ${course.maxStudents}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/student/courses/details?courseId=${course.courseId}" 
                               class="btn btn-info btn-sm">View Details</a>
                            <c:choose>
                                <c:when test="${course.description == 'ENROLLED'}">
                                    <span class="badge badge-active">Enrolled</span>
                                </c:when>
                                <c:when test="${course.enrolledCount >= course.maxStudents}">
                                    <span class="badge badge-dropped">Full</span>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/student/courses/enroll" method="post" style="display:inline;"
                                          onsubmit="return confirm('Are you sure you want to enroll in this course?');">
                                        <input type="hidden" name="courseId" value="${course.courseId}">
                                        <button type="submit" class="btn btn-success btn-sm">Enroll</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty courses}">
                    <tr>
                        <td colspan="8" style="text-align: center;">No courses available.</td>
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
        margin-right: 5px;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
