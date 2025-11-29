<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="${post.title}" scope="request"/>
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
        <span>Discussion</span>
    </div>

    <c:if test="${param.success == 'replied'}">
        <div class="alert alert-success">✅ Reply posted successfully!</div>
    </c:if>

    <!-- Original Post -->
    <div class="card">
        <div class="post-header">
            <h2>${post.title}</h2>
            <div class="post-meta">
                <span class="post-author">
                    <c:choose>
                        <c:when test="${post.authorType == 'TEACHER'}">
                            👨‍🏫 ${post.authorName}
                        </c:when>
                        <c:otherwise>
                            👤 ${post.authorName}
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="post-date">
                    <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                </span>
            </div>
        </div>
        <div class="post-content">
            ${post.content}
        </div>
    </div>

    <!-- Replies -->
    <div class="replies-section">
        <h3>💬 Replies (${replies.size()})</h3>
        
        <c:forEach var="reply" items="${replies}">
            <div class="reply-item">
                <div class="reply-header">
                    <span class="reply-author">
                        <c:choose>
                            <c:when test="${reply.authorType == 'TEACHER'}">
                                👨‍🏫 ${reply.authorName}
                            </c:when>
                            <c:otherwise>
                                👤 ${reply.authorName}
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <span class="reply-date">
                        <fmt:formatDate value="${reply.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                    </span>
                </div>
                <div class="reply-content">
                    ${reply.content}
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty replies}">
            <p style="text-align: center; color: #999; padding: 20px;">No replies yet. Be the first to reply!</p>
        </c:if>
    </div>

    <!-- Reply Form -->
    <div class="card">
        <h3>✍️ Post a Reply</h3>
        <form action="${pageContext.request.contextPath}/forum/reply" method="post">
            <input type="hidden" name="postId" value="${post.postId}">
            
            <div class="form-group">
                <textarea name="content" class="form-control" rows="5" 
                          placeholder="Write your reply..." required></textarea>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Post Reply</button>
                <a href="${pageContext.request.contextPath}/forum/course?courseId=${course.courseId}" class="btn btn-secondary">Back to Forum</a>
            </div>
        </form>
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
    
    .alert-success {
        background: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
    }
    
    .post-header {
        border-bottom: 2px solid #667eea;
        padding-bottom: 15px;
        margin-bottom: 20px;
    }
    
    .post-header h2 {
        margin: 0 0 10px 0;
        color: #333;
    }
    
    .post-meta, .reply-header {
        display: flex;
        gap: 15px;
        font-size: 14px;
        color: #777;
    }
    
    .post-author, .reply-author {
        font-weight: 500;
    }
    
    .post-content {
        color: #555;
        line-height: 1.7;
        white-space: pre-wrap;
        font-size: 15px;
    }
    
    .replies-section {
        margin: 30px 0;
    }
    
    .replies-section h3 {
        margin-bottom: 20px;
        color: #333;
    }
    
    .reply-item {
        background: #f8f9fa;
        border-left: 3px solid #667eea;
        padding: 15px 20px;
        margin-bottom: 15px;
        border-radius: 4px;
    }
    
    .reply-header {
        margin-bottom: 10px;
    }
    
    .reply-content {
        color: #555;
        line-height: 1.6;
        white-space: pre-wrap;
    }
    
    .form-group {
        margin-bottom: 20px;
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
    
    .form-actions {
        display: flex;
        gap: 10px;
    }
    
    .btn-secondary {
        background: #6c757d;
    }
    
    .btn-secondary:hover {
        background: #5a6268;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
