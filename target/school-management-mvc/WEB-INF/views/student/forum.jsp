<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="${course.courseName} - Forum" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <c:choose>
            <c:when test="${sessionScope.user.userType == 'ADMIN'}">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/semesters/list">Manage Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
                <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
                <li><a href="${pageContext.request.contextPath}/gpa/admin/view">Student GPA</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
            </c:when>
            <c:when test="${sessionScope.user.userType == 'TEACHER'}">
                <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/courses" class="active">My Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
                <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/student/courses" class="active">My Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
                <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
                <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <div>
            <h2>💬 ${course.courseCode} - Forum</h2>
            <p style="color: #666; margin-top: 5px;">${course.courseName}</p>
        </div>
        <a href="${pageContext.request.contextPath}/forum/create?courseId=${course.courseId}" class="btn btn-primary">
            + New Discussion
        </a>
    </div>

    <c:if test="${param.success == 'created'}">
        <div class="alert alert-success">✅ Discussion created successfully!</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">✅ Post deleted successfully!</div>
    </c:if>

    <div class="card">
        <c:if test="${empty posts}">
            <div style="text-align: center; padding: 40px; color: #999;">
                <p>💭 No discussions yet.</p>
                <p><a href="${pageContext.request.contextPath}/forum/create?courseId=${course.courseId}">Start the first discussion</a></p>
            </div>
        </c:if>

        <div class="forum-posts">
            <c:forEach var="post" items="${posts}">
                <a href="${pageContext.request.contextPath}/forum/post?postId=${post.postId}" class="forum-post-link">
                    <div class="forum-post-item">
                        <div class="forum-post-content">
                            <h3 class="forum-post-title">${post.title}</h3>
                            <div class="forum-post-preview">
                                <c:choose>
                                    <c:when test="${post.content.length() > 150}">
                                        ${post.content.substring(0, 150)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${post.content}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="forum-post-meta">
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
                                    <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy"/>
                                </span>
                            </div>
                        </div>
                        <div class="forum-post-stats">
                            <div class="stat-item">
                                <span class="stat-number">${post.replyCount}</span>
                                <span class="stat-label">Replies</span>
                            </div>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</div>

<style>
    .content-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 20px;
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
    
    .forum-posts {
        display: flex;
        flex-direction: column;
        gap: 10px;
    }
    
    .forum-post-link {
        text-decoration: none;
        color: inherit;
        display: block;
    }
    
    .forum-post-item {
        display: flex;
        justify-content: space-between;
        padding: 20px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        background: white;
        transition: all 0.2s;
    }
    
    .forum-post-item:hover {
        border-color: #667eea;
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
        transform: translateX(5px);
    }
    
    .forum-post-content {
        flex: 1;
    }
    
    .forum-post-title {
        margin: 0 0 10px 0;
        font-size: 18px;
        color: #333;
        font-weight: 600;
    }
    
    .forum-post-preview {
        color: #666;
        line-height: 1.5;
        margin-bottom: 10px;
        white-space: pre-wrap;
    }
    
    .forum-post-meta {
        display: flex;
        gap: 15px;
        font-size: 13px;
        color: #777;
    }
    
    .post-author {
        font-weight: 500;
    }
    
    .forum-post-stats {
        display: flex;
        align-items: center;
        padding-left: 20px;
        border-left: 1px solid #eee;
    }
    
    .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        min-width: 80px;
    }
    
    .stat-number {
        font-size: 24px;
        font-weight: 700;
        color: #667eea;
    }
    
    .stat-label {
        font-size: 12px;
        color: #999;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
