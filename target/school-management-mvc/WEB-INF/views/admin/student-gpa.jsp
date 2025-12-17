<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Student GPA Details" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/semesters/list">Manage Semesters</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/admin/view" class="active">Student GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📊 Student GPA Management</h2>
    </div>

    <div class="card">
        <h3>Search Student</h3>
        <form method="get" action="${pageContext.request.contextPath}/gpa/admin/view">
            <div class="form-row">
                <div class="form-group">
                    <label>Student ID:</label>
                    <input type="text" name="studentId" class="form-control" 
                           value="${param.studentId}" placeholder="Enter student ID" required>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">🔍 Search</button>
                </div>
            </div>
        </form>
    </div>

    <c:if test="${not empty student}">
        <div class="card">
            <h3>Student Information</h3>
            <div class="student-info">
                <p><strong>Student ID:</strong> ${student.userId}</p>
                <p><strong>Name:</strong> ${student.fullName}</p>
                <p><strong>Email:</strong> ${student.email}</p>
            </div>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <h4>Current Semester GPA</h4>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${not empty gpaRecords && gpaRecords[0].semesterGpa != null}">
                            <fmt:formatNumber value="${gpaRecords[0].semesterGpa}" pattern="0.00"/>
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="stat-card">
                <h4>Cumulative GPA</h4>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${not empty gpaRecords && gpaRecords[0].cumulativeGpa != null}">
                            <fmt:formatNumber value="${gpaRecords[0].cumulativeGpa}" pattern="0.00"/>
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="stat-card">
                <h4>Academic Standing</h4>
                <div class="stat-value standing-${not empty gpaRecords ? gpaRecords[0].academicStanding : 'GOOD'}">
                    ${not empty gpaRecords ? gpaRecords[0].academicStanding : 'GOOD'}
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3>Semester History</h3>
                <form method="post" action="${pageContext.request.contextPath}/gpa/recalculate" 
                      style="display: inline;">
                    <input type="hidden" name="studentId" value="${student.userId}">
                    <button type="submit" class="btn btn-info" 
                            onclick="return confirm('Recalculate GPA for this student?')">
                        🔄 Recalculate GPA
                    </button>
                </form>
            </div>
            
            <table class="table">
                <thead>
                    <tr>
                        <th>Semester</th>
                        <th>Credits Attempted</th>
                        <th>Credits Earned</th>
                        <th>Semester GPA</th>
                        <th>Cumulative GPA</th>
                        <th>Academic Standing</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="record" items="${gpaRecords}">
                        <tr>
                            <td><strong>${record.semesterName}</strong></td>
                            <td>${record.creditsAttempted}</td>
                            <td>${record.creditsEarned}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${record.semesterGpa != null}">
                                        <fmt:formatNumber value="${record.semesterGpa}" pattern="0.00"/>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${record.cumulativeGpa != null}">
                                        <fmt:formatNumber value="${record.cumulativeGpa}" pattern="0.00"/>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <span class="badge standing-${record.academicStanding}">
                                    ${record.academicStanding}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty gpaRecords}">
                        <tr>
                            <td colspan="6" class="text-center">No GPA records found for this student.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </c:if>
</div>

<style>
.form-row {
    display: flex;
    gap: 15px;
    align-items: flex-end;
}

.student-info {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin: 20px 0;
}

.stat-card {
    background: white;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    padding: 20px;
    text-align: center;
}

.stat-card h4 {
    color: #6c757d;
    font-size: 14px;
    margin-bottom: 10px;
}

.stat-value {
    font-size: 36px;
    font-weight: bold;
    color: #667eea;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.badge {
    display: inline-block;
    padding: 5px 15px;
    border-radius: 5px;
    font-weight: bold;
    font-size: 12px;
}

.standing-EXCELLENT { color: #28a745; }
.standing-GOOD { color: #17a2b8; }
.standing-PROBATION { color: #ffc107; }
.standing-WARNING { color: #fd7e14; }
.standing-SUSPENDED { color: #dc3545; }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
