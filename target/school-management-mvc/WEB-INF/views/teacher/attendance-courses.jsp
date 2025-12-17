<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Select Course for Attendance" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/attendance" class="active">Attendance</a></li>
        <li><a href="${pageContext.request.contextPath}/grades">Grades</a></li>
        <li><a href="${pageContext.request.contextPath}/announcements">Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📋 Attendance Management</h2>
    </div>

    <div class="card">
        <h3>Select a Course</h3>
        <c:choose>
            <c:when test="${timeFiltered}">
                <p>Showing courses happening right now based on today and current period.</p>
            </c:when>
            <c:otherwise>
                <p>Choose a course to record attendance:</p>
            </c:otherwise>
        </c:choose>
        <div class="search-container">
            <input type="text" id="searchInput" class="search-box" placeholder="🔍 Search by course code or name..." onkeyup="searchCourses()">
        </div>
        
        <div class="course-grid">
            <c:forEach var="course" items="${courses}">
                <div class="course-card">
                    <h4>${course.courseCode}</h4>
                    <p>${course.courseName}</p>
                    <div class="course-info">
                        <span>📍 ${course.roomNumber}</span>
                        <span>👥 ${course.maxStudents} students</span>
                    </div>
                          <a href="${pageContext.request.contextPath}/attendance/take?courseId=${course.courseId}" 
                              class="btn btn-primary btn-block">Take Attendance</a>
                          <a href="${pageContext.request.contextPath}/attendance/view?courseId=${course.courseId}"
                              class="btn btn-secondary btn-block" style="margin-top:8px;">View Attendance</a>
                </div>
            </c:forEach>
            <c:if test="${empty courses}">
                <p class="text-center">No courses are scheduled for you at this time slot.</p>
            </c:if>
        </div>
        <div id="noResults" style="display: none; text-align: center; padding: 20px; color: #999;">No courses found matching your search.</div>
        
        <!-- Search Pagination -->
        <div id="searchPagination" class="pagination" style="display: none;"></div>
    </div>
</div>

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

.course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.course-card {
    background: white;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    padding: 20px;
    transition: box-shadow 0.3s;
}

.course-card:hover {
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.course-card h4 {
    color: #667eea;
    margin-bottom: 10px;
}

.course-info {
    display: flex;
    gap: 15px;
    margin: 15px 0;
    font-size: 14px;
    color: #6c757d;
}

.btn-block {
    width: 100%;
    margin-top: 10px;
}
</style>

<script>
const pageSize = 6;
let currentSearchPage = 1;
let matchedCards = [];

function searchCourses() {
    const input = document.getElementById('searchInput');
    const filter = input.value.toUpperCase();
    const courseGrid = document.querySelector('.course-grid');
    const courseCards = courseGrid.getElementsByClassName('course-card');
    const searchPagination = document.getElementById('searchPagination');
    
    matchedCards = [];
    currentSearchPage = 1;
    
    for (let i = 0; i < courseCards.length; i++) {
        const h4 = courseCards[i].getElementsByTagName('h4')[0];
        const p = courseCards[i].getElementsByTagName('p')[0];
        
        if (h4 && p) {
            const codeValue = h4.textContent || h4.innerText;
            const nameValue = p.textContent || p.innerText;
            
            if (codeValue.toUpperCase().indexOf(filter) > -1 ||
                nameValue.toUpperCase().indexOf(filter) > -1) {
                matchedCards.push(courseCards[i]);
            }
        }
    }
    
    if (filter === '') {
        for (let i = 0; i < courseCards.length; i++) {
            courseCards[i].style.display = '';
        }
        document.getElementById('noResults').style.display = 'none';
        searchPagination.style.display = 'none';
    } else if (matchedCards.length === 0) {
        for (let i = 0; i < courseCards.length; i++) {
            courseCards[i].style.display = 'none';
        }
        document.getElementById('noResults').style.display = 'block';
        searchPagination.style.display = 'none';
    } else {
        for (let i = 0; i < courseCards.length; i++) {
            courseCards[i].style.display = 'none';
        }
        displaySearchPage(1);
        document.getElementById('noResults').style.display = 'none';
        
        if (matchedCards.length > pageSize) {
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
    
    for (let i = 0; i < matchedCards.length; i++) {
        if (i >= start && i < end) {
            matchedCards[i].style.display = '';
        } else {
            matchedCards[i].style.display = 'none';
        }
    }
}

function renderSearchPagination() {
    const searchPagination = document.getElementById('searchPagination');
    const totalPages = Math.ceil(matchedCards.length / pageSize);
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
