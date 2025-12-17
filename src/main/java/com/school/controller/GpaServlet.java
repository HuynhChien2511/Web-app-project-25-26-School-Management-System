package com.school.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.school.dao.EnrollmentDAO;
import com.school.dao.GpaRecordDAO;
import com.school.dao.GradeComponentDAO;
import com.school.dao.GradeDAO;
import com.school.dao.SemesterDAO;
import com.school.dao.UserDAO;
import com.school.model.Enrollment;
import com.school.model.GpaRecord;
import com.school.model.Grade;
import com.school.model.GradeComponent;
import com.school.model.Semester;
import com.school.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GpaServlet extends HttpServlet {
    private GpaRecordDAO gpaRecordDAO;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;
    private GradeComponentDAO gradeComponentDAO;
    private SemesterDAO semesterDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        gpaRecordDAO = new GpaRecordDAO();
        enrollmentDAO = new EnrollmentDAO();
        gradeDAO = new GradeDAO();
        gradeComponentDAO = new GradeComponentDAO();
        semesterDAO = new SemesterDAO();
        userDAO = new UserDAO();
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
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            if (user.getUserType() == User.UserType.STUDENT) {
                showStudentGpaDashboard(request, response);
            } else {
                showStudentGpaDashboard(request, response); // Can view any student
            }
        } else if (pathInfo.equals("/admin/view")) {
            if (user.getUserType() != User.UserType.STUDENT) {
                showAdminGpaView(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else if (pathInfo.equals("/calculate")) {
            if (user.getUserType() != User.UserType.STUDENT) {
                calculateGpa(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
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
        
        if (pathInfo != null && pathInfo.equals("/recalculate")) {
            recalculateGpa(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showStudentGpaDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        int studentId;
        
        // Allow admin/teacher to view any student's GPA
        String studentIdParam = request.getParameter("studentId");
        if (studentIdParam != null && user.getUserType() != User.UserType.STUDENT) {
            studentId = Integer.parseInt(studentIdParam);
        } else {
            studentId = user.getUserId();
        }

        // Get all GPA records
        List<GpaRecord> gpaRecords = gpaRecordDAO.getGpaRecordsByStudent(studentId);
        
        // Get latest GPA record for summary
        GpaRecord latestGpa = gpaRecordDAO.getLatestGpaRecord(studentId);
        
        // Pagination for enrollments
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
        
        // Get enrollments with pagination
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudentWithPagination(studentId, page, pageSize);
        int totalEnrollments = enrollmentDAO.getTotalEnrollmentCountByStudent(studentId);
        int totalPages = (int) Math.ceil((double) totalEnrollments / pageSize);

        // Build grade/component maps for quick lookup in JSP
        Map<Integer, Grade> gradeMap = new HashMap<>();
        Map<Integer, GradeComponent> componentMap = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeDAO.getGradeByEnrollment(enrollment.getEnrollmentId());
            if (grade != null) {
                gradeMap.put(enrollment.getEnrollmentId(), grade);
            }
            // Only fetch component when semesterId is present
            if (enrollment.getCourseId() > 0 && enrollment.getSemesterId() > 0) {
                GradeComponent comp = gradeComponentDAO.getGradeComponent(enrollment.getCourseId(), enrollment.getSemesterId());
                if (comp != null) {
                    componentMap.put(enrollment.getEnrollmentId(), comp);
                    // If grade exists but total score is missing, calculate it now
                    if (grade != null && grade.getTotalScore() == null && comp != null) {
                        grade.calculateFinalGrade(comp);
                    }
                }
            }
        }
        
        // Get all semesters
        List<Semester> semesters = semesterDAO.getAllSemesters();

        request.setAttribute("studentId", studentId);
        request.setAttribute("gpaRecords", gpaRecords);
        request.setAttribute("latestGpa", latestGpa);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("semesters", semesters);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("gradeMap", gradeMap);
        request.setAttribute("componentMap", componentMap);
        
        if (user.getUserType() == User.UserType.STUDENT) {
            request.getRequestDispatcher("/WEB-INF/views/student/gpa-dashboard.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/admin/student-gpa.jsp").forward(request, response);
        }
    }

    private void calculateGpa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        
        if (gpaRecordDAO.calculateAndSaveGpa(studentId, semesterId)) {
            request.getSession().setAttribute("message", "GPA calculated successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to calculate GPA.");
        }
        
        response.sendRedirect(request.getContextPath() + "/gpa/dashboard?studentId=" + studentId);
    }

    private void recalculateGpa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        
        // Recalculate GPA for all semesters
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        int recalculatedCount = 0;
        
        for (Enrollment enrollment : enrollments) {
            if (gpaRecordDAO.calculateAndSaveGpa(studentId, enrollment.getSemesterId())) {
                recalculatedCount++;
            }
        }

        request.getSession().setAttribute("message", 
            "GPA recalculated for " + recalculatedCount + " semester(s)!");
        response.sendRedirect(request.getContextPath() + "/gpa/admin/view?studentId=" + studentId);
    }

    private void showAdminGpaView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String studentIdParam = request.getParameter("studentId");
        
        // If no student ID provided, show search form only
        if (studentIdParam == null || studentIdParam.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/views/admin/student-gpa.jsp").forward(request, response);
            return;
        }
        
        int studentId = Integer.parseInt(studentIdParam);
        
        // Get student information
        User student = userDAO.getUserById(studentId);
        
        // Get all GPA records
        List<GpaRecord> gpaRecords = gpaRecordDAO.getGpaRecordsByStudent(studentId);
        
        // Get latest GPA record for summary
        GpaRecord latestGpa = gpaRecordDAO.getLatestGpaRecord(studentId);
        
        // Get all enrollments with grades
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        
        // Get all semesters
        List<Semester> semesters = semesterDAO.getAllSemesters();

        request.setAttribute("student", student);
        request.setAttribute("studentId", studentId);
        request.setAttribute("gpaRecords", gpaRecords);
        request.setAttribute("latestGpa", latestGpa);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("semesters", semesters);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/student-gpa.jsp").forward(request, response);
    }
}
