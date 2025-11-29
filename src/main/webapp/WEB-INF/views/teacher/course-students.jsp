<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Course Students" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses" class="active">My Courses</a></li>
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
        <h3>Enrolled Students (${enrollments.size()}/${course.maxStudents})</h3>
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
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
