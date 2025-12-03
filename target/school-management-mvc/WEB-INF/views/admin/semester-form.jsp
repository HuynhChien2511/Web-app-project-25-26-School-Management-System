<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="${semester != null ? 'Edit Semester' : 'Add Semester'}" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/semesters/list" class="active">Manage Semesters</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>${semester != null ? 'Edit Semester' : 'Add New Semester'}</h2>
    </div>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/admin/semesters/${semester != null ? 'update' : 'create'}">
            <c:if test="${semester != null}">
                <input type="hidden" name="semesterId" value="${semester.id}">
            </c:if>

            <div class="form-group">
                <label for="semesterName">Semester Name *</label>
                <input type="text" class="form-control" id="semesterName" name="semesterName" 
                       value="${semester.semesterName}" placeholder="e.g., Fall 2024" required>
            </div>

            <div class="form-group">
                <label for="semesterType">Semester Type *</label>
                <select class="form-control" id="semesterType" name="semesterType" 
                        onchange="updateWeeks()" required>
                    <option value="SEMESTER_1" ${semester.semesterType == 'SEMESTER_1' ? 'selected' : ''}>
                        Semester 1 (16 weeks)
                    </option>
                    <option value="SEMESTER_2" ${semester.semesterType == 'SEMESTER_2' ? 'selected' : ''}>
                        Semester 2 (16 weeks)
                    </option>
                    <option value="SEMESTER_3" ${semester.semesterType == 'SEMESTER_3' ? 'selected' : ''}>
                        Summer Semester (8 weeks)
                    </option>
                </select>
            </div>

            <div class="form-group">
                <label for="academicYear">Academic Year *</label>
                <input type="text" class="form-control" id="academicYear" name="academicYear" 
                       value="${semester.academicYear}" placeholder="e.g., 2024-2025" 
                       pattern="\d{4}-\d{4}" required>
                <small>Format: YYYY-YYYY (e.g., 2024-2025)</small>
            </div>

            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="startDate">Start Date *</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" 
                           value="${semester.startDate}" required>
                </div>

                <div class="form-group col-md-6">
                    <label for="endDate">End Date *</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" 
                           value="${semester.endDate}" required>
                </div>
            </div>

            <div class="form-group">
                <label for="weeks">Duration (weeks) *</label>
                <input type="number" class="form-control" id="weeks" name="weeks" 
                       value="${semester.weeks != 0 ? semester.weeks : 16}" 
                       min="1" max="20" required>
            </div>

            <div class="form-group">
                <div class="form-check">
                    <input type="checkbox" class="form-check-input" id="isActive" name="isActive" 
                           ${semester.active ? 'checked' : ''}>
                    <label class="form-check-label" for="isActive">
                        Set as Active Semester
                        <small class="text-muted">(Only one semester can be active at a time)</small>
                    </label>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    ${semester != null ? 'Update Semester' : 'Create Semester'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/semesters/list" 
                   class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
function updateWeeks() {
    var semesterType = document.getElementById('semesterType').value;
    var weeksInput = document.getElementById('weeks');
    
    if (semesterType === 'SEMESTER_3') {
        weeksInput.value = 8;
    } else {
        weeksInput.value = 16;
    }
}
</script>

<style>
.form-row {
    display: flex;
    gap: 20px;
}
.form-row .form-group {
    flex: 1;
}
.form-check {
    padding: 10px;
    background-color: #f8f9fa;
    border-radius: 5px;
}
.text-muted {
    color: #6c757d;
    display: block;
    margin-top: 5px;
}
.form-actions {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #dee2e6;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
