package com.school.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.school.dao.AttendanceDAO;
import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.dao.GpaRecordDAO;
import com.school.dao.GradeComponentDAO;
import com.school.dao.GradeDAO;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.Grade;
import com.school.model.GradeComponent;
import com.school.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GradeServlet extends HttpServlet {
    private GradeDAO gradeDAO;
    private GradeComponentDAO gradeComponentDAO;
    private EnrollmentDAO enrollmentDAO;
    private CourseDAO courseDAO;
    private AttendanceDAO attendanceDAO;
    private GpaRecordDAO gpaRecordDAO;

    @Override
    public void init() throws ServletException {
        gradeDAO = new GradeDAO();
        gradeComponentDAO = new GradeComponentDAO();
        enrollmentDAO = new EnrollmentDAO();
        courseDAO = new CourseDAO();
        attendanceDAO = new AttendanceDAO();
        gpaRecordDAO = new GpaRecordDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            if (user.getUserType() == User.UserType.TEACHER) {
                showTeacherCourses(request, response);
            } else if (user.getUserType() == User.UserType.STUDENT) {
                showStudentGrades(request, response);
            } else {
                showAllCourses(request, response);
            }
        } else if (pathInfo.equals("/enter")) {
            showGradeEntryForm(request, response);
        } else if (pathInfo.equals("/calculate-inclass")) {
            calculateInclassScores(request, response);
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
                saveGrades(request, response);
                break;
            case "/finalize":
                finalizeGrades(request, response);
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
        request.getRequestDispatcher("/WEB-INF/views/teacher/grade-courses.jsp").forward(request, response);
    }

    private void showAllCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Course> courses = courseDAO.getAllCourses();
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/admin/grade-courses.jsp").forward(request, response);
    }

    private void showGradeEntryForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        Course course = courseDAO.getCourseById(courseId);
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        // Get grade component for this course
        GradeComponent component = gradeComponentDAO.getGradeComponent(
            courseId, 
            enrollments.isEmpty() ? 0 : enrollments.get(0).getSemesterId()
        );
        
        // Get existing grades
        Map<Integer, Grade> gradeMap = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeDAO.getGradeByEnrollment(enrollment.getEnrollmentId());
            if (grade != null) {
                gradeMap.put(enrollment.getEnrollmentId(), grade);
            }
            
            // Calculate attendance rate for in-class score reference
            double attendanceRate = attendanceDAO.getAttendanceRate(enrollment.getEnrollmentId());
            enrollment.setCourse(course); // Store attendance rate temporarily
        }

        request.setAttribute("course", course);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("gradeMap", gradeMap);
        request.setAttribute("component", component);
        request.getRequestDispatcher("/WEB-INF/views/teacher/enter-grades.jsp").forward(request, response);
    }

    private void calculateInclassScores(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        int updatedCount = 0;
        for (Enrollment enrollment : enrollments) {
            // Calculate in-class score based on attendance rate
            double attendanceRate = attendanceDAO.getAttendanceRate(enrollment.getEnrollmentId());
            BigDecimal inclassScore = new BigDecimal(attendanceRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            
            if (gradeDAO.updateInclassScore(enrollment.getEnrollmentId(), inclassScore)) {
                updatedCount++;
            }
        }

        request.getSession().setAttribute("message", 
            "In-class scores calculated for " + updatedCount + " student(s) based on attendance!");
        response.sendRedirect(request.getContextPath() + "/grades/enter?courseId=" + courseId);
    }

    private void showStudentGrades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = (User) request.getSession().getAttribute("user");
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(student.getUserId());
        
        Map<Integer, Grade> gradeMap = new HashMap<>();
        Map<Integer, GradeComponent> componentMap = new HashMap<>();
        
        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeDAO.getGradeByEnrollment(enrollment.getEnrollmentId());
            if (grade != null) {
                gradeMap.put(enrollment.getEnrollmentId(), grade);
                
                GradeComponent component = gradeComponentDAO.getGradeComponent(
                    enrollment.getCourseId(), 
                    enrollment.getSemesterId()
                );
                if (component != null) {
                    componentMap.put(enrollment.getEnrollmentId(), component);
                }
            }
        }

        request.setAttribute("enrollments", enrollments);
        request.setAttribute("gradeMap", gradeMap);
        request.setAttribute("componentMap", componentMap);
        request.getRequestDispatcher("/WEB-INF/views/student/grades.jsp").forward(request, response);
    }

    private void saveGrades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        int savedCount = 0;
        for (Enrollment enrollment : enrollments) {
            String inclassStr = request.getParameter("inclass_" + enrollment.getEnrollmentId());
            String midtermStr = request.getParameter("midterm_" + enrollment.getEnrollmentId());
            String finalStr = request.getParameter("final_" + enrollment.getEnrollmentId());
            
            if (inclassStr != null && !inclassStr.isEmpty()) {
                gradeDAO.updateInclassScore(enrollment.getEnrollmentId(), new BigDecimal(inclassStr));
                savedCount++;
            }
            if (midtermStr != null && !midtermStr.isEmpty()) {
                gradeDAO.updateMidtermScore(enrollment.getEnrollmentId(), new BigDecimal(midtermStr));
                savedCount++;
            }
            if (finalStr != null && !finalStr.isEmpty()) {
                gradeDAO.updateFinalScore(enrollment.getEnrollmentId(), new BigDecimal(finalStr));
                savedCount++;
            }
        }

        request.getSession().setAttribute("message", "Grades saved successfully!");
        response.sendRedirect(request.getContextPath() + "/grades/enter?courseId=" + courseId);
    }

    private void finalizeGrades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        if (enrollments.isEmpty()) {
            request.getSession().setAttribute("error", "No enrollments found for this course.");
            response.sendRedirect(request.getContextPath() + "/grades/");
            return;
        }
        
        GradeComponent component = gradeComponentDAO.getGradeComponent(
            courseId, 
            enrollments.get(0).getSemesterId()
        );
        
        if (component == null) {
            request.getSession().setAttribute("error", "Grade components not configured for this course.");
            response.sendRedirect(request.getContextPath() + "/grades/enter?courseId=" + courseId);
            return;
        }

        int finalizedCount = 0;
        for (Enrollment enrollment : enrollments) {
            if (gradeDAO.recalculateGrade(enrollment.getEnrollmentId(), component)) {
                finalizedCount++;
                
                // Update enrollment grade field with letter grade
                Grade grade = gradeDAO.getGradeByEnrollment(enrollment.getEnrollmentId());
                if (grade != null && grade.getLetterGrade() != null) {
                    enrollmentDAO.updateEnrollmentGrade(enrollment.getEnrollmentId(), grade.getLetterGrade());
                    
                    // Recalculate GPA for the student
                    gpaRecordDAO.calculateAndSaveGpa(enrollment.getStudentId(), enrollment.getSemesterId());
                }
            }
        }

        request.getSession().setAttribute("message", 
            "Grades finalized for " + finalizedCount + " student(s). GPA records updated!");
        response.sendRedirect(request.getContextPath() + "/grades/enter?courseId=" + courseId);
    }
}
