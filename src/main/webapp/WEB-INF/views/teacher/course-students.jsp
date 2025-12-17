<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Course Students" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>${course.courseCode} - ${course.courseName}</h2>
    </div>
    
    <c:if test="${param.success == 'grade_updated'}">
        <div class="alert alert-success">Grade updated successfully!</div>
    </c:if>
    <c:if test="${param.success == 'student_dropped'}">
        <div class="alert alert-success">Student dropped successfully!</div>
    </c:if>
    
    <div class="card">
        <h3>Enrolled Students (${totalEnrollments}/${course.maxStudents})</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Student Name</th>
                    <th>Email</th>
                    <th>Enrollment Date</th>
                    <th>Grade</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="enrollment" items="${enrollments}">
                    <tr>
                        <td>${enrollment.studentName}</td>
                        <td>${enrollment.studentName}</td>
                        <td>${enrollment.enrollmentDate}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/teacher/grades/update" method="post" style="display:inline;">
                                <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                                <input type="hidden" name="courseId" value="${course.courseId}">
                                <input type="text" name="grade" value="${enrollment.grade}" 
                                       style="width:60px; padding:5px;" placeholder="A-F">
                                <button type="submit" class="btn btn-primary btn-sm">Save</button>
                            </form>
                        </td>
                        <td>
                            <span class="badge badge-${enrollment.status == 'ACTIVE' ? 'active' : 
                                                       enrollment.status == 'COMPLETED' ? 'completed' : 'dropped'}">
                                ${enrollment.status}
                            </span>
                        </td>
                        <td>
                            <c:if test="${enrollment.status == 'ACTIVE'}">
                                <form action="${pageContext.request.contextPath}/teacher/enrollments/drop" method="post" style="display:inline;"
                                      onsubmit="return confirm('Are you sure you want to drop this student?');">
                                    <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                                    <input type="hidden" name="courseId" value="${course.courseId}">
                                    <button type="submit" class="btn btn-danger btn-sm">Drop</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty enrollments}">
                    <tr>
                        <td colspan="6" style="text-align: center;">No students enrolled yet.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        
        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/teacher/courses/students?courseId=${course.courseId}&page=${currentPage - 1}" class="page-link">« Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage eq i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/teacher/courses/students?courseId=${course.courseId}&page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/teacher/courses/students?courseId=${course.courseId}&page=${currentPage + 1}" class="page-link">Next »</a>
                </c:if>
            </div>
        </c:if>
        
        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/teacher/courses" class="btn btn-primary">Back to My Courses</a>
        </div>
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
