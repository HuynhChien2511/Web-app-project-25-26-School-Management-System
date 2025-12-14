package com.school.controller;

import java.io.IOException;
import java.util.List;

import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.User;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StudentServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isStudent(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            showDashboard(request, response);
        } else if (pathInfo.equals("/courses")) {
            viewMyCourses(request, response);
        } else if (pathInfo.equals("/courses/available")) {
            viewAvailableCourses(request, response);
        } else if (pathInfo.equals("/courses/details")) {
            viewCourseDetails(request, response);
        } else if (pathInfo.equals("/timetable")) {
            viewTimetable(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isStudent(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo.equals("/courses/enroll")) {
            enrollInCourse(request, response);
        } else if (pathInfo.equals("/courses/drop")) {
            dropCourse(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = SessionValidator.getLoggedInUser(request);
        
        // Pagination support
        int page = 1;
        int pageSize = 5;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        List<Enrollment> myEnrollments = enrollmentDAO.getEnrollmentsByStudentWithPagination(student.getUserId(), page, pageSize);
        int totalEnrollments = enrollmentDAO.getTotalEnrollmentCountByStudent(student.getUserId());
        int totalPages = (int) Math.ceil((double) totalEnrollments / pageSize);
        
        long activeEnrollments = enrollmentDAO.getEnrollmentsByStudent(student.getUserId()).stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                .count();
        
        request.setAttribute("enrollments", myEnrollments);
        request.setAttribute("totalEnrollments", activeEnrollments);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        
        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }

    private void viewMyCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = SessionValidator.getLoggedInUser(request);
        
        // Pagination support
        int page = 1;
        int pageSize = 5;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudentWithPagination(student.getUserId(), page, pageSize);
        int totalEnrollments = enrollmentDAO.getTotalEnrollmentCountByStudent(student.getUserId());
        int totalPages = (int) Math.ceil((double) totalEnrollments / pageSize);
        
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/student/my-courses.jsp").forward(request, response);
    }

    private void viewAvailableCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = SessionValidator.getLoggedInUser(request);
        
        // Pagination support
        int page = 1;
        int pageSize = 10;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        List<Course> allCourses = courseDAO.getCoursesWithPagination(page, pageSize);
        int totalCourses = courseDAO.getTotalCourseCount();
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        
        // Mark courses the student is already enrolled in
        for (Course course : allCourses) {
            boolean isEnrolled = enrollmentDAO.isStudentEnrolled(student.getUserId(), course.getCourseId());
            course.setDescription(isEnrolled ? "ENROLLED" : course.getDescription());
        }
        
        request.setAttribute("courses", allCourses);
        request.setAttribute("studentId", student.getUserId());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/student/available-courses.jsp").forward(request, response);
    }

    private void viewCourseDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        User student = SessionValidator.getLoggedInUser(request);
        
        Course course = courseDAO.getCourseById(courseId);
        Enrollment enrollment = enrollmentDAO.getEnrollment(student.getUserId(), courseId);
        
        request.setAttribute("course", course);
        request.setAttribute("enrollment", enrollment);
        request.getRequestDispatcher("/WEB-INF/views/student/course-details.jsp").forward(request, response);
    }

    private void enrollInCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = SessionValidator.getLoggedInUser(request);
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        // Check if already enrolled
        if (enrollmentDAO.isStudentEnrolled(student.getUserId(), courseId)) {
            response.sendRedirect(request.getContextPath() + "/student/courses/available?error=already_enrolled");
            return;
        }
        
        // Check if course has available seats
        Course course = courseDAO.getCourseById(courseId);
        if (!course.hasAvailableSeats()) {
            response.sendRedirect(request.getContextPath() + "/student/courses/available?error=course_full");
            return;
        }
        
        // Check for schedule conflicts
        if (hasScheduleConflict(student.getUserId(), course)) {
            response.sendRedirect(request.getContextPath() + "/student/courses/available?error=schedule_conflict");
            return;
        }
        
        if (enrollmentDAO.enrollStudent(student.getUserId(), courseId)) {
            response.sendRedirect(request.getContextPath() + "/student/courses?success=enrolled");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/courses/available?error=enrollment_failed");
        }
    }
    
    private boolean hasScheduleConflict(int studentId, Course newCourse) {
        // Get student's current enrollments
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        
        // If new course has no schedule, no conflict
        if (newCourse.getScheduleDays() == null || newCourse.getScheduleTime() == null || 
            newCourse.getScheduleDays().isEmpty() || newCourse.getScheduleTime().isEmpty()) {
            return false;
        }
        
        // Parse new course periods
        int[] newPeriods = parsePeriods(newCourse.getScheduleTime());
        if (newPeriods == null) {
            return false; // Invalid format, allow enrollment
        }
        
        String[] newDays = parseScheduleDays(newCourse.getScheduleDays());
        
        // Check each existing enrollment for conflicts
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() != Enrollment.EnrollmentStatus.ACTIVE) {
                continue;
            }
            
            Course existingCourse = enrollment.getCourse();
            if (existingCourse == null || existingCourse.getScheduleDays() == null || 
                existingCourse.getScheduleTime() == null) {
                continue;
            }
            
            // Parse existing course periods
            int[] existingPeriods = parsePeriods(existingCourse.getScheduleTime());
            if (existingPeriods == null) {
                continue;
            }
            
            String[] existingDays = parseScheduleDays(existingCourse.getScheduleDays());
            
            // Check if courses share any days
            if (hasCommonDay(newDays, existingDays)) {
                // Check if periods overlap
                if (periodsOverlap(newPeriods, existingPeriods)) {
                    return true; // Conflict found
                }
            }
        }
        
        return false; // No conflicts
    }
    
    private int[] parsePeriods(String scheduleTime) {
        // Parse format like "P3-P4 (09:30-11:00)"
        try {
            if (scheduleTime.contains("P")) {
                int pStart = scheduleTime.indexOf("P");
                int dash = scheduleTime.indexOf("-", pStart);
                int pEnd = scheduleTime.indexOf("P", dash);
                int paren = scheduleTime.indexOf("(");
                
                if (pStart >= 0 && dash > 0 && pEnd > 0 && paren > 0) {
                    int start = Integer.parseInt(scheduleTime.substring(pStart + 1, dash).trim());
                    int end = Integer.parseInt(scheduleTime.substring(pEnd + 1, paren).trim());
                    return new int[]{start, end};
                }
            }
        } catch (Exception e) {
            // Invalid format
        }
        return null;
    }
    
    private String[] parseScheduleDays(String scheduleDays) {
        // Parse format like "Mon, Wed, Fri" or "Monday, Wednesday, Friday"
        if (scheduleDays == null || scheduleDays.isEmpty()) {
            return new String[0];
        }
        
        String[] days = scheduleDays.split(",");
        for (int i = 0; i < days.length; i++) {
            days[i] = days[i].trim();
        }
        return days;
    }
    
    private boolean hasCommonDay(String[] days1, String[] days2) {
        for (String day1 : days1) {
            for (String day2 : days2) {
                // Check both full name and abbreviation
                if (day1.equalsIgnoreCase(day2) || 
                    day1.toLowerCase().startsWith(day2.toLowerCase().substring(0, Math.min(3, day2.length()))) ||
                    day2.toLowerCase().startsWith(day1.toLowerCase().substring(0, Math.min(3, day1.length())))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean periodsOverlap(int[] periods1, int[] periods2) {
        int start1 = periods1[0];
        int end1 = periods1[1];
        int start2 = periods2[0];
        int end2 = periods2[1];
        
        // Check if ranges overlap
        return !(end1 < start2 || end2 < start1);
    }

    private void dropCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int enrollmentId = Integer.parseInt(request.getParameter("enrollmentId"));
        
        if (enrollmentDAO.dropEnrollment(enrollmentId)) {
            response.sendRedirect(request.getContextPath() + "/student/courses?success=dropped");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/courses?error=drop_failed");
        }
    }

    private void viewTimetable(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = SessionValidator.getLoggedInUser(request);
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(student.getUserId());
        
        request.setAttribute("enrollments", enrollments);
        request.getRequestDispatcher("/WEB-INF/views/student/timetable.jsp").forward(request, response);
    }
}
