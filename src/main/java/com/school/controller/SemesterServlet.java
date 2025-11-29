package com.school.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.school.dao.CourseDAO;
import com.school.dao.GradeComponentDAO;
import com.school.dao.SemesterDAO;
import com.school.model.Course;
import com.school.model.GradeComponent;
import com.school.model.Semester;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SemesterServlet extends HttpServlet {
    private SemesterDAO semesterDAO;
    private GradeComponentDAO gradeComponentDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        semesterDAO = new SemesterDAO();
        gradeComponentDAO = new GradeComponentDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            listSemesters(request, response);
        } else if (pathInfo.equals("/add")) {
            showAddSemesterForm(request, response);
        } else if (pathInfo.equals("/edit")) {
            showEditSemesterForm(request, response);
        } else if (pathInfo.equals("/components")) {
            showGradeComponents(request, response);
        } else if (pathInfo.equals("/components/edit")) {
            showEditComponentForm(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/create":
                createSemester(request, response);
                break;
            case "/update":
                updateSemester(request, response);
                break;
            case "/activate":
                activateSemester(request, response);
                break;
            case "/delete":
                deleteSemester(request, response);
                break;
            case "/components/update":
                updateGradeComponent(request, response);
                break;
            case "/components/create":
                createGradeComponents(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listSemesters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Semester> semesters = semesterDAO.getAllSemesters();
        request.setAttribute("semesters", semesters);
        request.getRequestDispatcher("/WEB-INF/views/admin/semesters.jsp").forward(request, response);
    }

    private void showAddSemesterForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/semester-form.jsp").forward(request, response);
    }

    private void showEditSemesterForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("id"));
        Semester semester = semesterDAO.getSemesterById(semesterId);
        request.setAttribute("semester", semester);
        request.getRequestDispatcher("/WEB-INF/views/admin/semester-form.jsp").forward(request, response);
    }

    private void showGradeComponents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        Semester semester = semesterDAO.getSemesterById(semesterId);
        List<GradeComponent> components = gradeComponentDAO.getGradeComponentsBySemester(semesterId);
        List<Course> allCourses = courseDAO.getAllCourses();
        
        request.setAttribute("semester", semester);
        request.setAttribute("components", components);
        request.setAttribute("courses", allCourses);
        request.getRequestDispatcher("/WEB-INF/views/admin/grade-components.jsp").forward(request, response);
    }

    private void showEditComponentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int componentId = Integer.parseInt(request.getParameter("id"));
        GradeComponent component = gradeComponentDAO.getGradeComponentById(componentId);
        request.setAttribute("component", component);
        request.getRequestDispatcher("/WEB-INF/views/admin/grade-component-form.jsp").forward(request, response);
    }

    private void createSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String semesterName = request.getParameter("semesterName");
        String semesterType = request.getParameter("semesterType");
        String academicYear = request.getParameter("academicYear");
        LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
        LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
        int weeks = Integer.parseInt(request.getParameter("weeks"));
        boolean isActive = request.getParameter("isActive") != null;

        Semester semester = new Semester();
        semester.setSemesterName(semesterName);
        semester.setSemesterType(Semester.SemesterType.valueOf(semesterType));
        semester.setAcademicYear(academicYear);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setWeeks(weeks);
        semester.setActive(isActive);

        if (semesterDAO.createSemester(semester)) {
            request.getSession().setAttribute("message", "Semester created successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to create semester.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/list");
    }

    private void updateSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        String semesterName = request.getParameter("semesterName");
        String semesterType = request.getParameter("semesterType");
        String academicYear = request.getParameter("academicYear");
        LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
        LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
        int weeks = Integer.parseInt(request.getParameter("weeks"));
        boolean isActive = request.getParameter("isActive") != null;

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterName(semesterName);
        semester.setSemesterType(Semester.SemesterType.valueOf(semesterType));
        semester.setAcademicYear(academicYear);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setWeeks(weeks);
        semester.setActive(isActive);

        if (semesterDAO.updateSemester(semester)) {
            request.getSession().setAttribute("message", "Semester updated successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to update semester.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/list");
    }

    private void activateSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        
        if (semesterDAO.setActiveSemester(semesterId)) {
            request.getSession().setAttribute("message", "Semester activated successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to activate semester.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/list");
    }

    private void deleteSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        
        if (semesterDAO.deleteSemester(semesterId)) {
            request.getSession().setAttribute("message", "Semester deleted successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to delete semester. It may have associated data.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/list");
    }

    private void createGradeComponents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        BigDecimal inclassPercentage = new BigDecimal(request.getParameter("inclassPercentage"));
        BigDecimal midtermPercentage = new BigDecimal(request.getParameter("midtermPercentage"));
        BigDecimal finalPercentage = new BigDecimal(request.getParameter("finalPercentage"));

        GradeComponent component = new GradeComponent();
        component.setCourseId(courseId);
        component.setSemesterId(semesterId);
        component.setInclassPercentage(inclassPercentage);
        component.setMidtermPercentage(midtermPercentage);
        component.setFinalPercentage(finalPercentage);

        if (!component.isValidPercentages()) {
            request.getSession().setAttribute("error", "Percentages must sum to 100!");
            response.sendRedirect(request.getContextPath() + "/admin/semesters/components?semesterId=" + semesterId);
            return;
        }

        if (gradeComponentDAO.createGradeComponent(component)) {
            request.getSession().setAttribute("message", "Grade component created successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to create grade component.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/components?semesterId=" + semesterId);
    }

    private void updateGradeComponent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int componentId = Integer.parseInt(request.getParameter("componentId"));
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));
        BigDecimal inclassPercentage = new BigDecimal(request.getParameter("inclassPercentage"));
        BigDecimal midtermPercentage = new BigDecimal(request.getParameter("midtermPercentage"));
        BigDecimal finalPercentage = new BigDecimal(request.getParameter("finalPercentage"));

        GradeComponent component = gradeComponentDAO.getGradeComponentById(componentId);
        if (component == null) {
            request.getSession().setAttribute("error", "Grade component not found.");
            response.sendRedirect(request.getContextPath() + "/admin/semesters/components?semesterId=" + semesterId);
            return;
        }

        component.setInclassPercentage(inclassPercentage);
        component.setMidtermPercentage(midtermPercentage);
        component.setFinalPercentage(finalPercentage);

        if (!component.isValidPercentages()) {
            request.getSession().setAttribute("error", "Percentages must sum to 100!");
            response.sendRedirect(request.getContextPath() + "/admin/semesters/components?semesterId=" + semesterId);
            return;
        }

        if (gradeComponentDAO.updateGradeComponent(component)) {
            request.getSession().setAttribute("message", "Grade component updated successfully!");
        } else {
            request.getSession().setAttribute("error", "Failed to update grade component.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/semesters/components?semesterId=" + semesterId);
    }
}
