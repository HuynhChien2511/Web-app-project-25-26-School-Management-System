<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Enter Grades" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades" class="active">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>Enter Grades - ${course.courseName}</h2>
        <div>
            <a href="${pageContext.request.contextPath}/grades/calculate-inclass?courseId=${course.courseId}" 
               class="btn btn-info" onclick="return confirm('Calculate in-class scores from attendance? This will overwrite existing in-class scores.')">
                🔄 Calculate In-Class from Attendance
            </a>
            <a href="${pageContext.request.contextPath}/grades/" class="btn btn-secondary">← Back</a>
        </div>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">
            ${sessionScope.message}
            <c:remove var="message" scope="session"/>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">
            ${sessionScope.error}
            <c:remove var="error" scope="session"/>
        </div>
    </c:if>

    <c:if test="${component != null}">
        <div class="alert alert-info">
            <strong>Grade Components:</strong> 
            In-Class: ${component.inclassPercentage}% | 
            Midterm: ${component.midtermPercentage}% | 
            Final: ${component.finalPercentage}%
        </div>
    </c:if>
    <c:if test="${component == null}">
        <div class="alert alert-warning">
            ⚠️ Grade components not configured for this course. Contact administrator.
        </div>
    </c:if>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/grades/save">
            <input type="hidden" name="courseId" value="${course.courseId}">

            <table class="table">
                <thead>
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>In-Class (0-100)</th>
                        <th>Midterm (0-100)</th>
                        <th>Final (0-100)</th>
                        <th>Total Score</th>
                        <th>Letter Grade</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="enrollment" items="${enrollments}">
                        <c:set var="grade" value="${gradeMap[enrollment.enrollmentId]}"/>
                        <tr>
                            <td>${enrollment.studentId}</td>
                            <td><strong>${enrollment.studentName}</strong></td>
                            <td>
                                <input type="number" step="0.01" min="0" max="100" 
                                       name="inclass_${enrollment.enrollmentId}" 
                                       class="form-control grade-input"
                                       value="${grade != null && grade.inclassScore != null ? grade.inclassScore : ''}"
                                       placeholder="0.00">
                            </td>
                            <td>
                                <input type="number" step="0.01" min="0" max="100" 
                                       name="midterm_${enrollment.enrollmentId}" 
                                       class="form-control grade-input"
                                       value="${grade != null && grade.midtermScore != null ? grade.midtermScore : ''}"
                                       placeholder="0.00">
                            </td>
                            <td>
                                <input type="number" step="0.01" min="0" max="100" 
                                       name="final_${enrollment.enrollmentId}" 
                                       class="form-control grade-input"
                                       value="${grade != null && grade.finalScore != null ? grade.finalScore : ''}"
                                       placeholder="0.00">
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${grade != null && grade.totalScore != null}">
                                        <strong>${grade.totalScore}</strong>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${grade != null && grade.letterGrade != null}">
                                        <span class="grade-badge grade-${grade.letterGrade}">${grade.letterGrade}</span>
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty enrollments}">
                        <tr>
                            <td colspan="7" class="text-center">No students enrolled in this course.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${not empty enrollments}">
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">💾 Save Grades</button>
                    <button type="button" class="btn btn-success" onclick="finalizeGrades()">
                        ✓ Finalize Grades
                    </button>
                    <p class="text-muted">
                        💡 Tip: Save individual scores first, then click "Finalize Grades" to calculate final grades and update GPAs.
                    </p>
                </div>
            </c:if>
        </form>
    </div>
</div>

<form id="finalizeForm" method="post" action="${pageContext.request.contextPath}/grades/finalize" style="display: none;">
    <input type="hidden" name="courseId" value="${course.courseId}">
</form>

<script>
function finalizeGrades() {
    if (confirm('Finalize all grades for this course? This will calculate final grades and update student GPAs.')) {
        document.getElementById('finalizeForm').submit();
    }
}
</script>

<style>
.grade-input {
    width: 100px;
    padding: 5px;
    text-align: right;
}

.grade-badge {
    display: inline-block;
    padding: 5px 15px;
    border-radius: 5px;
    font-weight: bold;
    font-size: 14px;
}

.grade-A, .grade-A- { background: #28a745; color: white; }
.grade-B+, .grade-B, .grade-B- { background: #17a2b8; color: white; }
.grade-C+, .grade-C, .grade-C- { background: #ffc107; color: #333; }
.grade-D+, .grade-D { background: #fd7e14; color: white; }
.grade-F { background: #dc3545; color: white; }

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

.content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.content-header div {
    display: flex;
    gap: 10px;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
