<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Teacher Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard" class="active">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Teacher Dashboard</h2>
    </div>
    
    <div class="stats-grid">
        <div class="stat-card">
            <h3>${totalCourses}</h3>
            <p>My Courses</p>
        </div>
        <div class="stat-card">
            <h3>${totalStudents}</h3>
            <p>Total Students</p>
        </div>
    </div>
    
    <div class="card">
        <h3>My Courses</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Credits</th>
                    <th>Enrolled Students</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="course" items="${myCourses}">
                    <tr>
                        <td>${course.courseCode}</td>
                        <td>${course.courseName}</td>
                        <td>${course.credits}</td>
                        <td>${course.enrolledCount} / ${course.maxStudents}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/teacher/courses/students?courseId=${course.courseId}" 
                               class="btn btn-info btn-sm">View Students</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty myCourses}">
                    <tr>
                        <td colspan="5" style="text-align: center;">No courses assigned yet.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        
        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/teacher/dashboard?page=${currentPage - 1}" class="page-link">« Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage eq i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/teacher/dashboard?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/teacher/dashboard?page=${currentPage + 1}" class="page-link">Next »</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

<style>
    .btn-sm {
        padding: 5px 10px;
        font-size: 12px;
    }
    
    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        margin: 20px 0;
        gap: 5px;
    }
    
    .page-link {
        padding: 8px 12px;
        text-decoration: none;
        color: #667eea;
        border: 1px solid #ddd;
        border-radius: 4px;
        transition: all 0.3s;
    }
    
    .page-link:hover {
        background-color: #667eea;
        color: white;
    }
    
    .page-link.active {
        background-color: #667eea;
        color: white;
        border-color: #667eea;
        pointer-events: none;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
