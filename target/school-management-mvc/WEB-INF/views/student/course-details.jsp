<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Course Details" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Course Details</h2>
    </div>
    
    <div class="card">
        <h3>${course.courseCode} - ${course.courseName}</h3>
        <hr>
        
        <div style="margin: 20px 0;">
            <p><strong>Description:</strong></p>
            <p>${course.description != null ? course.description : 'No description available.'}</p>
        </div>
        
        <div style="margin: 20px 0;">
            <p><strong>Credits:</strong> ${course.credits}</p>
            <p><strong>Teacher:</strong> ${course.teacherName != null ? course.teacherName : 'To be announced'}</p>
            <p><strong>Max Students:</strong> ${course.maxStudents}</p>
            <p><strong>Currently Enrolled:</strong> ${course.enrolledCount}</p>
            <p><strong>Available Seats:</strong> ${course.maxStudents - course.enrolledCount}</p>
        </div>
        
        <c:if test="${enrollment != null}">
            <div style="margin: 20px 0;">
                <h4>Your Enrollment Information</h4>
                <p><strong>Enrollment Date:</strong> ${enrollment.enrollmentDate}</p>
                <p><strong>Grade:</strong> ${enrollment.grade != null ? enrollment.grade : 'Not graded yet'}</p>
                <p><strong>Status:</strong> 
                    <span class="badge badge-${enrollment.status == 'ACTIVE' ? 'active' : 
                                               enrollment.status == 'COMPLETED' ? 'completed' : 'dropped'}">
                        ${enrollment.status}
                    </span>
                </p>
            </div>
            
            <c:if test="${enrollment.status == 'ACTIVE'}">
                <form action="${pageContext.request.contextPath}/student/courses/drop" method="post"
                      onsubmit="return confirm('Are you sure you want to drop this course?');">
                    <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                    <button type="submit" class="btn btn-danger">Drop This Course</button>
                </form>
            </c:if>
        </c:if>
        
        <c:if test="${enrollment == null}">
            <c:choose>
                <c:when test="${course.enrolledCount >= course.maxStudents}">
                    <p style="color: red;"><strong>This course is currently full.</strong></p>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/student/courses/enroll" method="post"
                          onsubmit="return confirm('Are you sure you want to enroll in this course?');">
                        <input type="hidden" name="courseId" value="${course.courseId}">
                        <button type="submit" class="btn btn-success">Enroll in This Course</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </c:if>
        
        <div style="margin-top: 20px;">
            <c:if test="${enrollment != null && enrollment.status == 'ACTIVE'}">
                <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}" class="btn btn-info">💬 Course Forum</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/student/courses/available" class="btn btn-primary">Back to Available Courses</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
