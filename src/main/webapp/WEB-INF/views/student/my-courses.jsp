<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Courses" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses" class="active">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>My Courses</h2>
    </div>
    
    <c:if test="${param.success == 'enrolled'}">
        <div class="alert alert-success">Successfully enrolled in the course!</div>
    </c:if>
    <c:if test="${param.success == 'dropped'}">
        <div class="alert alert-success">Successfully dropped the course!</div>
    </c:if>
    
    <div class="card">
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Enrollment Date</th>
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
                        <td>${enrollment.enrollmentDate}</td>
                        <td>${enrollment.grade != null ? enrollment.grade : 'Not graded yet'}</td>
                        <td>
                            <span class="badge badge-${enrollment.status == 'ACTIVE' ? 'active' : 
                                                       enrollment.status == 'COMPLETED' ? 'completed' : 'dropped'}">
                                ${enrollment.status}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/student/courses/details?courseId=${enrollment.courseId}" 
                               class="btn btn-info btn-sm">Details</a>
                            <c:if test="${enrollment.status == 'ACTIVE'}">
                                <a href="${pageContext.request.contextPath}/forum/course?courseId=${enrollment.courseId}" 
                                   class="btn btn-success btn-sm">💬 Forum</a>
                                <form action="${pageContext.request.contextPath}/student/courses/drop" method="post" style="display:inline;"
                                      onsubmit="return confirm('Are you sure you want to drop this course?');">
                                    <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                                    <button type="submit" class="btn btn-danger btn-sm">Drop</button>
                                </form>
                            </c:if>
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
                    <a href="${pageContext.request.contextPath}/student/courses?page=${currentPage - 1}" class="page-link">&laquo; Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/student/courses?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/student/courses?page=${currentPage + 1}" class="page-link">Next &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

<style>
    .btn-sm {
        padding: 5px 10px;
        font-size: 12px;
        margin-right: 5px;
    }
    
    /* Pagination Styles */
    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 5px;
        margin-top: 20px;
        padding: 20px 0;
    }
    
    .page-link {
        padding: 8px 12px;
        border: 1px solid #ddd;
        background: white;
        color: #667eea;
        text-decoration: none;
        border-radius: 4px;
        transition: all 0.2s;
    }
    
    .page-link:hover {
        background: #667eea;
        color: white;
        border-color: #667eea;
    }
    
    .page-link.active {
        background: #667eea;
        color: white;
        border-color: #667eea;
        font-weight: bold;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
