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
        <li><a href="${pageContext.request.contextPath}/gpa/admin/view">Student GPA</a></li>
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
        <div class="search-container">
            <input type="text" id="searchInput" class="search-box" placeholder="🔍 Search by semester name..." onkeyup="searchTable()" style="width: 100%; padding: 12px 20px; font-size: 14px; border: 2px solid #e0e0e0; border-radius: 8px; transition: all 0.3s;">
        </div>
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
    
    <!-- Pagination Controls -->
    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/admin/semesters?page=${currentPage - 1}" class="page-link">« Previous</a>
            </c:if>
            
            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${currentPage eq i}">
                        <span class="page-link active">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/admin/semesters?page=${i}" class="page-link">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            
            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/admin/semesters?page=${currentPage + 1}" class="page-link">Next »</a>
            </c:if>
        </div>
    </c:if>
</div>

<style>
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 20px 0;
    gap: 5px;
}

.page-link {
    padding: 8px 12px;
    text-decoration: none;
    color: #667eea;
    border: 1px solid #ddd;
    border-radius: 4px;
    transition: all 0.3s;
}

.page-link:hover {
    background-color: #667eea;
    color: white;
}

.page-link.active {
    background-color: #667eea;
    color: white;
    border-color: #667eea;
    pointer-events: none;
}
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
    
    // Tìm các hàng khớp với tìm kiếm
    for (let i = 0; i < tr.length; i++) {
        const tdName = tr[i].getElementsByTagName('td')[0];
        const tdType = tr[i].getElementsByTagName('td')[1];
        const tdYear = tr[i].getElementsByTagName('td')[2];
        
        if (tdName && tdType && tdYear) {
            const nameValue = tdName.textContent || tdName.innerText;
            const typeValue = tdType.textContent || tdType.innerText;
            const yearValue = tdYear.textContent || tdYear.innerText;
            
            if (nameValue.toUpperCase().indexOf(filter) > -1 ||
                typeValue.toUpperCase().indexOf(filter) > -1 ||
                yearValue.toUpperCase().indexOf(filter) > -1) {
                matchedRows.push(tr[i]);
            }
        }
    }
    
    // Hiển thị kết quả
    if (filter === '') {
        // Không có tìm kiếm - hiển thị tất cả và dùng pagination gốc
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = '';
        }
        document.getElementById('noResults').style.display = 'none';
        if (originalPagination) originalPagination.style.display = '';
        searchPagination.style.display = 'none';
    } else if (matchedRows.length === 0) {
        // Không có kết quả
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = 'none';
        }
        document.getElementById('noResults').style.display = 'block';
        if (originalPagination) originalPagination.style.display = 'none';
        searchPagination.style.display = 'none';
    } else {
        // Có kết quả - hiển thị với phân trang
        for (let i = 0; i < tr.length; i++) {
            tr[i].style.display = 'none';
        }
        displaySearchPage(1);
        document.getElementById('noResults').style.display = 'none';
        if (originalPagination) originalPagination.style.display = 'none';
        
        // Chỉ hiển thị pagination nếu kết quả > pageSize
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
    
    // Previous button
    if (currentSearchPage > 1) {
        html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${currentSearchPage - 1}); renderSearchPagination();">« Previous</a>`;
    }
    
    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentSearchPage) {
            html += `<span class="page-link active">${i}</span>`;
        } else {
            html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${i}); renderSearchPagination();">${i}</a>`;
        }
    }
    
    // Next button
    if (currentSearchPage < totalPages) {
        html += `<a href="javascript:void(0)" class="page-link" onclick="displaySearchPage(${currentSearchPage + 1}); renderSearchPagination();">Next »</a>`;
    }
    
    searchPagination.innerHTML = html;
}
</script>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
