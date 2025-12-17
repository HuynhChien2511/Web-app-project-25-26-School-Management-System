<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Take Attendance" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance" class="active">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Take Attendance - ${course.courseName}</h2>
        <a href="${pageContext.request.contextPath}/attendance/" class="btn btn-secondary">← Back to Courses</a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">
            ${sessionScope.message}
            <c:remove var="message" scope="session"/>
        </div>
    </c:if>

    <div class="card">
        <h3>Attendance for ${attendanceDate}</h3>
        <form method="post" action="${pageContext.request.contextPath}/attendance/save">
            <input type="hidden" name="courseId" value="${course.courseId}">
            <input type="hidden" name="date" value="${attendanceDate}">

            <div class="form-group">
                <label>Select Date:</label>
                <input type="date" name="newDate" value="${attendanceDate}" 
                       onchange="window.location.href='${pageContext.request.contextPath}/attendance/take?courseId=${course.courseId}&date=' + this.value">
            </div>

            <table class="table">
                <thead>
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Status</th>
                        <th>Notes</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="enrollment" items="${enrollments}">
                        <c:set var="attendance" value="${attendanceMap[enrollment.enrollmentId]}"/>
                        <tr>
                            <td>${enrollment.studentId}</td>
                            <td><strong>${enrollment.studentName}</strong></td>
                            <td>
                                <select name="status_${enrollment.enrollmentId}" class="form-control">
                                    <option value="PRESENT" ${attendance != null && attendance.status == 'PRESENT' ? 'selected' : ''}>
                                        ✓ Present
                                    </option>
                                    <option value="ABSENT" ${attendance != null && attendance.status == 'ABSENT' ? 'selected' : ''}>
                                        ✗ Absent
                                    </option>
                                    <option value="LATE" ${attendance != null && attendance.status == 'LATE' ? 'selected' : ''}>
                                        ⏰ Late
                                    </option>
                                    <option value="EXCUSED" ${attendance != null && attendance.status == 'EXCUSED' ? 'selected' : ''}>
                                        📝 Excused
                                    </option>
                                </select>
                            </td>
                            <td>
                                <input type="text" name="notes_${enrollment.enrollmentId}" 
                                       class="form-control" placeholder="Optional notes"
                                       value="${attendance != null ? attendance.notes : ''}">
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty enrollments}">
                        <tr>
                            <td colspan="4" class="text-center">No students enrolled in this course.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${not empty enrollments}">
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">💾 Save Attendance</button>
                    <p class="text-muted">⚠️ Note: Students with more than 3 absences will be automatically dropped.</p>
                </div>
            </c:if>
        </form>
    </div>
</div>

<style>
.form-actions {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #dee2e6;
}
.text-muted {
    color: #6c757d;
    margin-top: 10px;
    font-size: 14px;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
