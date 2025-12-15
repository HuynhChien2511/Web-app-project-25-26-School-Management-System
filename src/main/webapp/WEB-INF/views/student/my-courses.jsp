<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Courses" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses" class="active">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/gpa/dashboard">My Grades & GPA</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>My Courses</h2>
    </div>
    
    <c:if test="${param.success == 'enrolled'}">
        <div class="alert alert-success">Successfully enrolled in the course!</div>
    </c:if>
    <c:if test="${param.success == 'dropped'}">
        <div class="alert alert-success">Successfully dropped the course!</div>
    </c:if>
    
    <div class="card">
        <div class="search-container">
            <input type="text" id="searchInput" class="search-box" placeholder="🔍 Search by course code or name..." onkeyup="searchTable()">
        </div>
        <table class="table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Enrollment Date</th>
                    <th>Grade</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="enrollment" items="${enrollments}">
                    <tr>
                        <td>${enrollment.courseCode}</td>
                        <td>${enrollment.courseName}</td>
                        <td>${enrollment.enrollmentDate}</td>
                        <td>${enrollment.grade != null ? enrollment.grade : 'Not graded yet'}</td>
                        <td>
                            <span class="badge badge-${enrollment.status == 'ACTIVE' ? 'active' : 
                                                       enrollment.status == 'COMPLETED' ? 'completed' : 'dropped'}">
                                ${enrollment.status}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/student/courses/details?courseId=${enrollment.courseId}" 
                               class="btn btn-info btn-sm">Details</a>
                            <c:if test="${enrollment.status == 'ACTIVE'}">
                                <a href="${pageContext.request.contextPath}/forum/course?courseId=${enrollment.courseId}" 
                                   class="btn btn-success btn-sm">💬 Forum</a>
                                <form action="${pageContext.request.contextPath}/student/courses/drop" method="post" style="display:inline;"
                                      onsubmit="return confirm('Are you sure you want to drop this course?');">
                                    <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                                    <button type="submit" class="btn btn-danger btn-sm">Drop</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty enrollments}">
                    <tr>
                        <td colspan="6" style="text-align: center;">
                            You are not enrolled in any courses yet.
                            <a href="${pageContext.request.contextPath}/student/courses/available">Browse available courses</a>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <div id="noResults" style="display: none; text-align: center; padding: 20px; color: #999;">No courses found matching your search.</div>
        
        <!-- Search Pagination -->
        <div id="searchPagination" class="pagination" style="display: none;"></div>
        
        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/student/courses?page=${currentPage - 1}" class="page-link">&laquo; Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span class="page-link active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/student/courses?page=${i}" class="page-link">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/student/courses?page=${currentPage + 1}" class="page-link">Next &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

<style>
    .btn-sm {
        padding: 5px 10px;
        font-size: 12px;
        margin-right: 5px;
    }
    
    /* Pagination Styles */
    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 5px;
        margin-top: 20px;
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

<style>
.search-container {
    margin-bottom: 20px;
}

.search-box {
    width: 100%;
    padding: 12px 20px;
    font-size: 14px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    transition: all 0.3s;
}

.search-box:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}
</style>

<script>
const pageSize = 10;
let currentSearchPage = 1;
let matchedRows = [];

function searchTable() {
    const input = document.getElementById('searchInput');
    const filter = input.value.toUpperCase();
    const table = document.querySelector('.table');
    const tbody = table.querySelector('tbody');
    const tr = tbody.getElementsByTagName('tr');
    const originalPagination = document.querySelector('.pagination:not(#searchPagination)');
    const searchPagination = document.getElementById('searchPagination');
    
    matchedRows = [];
    currentSearchPage = 1;
    
    for (let i = 0; i < tr.length; i++) {
        const tdCode = tr[i].getElementsByTagName('td')[0];
        const tdName = tr[i].getElementsByTagName('td')[1];
        
        if (tdCode && tdName) {
            const codeValue = tdCode.textContent || tdCode.innerText;
            const nameValue = tdName.textContent || tdName.innerText;
            
            if (codeValue.toUpperCase().indexOf(filter) > -1 ||
                nameValue.toUpperCase().indexOf(filter) > -1) {
                matchedRows.push(tr[i]);
            }
        }
    }
    
    if (filter === '') {
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = '';
        }
        document.getElementById('noResults').style.display = 'none';
        if (originalPagination) originalPagination.style.display = '';
        searchPagination.style.display = 'none';
    } else if (matchedRows.length === 0) {
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = 'none';
        }
        document.getElementById('noResults').style.display = 'block';
        if (originalPagination) originalPagination.style.display = 'none';
        searchPagination.style.display = 'none';
    } else {
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = 'none';
        }
        displaySearchPage(1);
        document.getElementById('noResults').style.display = 'none';
        if (originalPagination) originalPagination.style.display = 'none';
        
        if (matchedRows.length > pageSize) {
            renderSearchPagination();
            searchPagination.style.display = 'flex';
        } else {
            searchPagination.style.display = 'none';
        }
    }
}

function displaySearchPage(page) {
    currentSearchPage = page;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    
    for (let i = 0; i < matchedRows.length; i++) {
        if (i >= start && i < end) {
            matchedRows[i].style.display = '';
        } else {
            matchedRows[i].style.display = 'none';
        }
    }
}

function renderSearchPagination() {
    const searchPagination = document.getElementById('searchPagination');
    const totalPages = Math.ceil(matchedRows.length / pageSize);
    let html = '';
    
    if (currentSearchPage > 1) {
        html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${currentSearchPage - 1}); renderSearchPagination();">« Previous</a>`;
    }
    
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentSearchPage) {
            html += `<span class="page-link active">${i}</span>`;
        } else {
            html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${i}); renderSearchPagination();">${i}</a>`;
        }
    }
    
    if (currentSearchPage < totalPages) {
        html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${currentSearchPage + 1}); renderSearchPagination();">Next »</a>`;
    }
    
    searchPagination.innerHTML = html;
}
</script>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
