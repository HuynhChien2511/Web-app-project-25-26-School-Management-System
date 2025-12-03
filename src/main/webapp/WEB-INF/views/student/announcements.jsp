<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Announcements" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements" class="active">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📢 Announcements</h2>
    </div>

    <div class="card">
        <c:if test="${empty announcements}">
            <div style="text-align: center; padding: 40px; color: #999;">
                <p>📭 No announcements at this time.</p>
            </div>
        </c:if>

        <c:forEach var="announcement" items="${announcements}">
            <div class="announcement-item ${announcement.important ? 'important' : ''}">
                <div class="announcement-header">
                    <div class="announcement-title-section">
                        <c:if test="${announcement.important}">
                            <span class="badge badge-important">⚠️ Important</span>
                        </c:if>
                        <h3>${announcement.title}</h3>
                    </div>
                    <div class="announcement-meta">
                        <c:choose>
                            <c:when test="${announcement.courseSpecific}">
                                <span class="announcement-course">${announcement.courseCode}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="announcement-school">🏫 School-Wide</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="announcement-content">
                    ${announcement.content}
                </div>
                
                <div class="announcement-footer">
                    <span class="announcement-author">By: ${announcement.authorName}</span>
                    <span class="announcement-date">
                        <fmt:formatDate value="${announcement.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                    </span>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<style>
    .announcement-item {
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 15px;
        background: white;
        transition: box-shadow 0.2s;
    }
    
    .announcement-item:hover {
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    
    .announcement-item.important {
        border-left: 4px solid #f44336;
        background: #fff8f8;
    }
    
    .announcement-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 15px;
    }
    
    .announcement-title-section {
        flex: 1;
    }
    
    .announcement-title-section h3 {
        margin: 5px 0;
        color: #333;
        font-size: 18px;
    }
    
    .badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        margin-bottom: 5px;
    }
    
    .badge-important {
        background: #f44336;
        color: white;
    }
    
    .announcement-meta {
        margin-left: 15px;
    }
    
    .announcement-course {
        background: #667eea;
        color: white;
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
    }
    
    .announcement-school {
        background: #4caf50;
        color: white;
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
    }
    
    .announcement-content {
        color: #555;
        line-height: 1.6;
        margin-bottom: 15px;
        white-space: pre-wrap;
    }
    
    .announcement-footer {
        display: flex;
        justify-content: space-between;
        padding-top: 15px;
        border-top: 1px solid #f0f0f0;
        font-size: 13px;
        color: #777;
    }
    
    .announcement-author {
        font-weight: 500;
    }
    
    .announcement-date {
        font-style: italic;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
