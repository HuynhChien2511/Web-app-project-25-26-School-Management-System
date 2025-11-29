<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Timetable" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="sidebar">
    <ul class="sidebar-menu">
        <li><a href="${pageContext.request.contextPath}/teacher/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/courses">My Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/students">Students</a></li>
        <li><a href="${pageContext.request.contextPath}/teacher/timetable" class="active">Timetable</a></li>
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
                        java.util.List courses = (java.util.List) request.getAttribute("courses");
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
                                    <c:forEach var="course" items="${courses}">
                                        <c:if test="${not empty course.scheduleDays && not empty course.scheduleTime}">
                                            <%
                                                com.school.model.Course course = (com.school.model.Course) pageContext.getAttribute("course");
                                                if (course != null) {
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
                                                                            <div class="course-name"><%=course.getCourseName()%></div>
                                                                            <div class="course-room">📍 <%=course.getRoomNumber()%></div>
                                                                            
                                                                            <!-- Hover tooltip with all course information -->
                                                                            <div class="course-tooltip">
                                                                                <div class="tooltip-header">
                                                                                    <strong><%=course.getCourseName()%></strong>
                                                                                </div>
                                                                                <div class="tooltip-content">
                                                                                    <div class="tooltip-row">
                                                                                        <span class="tooltip-label">Course ID:</span>
                                                                                        <span class="tooltip-value"><%=course.getCourseCode()%></span>
                                                                                    </div>
                                                                                    <div class="tooltip-row">
                                                                                        <span class="tooltip-label">Course Name:</span>
                                                                                        <span class="tooltip-value"><%=course.getCourseName()%></span>
                                                                                    </div>
                                                                                    <div class="tooltip-row">
                                                                                        <span class="tooltip-label">Room:</span>
                                                                                        <span class="tooltip-value"><%=course.getRoomNumber()%></span>
                                                                                    </div>
                                                                                    <div class="tooltip-row">
                                                                                        <span class="tooltip-label">Class:</span>
                                                                                        <span class="tooltip-value"><%=course.getCourseCode()%></span>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
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
        overflow: visible;
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
        cursor: pointer;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    
    .course-block:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        z-index: 100;
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
        font-size: 10px;
        font-weight: 600;
        margin-bottom: 3px;
        overflow: hidden;
        text-overflow: ellipsis;
        word-wrap: break-word;
        width: 100%;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    
    .course-room {
        font-size: 9px;
        opacity: 0.9;
        overflow: hidden;
        text-overflow: ellipsis;
        word-wrap: break-word;
        width: 100%;
    }
    
    /* Tooltip styles */
    .course-tooltip {
        display: none;
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        margin-top: 8px;
        background: white;
        color: #333;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.25);
        padding: 0;
        min-width: 280px;
        max-width: 320px;
        z-index: 1000;
        border: 2px solid #667eea;
        animation: fadeIn 0.2s ease-in-out;
    }
    
    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateX(-50%) translateY(-5px);
        }
        to {
            opacity: 1;
            transform: translateX(-50%) translateY(0);
        }
    }
    
    .course-block:hover .course-tooltip {
        display: block;
    }
    
    .tooltip-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 12px 15px;
        border-radius: 6px 6px 0 0;
        font-size: 14px;
        font-weight: 600;
        text-align: center;
    }
    
    .tooltip-content {
        padding: 15px;
    }
    
    .tooltip-row {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;
        font-size: 12px;
    }
    
    .tooltip-row:last-child {
        border-bottom: none;
    }
    
    .tooltip-label {
        font-weight: 600;
        color: #667eea;
        flex: 0 0 45%;
        text-align: left;
    }
    
    .tooltip-value {
        color: #333;
        flex: 0 0 55%;
        text-align: right;
        font-weight: 500;
    }
    
    /* Arrow for tooltip */
    .course-tooltip::before {
        content: '';
        position: absolute;
        top: -10px;
        left: 50%;
        transform: translateX(-50%);
        border-width: 0 10px 10px 10px;
        border-style: solid;
        border-color: transparent transparent #667eea transparent;
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
