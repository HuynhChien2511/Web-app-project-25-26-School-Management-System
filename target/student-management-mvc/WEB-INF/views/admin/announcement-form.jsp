<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Create Announcement" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements" class="active">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📝 Create Announcement</h2>
    </div>

    <c:if test="${param.error == 'failed'}">
        <div class="alert alert-error">❌ Failed to create announcement. Please try again.</div>
    </c:if>

    <div class="card">
        <form action="${pageContext.request.contextPath}/announcements/create" method="post">
            <div class="form-group">
                <label for="courseId">Announcement Type *</label>
                <select id="courseId" name="courseId" class="form-control" required>
                    <option value="0">📢 School-Wide Announcement (All Users)</option>
                    <optgroup label="Course-Specific Announcements">
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.courseId}">${course.courseCode} - ${course.courseName}</option>
                        </c:forEach>
                    </optgroup>
                </select>
                <small>Choose whether this announcement is for all users or a specific course</small>
            </div>

            <div class="form-group">
                <label for="title">Title *</label>
                <input type="text" id="title" name="title" class="form-control" 
                       placeholder="Enter announcement title" required maxlength="200">
            </div>

            <div class="form-group">
                <label for="content">Message *</label>
                <textarea id="content" name="content" class="form-control" rows="8" 
                          placeholder="Enter announcement message" required></textarea>
            </div>

            <div class="form-group">
                <label class="checkbox-label">
                    <input type="checkbox" name="isImportant" value="true">
                    <span>⚠️ Mark as Important</span>
                </label>
                <small>Important announcements are highlighted and appear at the top</small>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Create Announcement</button>
                <a href="${pageContext.request.contextPath}/announcements" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<style>
    .form-group {
        margin-bottom: 20px;
    }
    
    .form-group label {
        display: block;
        font-weight: 600;
        margin-bottom: 8px;
        color: #333;
    }
    
    .form-control {
        width: 100%;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 14px;
    }
    
    .form-control:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    textarea.form-control {
        resize: vertical;
        font-family: inherit;
    }
    
    small {
        display: block;
        margin-top: 5px;
        color: #666;
        font-size: 13px;
    }
    
    .checkbox-label {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
    }
    
    .checkbox-label input[type="checkbox"] {
        margin: 0;
        cursor: pointer;
    }
    
    .checkbox-label span {
        font-weight: 500;
    }
    
    .form-actions {
        display: flex;
        gap: 10px;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 1px solid #eee;
    }
    
    .btn {
        padding: 10px 24px;
        border: none;
        border-radius: 4px;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        text-decoration: none;
        display: inline-block;
        transition: all 0.2s;
    }
    
    .btn-primary {
        background: #667eea;
        color: white;
    }
    
    .btn-primary:hover {
        background: #5568d3;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
    }
    
    .btn-secondary {
        background: #e0e7ff;
        color: #667eea;
    }
    
    .btn-secondary:hover {
        background: #c7d2fe;
    }
    
    .alert {
        padding: 12px 16px;
        border-radius: 4px;
        margin-bottom: 20px;
    }
    
    .alert-error {
        background: #fee;
        color: #c33;
        border: 1px solid #fcc;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
