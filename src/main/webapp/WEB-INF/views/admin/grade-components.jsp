<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Grade Components" scope="request"/>
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
        <h2>Grade Components - ${semester.semesterName}</h2>
        <a href="${pageContext.request.contextPath}/admin/semesters/list" class="btn btn-secondary">← Back to Semesters</a>
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
        <h3>Configure Grading Percentages</h3>
        <p class="text-muted">Set the percentage breakdown for each course. Total must equal 100%.</p>
        
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>In-Class %</th>
                    <th>Midterm %</th>
                    <th>Final %</th>
                    <th>Total</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="course" items="${courses}">
                    <c:set var="hasComponent" value="false"/>
                    <c:set var="currentComponent" value="null"/>
                    
                    <c:forEach var="comp" items="${components}">
                        <c:if test="${comp.courseId == course.courseId}">
                            <c:set var="hasComponent" value="true"/>
                            <c:set var="currentComponent" value="${comp}"/>
                        </c:if>
                    </c:forEach>
                    
                    <tr>
                        <td><strong>${course.courseCode}</strong></td>
                        <td>${course.courseName}</td>
                        <c:choose>
                            <c:when test="${hasComponent}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/semesters/components/update" 
                                      class="component-form" onsubmit="return validatePercentages(this)">
                                    <input type="hidden" name="componentId" value="${currentComponent.id}">
                                    <input type="hidden" name="semesterId" value="${semester.id}">
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="inclassPercentage" value="${currentComponent.inclassPercentage}" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="midtermPercentage" value="${currentComponent.midtermPercentage}" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="finalPercentage" value="${currentComponent.finalPercentage}" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td class="total-display">100%</td>
                                    <td>
                                        <button type="submit" class="btn btn-sm btn-primary">💾 Save</button>
                                    </td>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/admin/semesters/components/create" 
                                      class="component-form" onsubmit="return validatePercentages(this)">
                                    <input type="hidden" name="courseId" value="${course.courseId}">
                                    <input type="hidden" name="semesterId" value="${semester.id}">
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="inclassPercentage" value="20.00" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="midtermPercentage" value="30.00" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td>
                                        <input type="number" step="0.01" min="0" max="100" 
                                               name="finalPercentage" value="50.00" 
                                               class="form-control percentage-input" required>
                                    </td>
                                    <td class="total-display">100%</td>
                                    <td>
                                        <button type="submit" class="btn btn-sm btn-success">+ Create</button>
                                    </td>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
                <c:if test="${empty courses}">
                    <tr>
                        <td colspan="7" class="text-center">No courses found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<script>
// Update total percentage display as user types
document.querySelectorAll('.component-form').forEach(form => {
    const inputs = form.querySelectorAll('.percentage-input');
    const totalDisplay = form.querySelector('.total-display');
    
    inputs.forEach(input => {
        input.addEventListener('input', () => {
            const total = Array.from(inputs).reduce((sum, inp) => {
                return sum + (parseFloat(inp.value) || 0);
            }, 0);
            
            totalDisplay.textContent = total.toFixed(2) + '%';
            totalDisplay.style.color = Math.abs(total - 100) < 0.01 ? '#28a745' : '#dc3545';
        });
    });
});

function validatePercentages(form) {
    const inputs = form.querySelectorAll('.percentage-input');
    const total = Array.from(inputs).reduce((sum, input) => {
        return sum + (parseFloat(input.value) || 0);
    }, 0);
    
    if (Math.abs(total - 100) > 0.01) {
        alert('Percentages must add up to 100%. Current total: ' + total.toFixed(2) + '%');
        return false;
    }
    
    return true;
}
</script>

<style>
.component-form {
    display: contents;
}
.percentage-input {
    width: 80px;
    padding: 5px;
    text-align: right;
}
.total-display {
    font-weight: bold;
    font-size: 14px;
}
.text-muted {
    color: #6c757d;
    margin-bottom: 20px;
}
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
