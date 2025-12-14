<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Student Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard" class="active">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Student Dashboard</h2>
    </div>
    
    <div class="stats-grid">
        <div class="stat-card">
            <h3>${totalEnrollments}</h3>
            <p>Enrolled Courses</p>
        </div>
    </div>
    
    <div class="card">
        <h3>My Current Enrollments</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Credits</th>
                    <th>Grade</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="enrollment" items="${enrollments}">
                    <tr>
                        <td>${enrollment.courseCode}</td>
                        <td>${enrollment.courseName}</td>
                        <td>N/A</td>
                        <td>${enrollment.grade != null ? enrollment.grade : 'Not graded'}</td>
                        <td>
                            <span class="badge badge-${enrollment.status == 'ACTIVE' ? 'active' : 
                                                       enrollment.status == 'COMPLETED' ? 'completed' : 'dropped'}">
                                ${enrollment.status}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/student/courses/details?courseId=${enrollment.courseId}" 
                               class="btn btn-info btn-sm">View Details</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty enrollments}">
                    <tr>
                        <td colspan="6" style="text-align: center;">
                            You are not enrolled in any courses yet.
                            <a href="${pageContext.request.contextPath}/student/courses/available">Browse available courses</a>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        
        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/student/dashboard?page=${currentPage - 1}" class="page-link">« Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage eq i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/student/dashboard?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/student/dashboard?page=${currentPage + 1}" class="page-link">Next »</a>
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
