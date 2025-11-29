<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Manage Semesters" scope="request"/>
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
        <h2>Manage Semesters</h2>
        <a href="${pageContext.request.contextPath}/admin/semesters/add" class="btn btn-primary">Add New Semester</a>
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

    <div class="card">
        <h3>Semesters</h3>
        <table class="table">
            <thead>
                <tr>
                    <th>Semester Name</th>
                    <th>Type</th>
                    <th>Academic Year</th>
                    <th>Duration</th>
                    <th>Dates</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="semester" items="${semesters}">
                    <tr>
                        <td><strong>${semester.semesterName}</strong></td>
                        <td>
                            <c:choose>
                                <c:when test="${semester.semesterType == 'SEMESTER_1'}">Semester 1</c:when>
                                <c:when test="${semester.semesterType == 'SEMESTER_2'}">Semester 2</c:when>
                                <c:when test="${semester.semesterType == 'SEMESTER_3'}">Summer</c:when>
                            </c:choose>
                        </td>
                        <td>${semester.academicYear}</td>
                        <td>${semester.weeks} weeks</td>
                        <td>${semester.startDate} to ${semester.endDate}</td>
                        <td>
                            <c:choose>
                                <c:when test="${semester.active}">
                                    <span class="badge badge-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-secondary">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/admin/semesters/components?semesterId=${semester.id}" 
                               class="btn btn-sm btn-info" title="Grade Components">
                                📊 Components
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/semesters/edit?id=${semester.id}" 
                               class="btn btn-sm btn-warning" title="Edit">
                                ✏️ Edit
                            </a>
                            <c:if test="${!semester.active}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/semesters/activate" 
                                      style="display: inline;">
                                    <input type="hidden" name="semesterId" value="${semester.id}">
                                    <button type="submit" class="btn btn-sm btn-success" 
                                            onclick="return confirm('Activate this semester? This will deactivate all other semesters.')">
                                        ✓ Activate
                                    </button>
                                </form>
                            </c:if>
                            <form method="post" action="${pageContext.request.contextPath}/admin/semesters/delete" 
                                  style="display: inline;">
                                <input type="hidden" name="semesterId" value="${semester.id}">
                                <button type="submit" class="btn btn-sm btn-danger" 
                                        onclick="return confirm('Are you sure you want to delete this semester?')">
                                    🗑️ Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty semesters}">
                    <tr>
                        <td colspan="7" class="text-center">No semesters found. Create one to get started!</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.badge {
    padding: 5px 10px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
}
.badge-success {
    background-color: #28a745;
    color: white;
}
.badge-secondary {
    background-color: #6c757d;
    color: white;
}
.actions form {
    margin: 0;
}
.btn-sm {
    padding: 5px 10px;
    font-size: 13px;
    margin: 2px;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
