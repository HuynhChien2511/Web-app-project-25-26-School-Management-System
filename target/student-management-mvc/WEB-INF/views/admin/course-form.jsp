<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="${course != null ? 'Edit Course' : 'Add Course'}" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses" class="active">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>${course != null ? 'Edit Course' : 'Add New Course'}</h2>
    </div>
    
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    
    <div class="card">
        <form action="${pageContext.request.contextPath}/admin/courses/${course != null ? 'update' : 'add'}" method="post">
            <c:if test="${course != null}">
                <input type="hidden" name="courseId" value="${course.courseId}">
            </c:if>
            
            <div class="form-group">
                <label for="courseCode">Course Code *</label>
                <input type="text" id="courseCode" name="courseCode" value="${course.courseCode}" required>
            </div>
            
            <div class="form-group">
                <label for="courseName">Course Name *</label>
                <input type="text" id="courseName" name="courseName" value="${course.courseName}" required>
            </div>
            
            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="4">${course.description}</textarea>
            </div>
            
            <div class="form-group">
                <label for="credits">Credits *</label>
                <input type="number" id="credits" name="credits" value="${course.credits}" min="1" max="10" required>
            </div>
            
            <div class="form-group">
                <label for="teacherId">Assign Teacher</label>
                <select id="teacherId" name="teacherId">
                    <option value="">-- Select Teacher --</option>
                    <c:forEach var="teacher" items="${teachers}">
                        <option value="${teacher.userId}" ${course.teacherId == teacher.userId ? 'selected' : ''}>
                            ${teacher.fullName}
                        </option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label for="maxStudents">Max Students *</label>
                <input type="number" id="maxStudents" name="maxStudents" value="${course.maxStudents != 0 ? course.maxStudents : 30}" min="1" required>
            </div>
            
            <div class="form-group">
                <label for="scheduleDays">Schedule Days</label>
                <input type="text" id="scheduleDays" name="scheduleDays" value="${course.scheduleDays}" placeholder="e.g., Mon, Wed, Fri">
                <small style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 5px;">
                    Enter days when this course meets (e.g., Mon, Wed, Fri)
                </small>
            </div>
            
            <div class="form-group">
                <label for="startPeriod">Start Period</label>
                <select id="startPeriod" name="startPeriod">
                    <option value="">-- Select Start Period --</option>
                    <option value="1" ${course.scheduleTime != null && course.scheduleTime.contains('08:00') ? 'selected' : ''}>Period 1 (08:00 - 08:45)</option>
                    <option value="2" ${course.scheduleTime != null && course.scheduleTime.contains('08:45') ? 'selected' : ''}>Period 2 (08:45 - 09:30)</option>
                    <option value="3" ${course.scheduleTime != null && course.scheduleTime.contains('09:30') ? 'selected' : ''}>Period 3 (09:30 - 10:15)</option>
                    <option value="4" ${course.scheduleTime != null && course.scheduleTime.contains('10:15') ? 'selected' : ''}>Period 4 (10:15 - 11:00)</option>
                    <option value="5" ${course.scheduleTime != null && course.scheduleTime.contains('11:00') ? 'selected' : ''}>Period 5 (11:00 - 11:45)</option>
                    <option value="6" ${course.scheduleTime != null && course.scheduleTime.contains('13:00') ? 'selected' : ''}>Period 6 (13:00 - 13:45)</option>
                    <option value="7" ${course.scheduleTime != null && course.scheduleTime.contains('13:45') ? 'selected' : ''}>Period 7 (13:45 - 14:30)</option>
                    <option value="8" ${course.scheduleTime != null && course.scheduleTime.contains('14:30') ? 'selected' : ''}>Period 8 (14:30 - 15:15)</option>
                    <option value="9" ${course.scheduleTime != null && course.scheduleTime.contains('15:15') ? 'selected' : ''}>Period 9 (15:15 - 16:00)</option>
                    <option value="10" ${course.scheduleTime != null && course.scheduleTime.contains('16:00') ? 'selected' : ''}>Period 10 (16:00 - 16:45)</option>
                </select>
                <small style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 5px;">
                    Select the starting period for this course
                </small>
            </div>
            
            <div class="form-group">
                <label for="numPeriods">Number of Periods</label>
                <select id="numPeriods" name="numPeriods">
                    <option value="">-- Select Duration --</option>
                    <option value="1">1 Period (45 minutes)</option>
                    <option value="2" selected>2 Periods (90 minutes)</option>
                    <option value="3">3 Periods (135 minutes)</option>
                    <option value="4">4 Periods (180 minutes)</option>
                </select>
                <small style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 5px;">
                    Each period is 45 minutes. Maximum 4 periods per class.
                </small>
            </div>
            
            <div class="form-group">
                <label for="roomNumber">Room Number</label>
                <input type="text" id="roomNumber" name="roomNumber" value="${course.roomNumber}" placeholder="e.g., A101">
                <small style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 5px;">
                    Enter the room or building number
                </small>
            </div>
            
            <div style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary">${course != null ? 'Update' : 'Add'} Course</button>
                <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-danger">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
