<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="View Attendance" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/students">My Students</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance" class="active">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Attendance History - ${course.courseName}</h2>
        <a href="${pageContext.request.contextPath}/attendance/" class="btn btn-secondary">← Back</a>
    </div>

    <div class="card">
        <h3>Student Attendance Summary</h3>
        
        <table class="table">
            <thead>
                <tr>
                    <th>Student ID</th>
                    <th>Student Name</th>
                    <th>Attendance Rate</th>
                    <th>Present</th>
                    <th>Absent</th>
                    <th>Late</th>
                    <th>Excused</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="record" items="${attendanceRecords}">
                    <tr>
                        <td>${record.studentId}</td>
                        <td><strong>${record.studentName}</strong></td>
                        <td>
                            <div class="progress-bar">
                                <div class="progress" style="width: ${record.attendanceRate}%">
                                    ${record.attendanceRate}%
                                </div>
                            </div>
                        </td>
                        <td><span class="badge badge-success">${record.presentCount}</span></td>
                        <td><span class="badge badge-danger">${record.absentCount}</span></td>
                        <td><span class="badge badge-warning">${record.lateCount}</span></td>
                        <td><span class="badge badge-info">${record.excusedCount}</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${record.absentCount > 3}">
                                    <span class="status-badge status-dropped">⚠️ At Risk</span>
                                </c:when>
                                <c:when test="${record.attendanceRate >= 80}">
                                    <span class="status-badge status-good">✓ Good</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-warning">⚡ Warning</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty attendanceRecords}">
                    <tr>
                        <td colspan="8" class="text-center">No attendance records found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.progress-bar {
    width: 100%;
    height: 25px;
    background: #e9ecef;
    border-radius: 5px;
    overflow: hidden;
}

.progress {
    height: 100%;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;
    transition: width 0.3s;
}

.badge {
    display: inline-block;
    padding: 5px 10px;
    border-radius: 5px;
    font-size: 12px;
    font-weight: bold;
}

.badge-success { background: #28a745; color: white; }
.badge-danger { background: #dc3545; color: white; }
.badge-warning { background: #ffc107; color: #333; }
.badge-info { background: #17a2b8; color: white; }

.status-badge {
    display: inline-block;
    padding: 5px 15px;
    border-radius: 15px;
    font-size: 12px;
    font-weight: bold;
}

.status-good { background: #28a745; color: white; }
.status-warning { background: #ffc107; color: #333; }
.status-dropped { background: #dc3545; color: white; }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
