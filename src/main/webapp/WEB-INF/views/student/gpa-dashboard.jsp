<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="GPA Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard" class="active">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📊 My Academic Performance</h2>
    </div>

    <!-- GPA Summary Cards -->
    <c:if test="${latestGpa != null}">
        <div class="gpa-summary">
            <div class="gpa-card highlight">
                <div class="gpa-value">${latestGpa.cumulativeGpa}</div>
                <div class="gpa-label">Cumulative GPA</div>
                <div class="gpa-scale">4.0 Scale</div>
            </div>
            <div class="gpa-card">
                <div class="gpa-value">${latestGpa.semesterGpa}</div>
                <div class="gpa-label">Current Semester GPA</div>
            </div>
            <div class="gpa-card">
                <div class="gpa-value">${latestGpa.totalCredits}</div>
                <div class="gpa-label">Total Credits Earned</div>
            </div>
            <div class="gpa-card standing">
                <div class="gpa-value">${latestGpa.academicStanding}</div>
                <div class="gpa-label">Academic Standing</div>
            </div>
        </div>
    </c:if>

    <!-- Semester GPA Records -->
    <c:if test="${not empty gpaRecords}">
        <div class="card">
            <h3>📈 Semester GPA History</h3>
            <table class="table">
                <thead>
                    <tr>
                        <th>Semester</th>
                        <th>Semester GPA</th>
                        <th>Credits</th>
                        <th>Cumulative GPA</th>
                        <th>Total Credits</th>
                        <th>Calculated</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="record" items="${gpaRecords}">
                        <tr>
                            <td><strong>Semester ${record.semesterId}</strong></td>
                            <td>
                                <span class="gpa-badge gpa-${record.semesterGpa >= 3.5 ? 'high' : record.semesterGpa >= 3.0 ? 'good' : record.semesterGpa >= 2.0 ? 'ok' : 'low'}">
                                    ${record.semesterGpa}
                                </span>
                            </td>
                            <td>${record.semesterCredits}</td>
                            <td>
                                <span class="gpa-badge gpa-${record.cumulativeGpa >= 3.5 ? 'high' : record.cumulativeGpa >= 3.0 ? 'good' : record.cumulativeGpa >= 2.0 ? 'ok' : 'low'}">
                                    ${record.cumulativeGpa}
                                </span>
                            </td>
                            <td>${record.totalCredits}</td>
                            <td><fmt:formatDate value="${record.calculatedAtDate}" pattern="MMM dd, yyyy"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- Course Grades -->
    <div class="card">
        <h3>📚 Course Grades</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Course</th>
                    <th>Semester</th>
                    <th>In-Class</th>
                    <th>Midterm</th>
                    <th>Final</th>
                    <th>Total Score</th>
                    <th>Letter Grade</th>
                    <th>Grade Points</th>
                    <th>Credits</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="enrollment" items="${enrollments}">
                    <c:set var="grade" value="${gradeMap[enrollment.enrollmentId]}"/>
                    <c:set var="component" value="${componentMap[enrollment.enrollmentId]}"/>
                    <tr>
                        <td>
                            <strong>${enrollment.courseCode}</strong><br>
                            <small>${enrollment.courseName}</small>
                        </td>
                        <td>Semester ${enrollment.semesterId}</td>
                        <c:choose>
                            <c:when test="${grade != null}">
                                <td>
                                    ${grade.inclassScore != null ? grade.inclassScore : '-'}
                                    <c:if test="${component != null}">
                                        <br><small>(${component.inclassPercentage}%)</small>
                                    </c:if>
                                </td>
                                <td>
                                    ${grade.midtermScore != null ? grade.midtermScore : '-'}
                                    <c:if test="${component != null}">
                                        <br><small>(${component.midtermPercentage}%)</small>
                                    </c:if>
                                </td>
                                <td>
                                    ${grade.finalScore != null ? grade.finalScore : '-'}
                                    <c:if test="${component != null}">
                                        <br><small>(${component.finalPercentage}%)</small>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${grade.totalScore != null}">
                                            <strong>${grade.totalScore}</strong>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${grade.letterGrade != null}">
                                            <span class="grade-badge grade-${grade.letterGrade}">
                                                ${grade.letterGrade}
                                            </span>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${grade.gradePoint != null ? grade.gradePoint : '-'}</td>
                            </c:when>
                            <c:otherwise>
                                <td colspan="6" class="text-center text-muted">
                                    <em>Grades not yet posted</em>
                                </td>
                            </c:otherwise>
                        </c:choose>
                        <td>${enrollment.course.credits}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty enrollments}">
                    <tr>
                        <td colspan="9" class="text-center">No course enrollments found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        
        <!-- Pagination Controls for Course Grades -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/gpa/dashboard?page=${currentPage - 1}" class="page-link">« Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage eq i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/gpa/dashboard?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/gpa/dashboard?page=${currentPage + 1}" class="page-link">Next »</a>
                </c:if>
            </div>
        </c:if>
    </div>

    <!-- Grading Scale Reference -->
    <div class="card">
        <h3>📋 Grading Scale Reference</h3>
        <div class="grading-scale">
            <div class="scale-item">
                <span class="grade-badge grade-A">A</span>
                <span>93-100 (4.00)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-A-">A-</span>
                <span>90-92 (3.70)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-B+">B+</span>
                <span>87-89 (3.30)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-B">B</span>
                <span>83-86 (3.00)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-B-">B-</span>
                <span>80-82 (2.70)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-C+">C+</span>
                <span>77-79 (2.30)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-C">C</span>
                <span>73-76 (2.00)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-C-">C-</span>
                <span>70-72 (1.70)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-D+">D+</span>
                <span>67-69 (1.30)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-D">D</span>
                <span>60-66 (1.00)</span>
            </div>
            <div class="scale-item">
                <span class="grade-badge grade-F">F</span>
                <span>0-59 (0.00)</span>
            </div>
        </div>
    </div>
</div>

<style>
.gpa-summary {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.gpa-card {
    background: white;
    padding: 25px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    text-align: center;
}

.gpa-card.highlight {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.gpa-card.standing {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
}

.gpa-value {
    font-size: 36px;
    font-weight: bold;
    margin-bottom: 10px;
}

.gpa-label {
    font-size: 14px;
    opacity: 0.9;
}

.gpa-scale {
    font-size: 12px;
    opacity: 0.8;
    margin-top: 5px;
}

.gpa-badge {
    display: inline-block;
    padding: 5px 12px;
    border-radius: 20px;
    font-weight: bold;
    font-size: 14px;
}

.gpa-high {
    background: #28a745;
    color: white;
}

.gpa-good {
    background: #17a2b8;
    color: white;
}

.gpa-ok {
    background: #ffc107;
    color: #333;
}

.gpa-low {
    background: #dc3545;
    color: white;
}

.grade-badge {
    display: inline-block;
    padding: 5px 15px;
    border-radius: 5px;
    font-weight: bold;
    font-size: 16px;
}

.grade-A, .grade-A- { background: #28a745; color: white; }
.grade-B+, .grade-B, .grade-B- { background: #17a2b8; color: white; }
.grade-C+, .grade-C, .grade-C- { background: #ffc107; color: #333; }
.grade-D+, .grade-D { background: #fd7e14; color: white; }
.grade-F { background: #dc3545; color: white; }

.grading-scale {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 15px;
}

.scale-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.text-center {
    text-align: center;
}

.text-muted {
    color: #6c757d;
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
