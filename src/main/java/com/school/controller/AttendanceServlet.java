package com.school.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.school.dao.AttendanceDAO;
import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.model.Attendance;
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
        List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getUserId());
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/teacher/attendance-courses.jsp").forward(request, response);
    }

    private void showAllCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Course> courses = courseDAO.getAllCourses();
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/admin/attendance-courses.jsp").forward(request, response);
    }

    private void showAttendanceForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String dateStr = request.getParameter("date");
        LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? 
                         LocalDate.parse(dateStr) : LocalDate.now();

        Course course = courseDAO.getCourseById(courseId);
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
        int enrollmentId = Integer.parseInt(request.getParameter("enrollmentId"));
        
        Enrollment enrollment = enrollmentDAO.getEnrollmentsByCourse(0).stream()
                .filter(e -> e.getEnrollmentId() == enrollmentId)
                .findFirst().orElse(null);
        
        List<Attendance> attendances = attendanceDAO.getAttendanceByEnrollment(enrollmentId);
        int absentCount = attendanceDAO.getAbsentCount(enrollmentId);
        int presentCount = attendanceDAO.getPresentCount(enrollmentId);
        double attendanceRate = attendanceDAO.getAttendanceRate(enrollmentId);

        request.setAttribute("enrollment", enrollment);
        request.setAttribute("attendances", attendances);
        request.setAttribute("absentCount", absentCount);
        request.setAttribute("presentCount", presentCount);
        request.setAttribute("attendanceRate", attendanceRate);
        request.getRequestDispatcher("/WEB-INF/views/teacher/view-attendance.jsp").forward(request, response);
    }

    private void saveAttendance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        LocalDate date = LocalDate.parse(request.getParameter("date"));
        
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
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/attendance/");
        }
    }
}
