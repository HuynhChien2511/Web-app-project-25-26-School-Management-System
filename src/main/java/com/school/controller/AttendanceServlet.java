package com.school.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.school.dao.AttendanceDAO;
import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.model.Attendance;
import com.school.model.AttendanceSummary;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AttendanceServlet extends HttpServlet {
    private AttendanceDAO attendanceDAO;
    private EnrollmentDAO enrollmentDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        attendanceDAO = new AttendanceDAO();
        enrollmentDAO = new EnrollmentDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserType() == User.UserType.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            if (user.getUserType() == User.UserType.TEACHER) {
                showTeacherCourses(request, response);
            } else {
                showAllCourses(request, response);
            }
        } else if (pathInfo.equals("/take")) {
            showAttendanceForm(request, response);
        } else if (pathInfo.equals("/view")) {
            viewAttendance(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserType() == User.UserType.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/save":
                saveAttendance(request, response);
                break;
            case "/update":
                updateAttendanceRecord(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showTeacherCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User teacher = (User) request.getSession().getAttribute("user");
        List<Course> allCourses = courseDAO.getCoursesByTeacher(teacher.getUserId());
        List<Course> coursesNow = filterCoursesHappeningNow(allCourses);
        request.setAttribute("courses", coursesNow);
        request.setAttribute("timeFiltered", true);
        request.getRequestDispatcher("/WEB-INF/views/teacher/attendance-courses.jsp").forward(request, response);
    }

    private void showAllCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pagination parameters
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int pageSize = 9;
        List<Course> courses = courseDAO.getCoursesWithPagination(page, pageSize);
        int totalCourses = courseDAO.getTotalCourseCount();
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/admin/attendance-courses.jsp").forward(request, response);
    }

    private void showAttendanceForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String dateStr = request.getParameter("date");
        LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? 
                         LocalDate.parse(dateStr) : LocalDate.now();

        Course course = courseDAO.getCourseById(courseId);
        // Ownership check for teachers
        User current = (User) request.getSession().getAttribute("user");
        if (current != null && current.getUserType() == User.UserType.TEACHER) {
            if (course == null || course.getTeacherId() != current.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/attendance");
                return;
            }
        }
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        // Get existing attendance for this date
        Map<Integer, Attendance> attendanceMap = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            Attendance attendance = attendanceDAO.getAttendance(enrollment.getEnrollmentId(), date);
            if (attendance != null) {
                attendanceMap.put(enrollment.getEnrollmentId(), attendance);
            }
        }

        request.setAttribute("course", course);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("attendanceMap", attendanceMap);
        request.setAttribute("attendanceDate", date);
        request.getRequestDispatcher("/WEB-INF/views/teacher/take-attendance.jsp").forward(request, response);
    }

    private void viewAttendance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        Course course = courseDAO.getCourseById(courseId);

        // Ownership check for teachers
        User current = (User) request.getSession().getAttribute("user");
        if (current != null && current.getUserType() == User.UserType.TEACHER) {
            if (course == null || course.getTeacherId() != current.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/attendance");
                return;
            }
        }

        List<AttendanceSummary> summaries = attendanceDAO.getAttendanceSummaryByCourse(courseId);
        request.setAttribute("course", course);
        request.setAttribute("attendanceRecords", summaries);
        request.getRequestDispatcher("/WEB-INF/views/teacher/view-attendance.jsp").forward(request, response);
    }

    private void saveAttendance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        LocalDate date = LocalDate.parse(request.getParameter("date"));
        
        // Ownership check for teachers
        User current = (User) request.getSession().getAttribute("user");
        if (current != null && current.getUserType() == User.UserType.TEACHER) {
            Course course = courseDAO.getCourseById(courseId);
            if (course == null || course.getTeacherId() != current.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/attendance");
                return;
            }
        }

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        int savedCount = 0;
        for (Enrollment enrollment : enrollments) {
            String statusParam = request.getParameter("status_" + enrollment.getEnrollmentId());
            String notesParam = request.getParameter("notes_" + enrollment.getEnrollmentId());
            
            if (statusParam != null) {
                Attendance attendance = new Attendance();
                attendance.setEnrollmentId(enrollment.getEnrollmentId());
                attendance.setAttendanceDate(date);
                attendance.setStatus(Attendance.AttendanceStatus.valueOf(statusParam));
                attendance.setNotes(notesParam != null ? notesParam : "");
                
                if (attendanceDAO.recordAttendance(attendance)) {
                    savedCount++;
                    
                    // Check for auto-drop (>3 absences)
                    int absentCount = attendanceDAO.getAbsentCount(enrollment.getEnrollmentId());
                    if (absentCount > 3 && enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE) {
                        enrollmentDAO.updateEnrollmentStatus(
                            enrollment.getEnrollmentId(), 
                            Enrollment.EnrollmentStatus.DROPPED
                        );
                    }
                }
            }
        }

        request.getSession().setAttribute("message", 
            "Attendance saved for " + savedCount + " student(s)!");
        response.sendRedirect(request.getContextPath() + "/attendance/take?courseId=" + courseId + "&date=" + date);
    }

    private void updateAttendanceRecord(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int attendanceId = Integer.parseInt(request.getParameter("attendanceId"));
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");
        
        if (attendanceDAO.updateAttendance(attendanceId, Attendance.AttendanceStatus.valueOf(status), notes)) {
            request.getSession().setAttribute("message", "Attendance updated successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to update attendance.");
        }
        
        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && !redirectUrl.isEmpty() && redirectUrl.startsWith(request.getContextPath())) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/attendance/");
        }
    }

    private List<Course> filterCoursesHappeningNow(List<Course> courses) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();
        String dayToken = mapDayToToken(currentDay);
        return courses.stream()
                .filter(c -> isCourseRunningNow(c, dayToken, currentTime))
                .toList();
    }

    private boolean isCourseRunningNow(Course course, String dayToken, LocalTime currentTime) {
        if (course == null) return false;

        String days = course.getScheduleDays();
        if (days == null || !containsDay(days, dayToken)) {
            return false;
        }

        String schedule = course.getScheduleTime();
        if (schedule == null) return false;

        Matcher m = Pattern.compile(".*\\((\\d{2}:\\d{2})-(\\d{2}:\\d{2})\\).*").matcher(schedule);
        if (!m.matches()) return false;

        LocalTime start = LocalTime.parse(m.group(1));
        LocalTime end = LocalTime.parse(m.group(2));
        return !currentTime.isBefore(start) && !currentTime.isAfter(end);
    }

    private boolean containsDay(String scheduleDays, String dayToken) {
        String[] parts = scheduleDays.split(",");
        for (String part : parts) {
            if (part.trim().equalsIgnoreCase(dayToken)) {
                return true;
            }
        }
        return false;
    }

    private String mapDayToToken(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
            case SUNDAY -> "Sun";
        };
    }
}
