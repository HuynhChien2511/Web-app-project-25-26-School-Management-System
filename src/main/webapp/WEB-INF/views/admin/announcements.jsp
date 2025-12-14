<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Announcements" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/semesters/list">Manage Semesters</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/admin/view">Student GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements" class="active">Announcements</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📢 Announcements Management</h2>
        <a href="${pageContext.request.contextPath}/announcements/create" class="btn btn-primary">+ Create Announcement</a>
    </div>

    <c:if test="${param.success == 'created'}">
        <div class="alert alert-success">✅ Announcement created successfully!</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">✅ Announcement deleted successfully!</div>
    </c:if>
    <c:if test="${param.error == 'unauthorized'}">
        <div class="alert alert-error">❌ You do not have the authority. Cannot delete this announcement.</div>
    </c:if>
    <c:if test="${param.error == 'deletefailed'}">
        <div class="alert alert-error">❌ Failed to delete announcement. Please try again.</div>
    </c:if>

    <div class="announcements-container">
        <c:forEach var="announcement" items="${announcements}">
            <div class="announcement-card">
                <div class="announcement-header">
                    <div class="announcement-title-section">
                        <h3>${announcement.title}</h3>
                        <div class="announcement-badges">
                            <c:if test="${announcement.important}">
                                <span class="badge badge-important">⚠️ IMPORTANT</span>
                            </c:if>
                            <c:choose>
                                <c:when test="${announcement.courseId == null}">
                                    <span class="badge badge-school-wide">📢 School-Wide</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-course">${announcement.courseCode}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="announcement-actions">
                        <form action="${pageContext.request.contextPath}/announcements/delete" method="get" style="display:inline;"
                              onsubmit="return confirm('Are you sure you want to delete this announcement?');">
                            <input type="hidden" name="id" value="${announcement.announcementId}">
                            <button type="submit" class="btn btn-danger btn-sm">🗑️ Delete</button>
                        </form>
                    </div>
                </div>
                
                <div class="announcement-content">
                    <p>${announcement.content}</p>
                </div>
                
                <div class="announcement-footer">
                    <span class="author">👤 ${announcement.authorName}</span>
                    <span class="date">📅 ${announcement.createdAt}</span>
                </div>
            </div>
        </c:forEach>
        
        <c:if test="${empty announcements}">
            <div class="empty-state">
                <p>📭 No announcements yet.</p>
                <a href="${pageContext.request.contextPath}/announcements/create" class="btn btn-primary">Create First Announcement</a>
            </div>
        </c:if>
        
        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/announcements?page=${currentPage - 1}" class="page-link">&laquo; Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/announcements?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/announcements?page=${currentPage + 1}" class="page-link">Next &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

<style>
    .content-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }
    
    .announcements-container {
        display: flex;
        flex-direction: column;
        gap: 15px;
    }
    
    .announcement-card {
        background: white;
        border: 1px solid #e0e7ff;
        border-radius: 8px;
        padding: 20px;
        transition: all 0.2s;
    }
    
    .announcement-card:hover {
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        transform: translateY(-2px);
    }
    
    .announcement-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 15px;
    }
    
    .announcement-title-section h3 {
        margin: 0 0 8px 0;
        color: #333;
        font-size: 18px;
    }
    
    .announcement-badges {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
    }
    
    .badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
    }
    
    .badge-important {
        background: #fee;
        color: #c33;
    }
    
    .badge-school-wide {
        background: #e0f2fe;
        color: #0369a1;
    }
    
    .badge-course {
        background: #f3e8ff;
        color: #7c3aed;
    }
    
    .announcement-content {
        margin: 15px 0;
        line-height: 1.6;
        color: #555;
    }
    
    .announcement-content p {
        margin: 0;
        white-space: pre-wrap;
    }
    
    .announcement-footer {
        display: flex;
        justify-content: space-between;
        margin-top: 15px;
        padding-top: 15px;
        border-top: 1px solid #eee;
        font-size: 13px;
        color: #666;
    }
    
    .announcement-actions {
        display: flex;
        gap: 8px;
    }
    
    .btn {
        padding: 8px 16px;
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
    }
    
    .btn-danger {
        background: #ef4444;
        color: white;
    }
    
    .btn-danger:hover {
        background: #dc2626;
    }
    
    .btn-sm {
        padding: 6px 12px;
        font-size: 13px;
    }
    
    .alert {
        padding: 12px 16px;
        border-radius: 4px;
        margin-bottom: 20px;
    }
    
    .alert-success {
        background: #d1fae5;
        color: #065f46;
        border: 1px solid #6ee7b7;
    }
    
    .alert-error {
        background: #fee;
        color: #c33;
        border: 1px solid #fcc;
    }
    
    .empty-state {
        text-align: center;
        padding: 60px 20px;
        background: white;
        border: 2px dashed #ddd;
        border-radius: 8px;
    }
    
    .empty-state p {
        font-size: 18px;
        color: #666;
        margin-bottom: 20px;
    }
    
    /* Pagination Styles */
    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 5px;
        margin-top: 30px;
        padding: 20px 0;
    }
    
    .page-link {
        padding: 8px 12px;
        border: 1px solid #ddd;
        background: white;
        color: #667eea;
        text-decoration: none;
        border-radius: 4px;
        transition: all 0.2s;
    }
    
    .page-link:hover {
        background: #667eea;
        color: white;
        border-color: #667eea;
    }
    
    .page-link.active {
        background: #667eea;
        color: white;
        border-color: #667eea;
        font-weight: bold;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
