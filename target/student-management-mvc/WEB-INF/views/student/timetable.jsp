<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Timetable" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/courses/available">Available Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/student/timetable" class="active">Timetable</a></li>
    </ul>
</div>

<div class="main-content">
    <div class="content-header">
        <h2>📅 My Weekly Timetable</h2>
    </div>
    
    <div class="card">
        <div class="timetable-container">
            <table class="timetable">
                <thead>
                    <tr>
                        <th class="time-column">Period</th>
                        <th>Monday</th>
                        <th>Tuesday</th>
                        <th>Wednesday</th>
                        <th>Thursday</th>
                        <th>Friday</th>
                        <th>Saturday</th>
                        <th>Sunday</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        String[] periodTimes = {
                            "08:00-08:45", "08:45-09:30", "09:30-10:15", "10:15-11:00", "11:00-11:45",
                            "11:45-12:30", "13:00-13:45", "13:45-14:30", "14:30-15:15", "15:15-16:00"
                        };
                        java.util.List enrollments = (java.util.List) request.getAttribute("enrollments");
                    %>
                    <c:forEach var="period" begin="1" end="10">
                        <tr>
                            <td class="time-cell">
                                P${period}<br>
                                <small><%=periodTimes[(Integer)pageContext.getAttribute("period")-1]%></small>
                            </td>
                            <% String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                               for (String day : days) { %>
                                <td class="schedule-cell">
                                    <c:forEach var="enrollment" items="${enrollments}">
                                        <c:if test="${enrollment.status == 'ACTIVE' && not empty enrollment.course.scheduleDays && not empty enrollment.course.scheduleTime}">
                                            <c:set var="course" value="${enrollment.course}" />
                                            <%
                                                com.school.model.Enrollment enrollment = (com.school.model.Enrollment) pageContext.getAttribute("enrollment");
                                                if (enrollment != null && enrollment.getCourse() != null) {
                                                    com.school.model.Course course = enrollment.getCourse();
                                                    String scheduleDays = course.getScheduleDays();
                                                    String scheduleTime = course.getScheduleTime();
                                                    int currentPeriod = (Integer) pageContext.getAttribute("period");
                                                    
                                                    if (scheduleDays != null && scheduleTime != null && 
                                                        (scheduleDays.contains(day) || scheduleDays.contains(day.substring(0, 3)))) {
                                                        
                                                        // Parse P3-P4 format
                                                        if (scheduleTime.contains("P")) {
                                                            int pStart = scheduleTime.indexOf("P");
                                                            int dash = scheduleTime.indexOf("-", pStart);
                                                            int pEnd = scheduleTime.indexOf("P", dash);
                                                            int paren = scheduleTime.indexOf("(");
                                                            
                                                            if (pStart >= 0 && dash > 0 && pEnd > 0 && paren > 0) {
                                                                String startStr = scheduleTime.substring(pStart + 1, dash).trim();
                                                                String endStr = scheduleTime.substring(pEnd + 1, paren).trim();
                                                                
                                                                try {
                                                                    int startPeriod = Integer.parseInt(startStr);
                                                                    int endPeriod = Integer.parseInt(endStr);
                                                                    
                                                                    if (currentPeriod == startPeriod) {
                                                                        int numPeriods = endPeriod - startPeriod + 1;
                                                                        int height = numPeriods * 60 - 8;
                                            %>
                                                                        <div class="course-block" style="height: <%=height%>px;">
                                                                            <div class="course-code"><%=course.getCourseCode()%></div>
                                                                            <div class="course-name"><%=course.getCourseName()%></div>
                                                                            <div class="course-room">📍 <%=course.getRoomNumber()%></div>
                                                                            <div class="course-teacher">👨‍🏫 <%=course.getTeacherName()%></div>
                                                                        </div>
                                            <%
                                                                    }
                                                                } catch (NumberFormatException e) {
                                                                    // Skip invalid format
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            %>
                                        </c:if>
                                    </c:forEach>
                                </td>
                            <% } %>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<style>
    .timetable-container {
        overflow-x: auto;
    }
    
    .timetable {
        width: 100%;
        border-collapse: collapse;
        table-layout: fixed;
    }
    
    .timetable th,
    .timetable td {
        border: 1px solid #ddd;
        min-width: 120px;
        max-width: 120px;
        width: 120px;
    }
    
    .timetable th {
        background: #667eea;
        color: white;
        padding: 12px 8px;
        text-align: center;
        font-weight: 600;
    }
    
    .time-column {
        min-width: 100px !important;
        max-width: 100px !important;
        width: 100px !important;
    }
    
    .time-cell {
        background: #f8f9fa;
        font-weight: 600;
        text-align: center;
        padding: 10px 5px;
        color: #666;
        height: 60px;
        min-height: 60px;
        max-height: 60px;
    }
    
    .time-cell small {
        font-size: 10px;
        display: block;
    }
    
    .schedule-cell {
        padding: 4px;
        vertical-align: top;
        height: 60px;
        min-height: 60px;
        max-height: 60px;
        position: relative;
        overflow: visible;
    }
    
    .course-block {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 6px;
        border-radius: 4px;
        margin: 2px;
        font-size: 10px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        overflow: hidden;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        text-align: center;
        position: absolute;
        top: 4px;
        left: 4px;
        right: 4px;
        z-index: 10;
    }
    
    .course-code {
        font-weight: bold;
        font-size: 11px;
        margin-bottom: 3px;
        overflow: hidden;
        text-overflow: ellipsis;
        word-wrap: break-word;
        width: 100%;
    }
    
    .course-name {
        font-size: 9px;
        margin-bottom: 3px;
        opacity: 0.9;
        overflow: hidden;
        text-overflow: ellipsis;
        word-wrap: break-word;
        width: 100%;
    }
    
    .course-room,
    .course-teacher {
        font-size: 8px;
        opacity: 0.8;
        overflow: hidden;
        text-overflow: ellipsis;
        word-wrap: break-word;
        width: 100%;
        margin-bottom: 2px;
    }
    
    .course-list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 15px;
        margin-top: 15px;
    }
    
    .course-legend-item {
        padding: 12px;
        background: #f8f9fa;
        border-radius: 5px;
        border-left: 4px solid #667eea;
    }
    
    .course-legend-item small {
        color: #666;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
