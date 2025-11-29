<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="New Discussion" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <c:choose>
            <c:when test="${sessionScope.user.userType == 'ADMIN'}">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
            </c:when>
            <c:when test="${sessionScope.user.userType == 'TEACHER'}">
                <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/courses" class="active">My Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/student/courses" class="active">My Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
                <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>

<div class="main-content">
    <div class="breadcrumb">
        <a href="${pageContext.request.contextPath}/student/courses">My Courses</a> » 
        <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}">${course.courseCode} Forum</a> »
        <span>New Discussion</span>
    </div>

    <div class="content-header">
        <h2>💬 Start New Discussion</h2>
        <p style="color: #666; margin-top: 5px;">${course.courseCode} - ${course.courseName}</p>
    </div>

    <c:if test="${param.error == 'failed'}">
        <div class="alert alert-error">❌ Failed to create discussion. Please try again.</div>
    </c:if>

    <div class="card">
        <form action="${pageContext.request.contextPath}/forum/create" method="post">
            <input type="hidden" name="courseId" value="${course.courseId}">
            
            <div class="form-group">
                <label for="title">Discussion Title *</label>
                <input type="text" id="title" name="title" class="form-control" 
                       placeholder="Enter a clear, descriptive title" required maxlength="200">
                <small>Make your title specific so others can quickly understand the topic</small>
            </div>

            <div class="form-group">
                <label for="content">Message *</label>
                <textarea id="content" name="content" class="form-control" rows="10" 
                          placeholder="Describe your question, idea, or topic in detail..." required></textarea>
                <small>Provide enough context for others to understand and respond effectively</small>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Create Discussion</button>
                <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <div class="tips-card">
        <h3>💡 Tips for Great Discussions</h3>
        <ul>
            <li><strong>Be specific:</strong> A clear title helps others find and respond to your post</li>
            <li><strong>Provide context:</strong> Explain what you've tried or what you're thinking</li>
            <li><strong>Be respectful:</strong> Remember there are real people reading your posts</li>
            <li><strong>Check first:</strong> See if someone else has already asked a similar question</li>
        </ul>
    </div>
</div>

<style>
    .breadcrumb {
        margin-bottom: 20px;
        font-size: 14px;
        color: #666;
    }
    
    .breadcrumb a {
        color: #667eea;
        text-decoration: none;
    }
    
    .breadcrumb a:hover {
        text-decoration: underline;
    }
    
    .alert {
        padding: 12px 20px;
        border-radius: 4px;
        margin-bottom: 20px;
    }
    
    .alert-error {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
    
    .form-group {
        margin-bottom: 25px;
    }
    
    .form-group label {
        display: block;
        font-weight: 600;
        margin-bottom: 8px;
        color: #333;
    }
    
    .form-control {
        width: 100%;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 14px;
        font-family: inherit;
    }
    
    .form-control:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    textarea.form-control {
        resize: vertical;
    }
    
    small {
        display: block;
        margin-top: 5px;
        color: #666;
        font-size: 13px;
    }
    
    .form-actions {
        display: flex;
        gap: 10px;
        padding-top: 20px;
        border-top: 1px solid #eee;
    }
    
    .btn-secondary {
        background: #6c757d;
    }
    
    .btn-secondary:hover {
        background: #5a6268;
    }
    
    .tips-card {
        margin-top: 30px;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        border-left: 4px solid #667eea;
    }
    
    .tips-card h3 {
        margin: 0 0 15px 0;
        color: #333;
        font-size: 16px;
    }
    
    .tips-card ul {
        margin: 0;
        padding-left: 20px;
    }
    
    .tips-card li {
        margin-bottom: 8px;
        color: #555;
        line-height: 1.6;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
