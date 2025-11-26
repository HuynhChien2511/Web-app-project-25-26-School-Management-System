package com.school.controller;

import java.io.IOException;
import java.util.List;

import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.dao.UserDAO;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.User;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TeacherServlet extends HttpServlet {
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        courseDAO = new CourseDAO();
        enrollmentDAO = new EnrollmentDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isTeacher(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            showDashboard(request, response);
        } else if (pathInfo.equals("/courses")) {
            listMyCourses(request, response);
        } else if (pathInfo.equals("/courses/students")) {
            viewCourseStudents(request, response);
        } else if (pathInfo.equals("/students")) {
            listAllStudents(request, response);
        } else if (pathInfo.equals("/timetable")) {
            viewTimetable(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!SessionValidator.isTeacher(request)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo.equals("/grades/update")) {
            updateGrade(request, response);
        } else if (pathInfo.equals("/enrollments/drop")) {
            dropStudent(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User teacher = SessionValidator.getLoggedInUser(request);
        List<Course> myCourses = courseDAO.getCoursesByTeacher(teacher.getUserId());
        
        int totalStudents = 0;
        for (Course course : myCourses) {
            totalStudents += course.getEnrolledCount();
        }
        
        request.setAttribute("myCourses", myCourses);
        request.setAttribute("totalCourses", myCourses.size());
        request.setAttribute("totalStudents", totalStudents);
        
        request.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(request, response);
    }

    private void listMyCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User teacher = SessionValidator.getLoggedInUser(request);
        List<Course> myCourses = courseDAO.getCoursesByTeacher(teacher.getUserId());
        
        request.setAttribute("courses", myCourses);
        request.getRequestDispatcher("/WEB-INF/views/teacher/courses.jsp").forward(request, response);
    }

    private void viewCourseStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        // Verify the teacher owns this course
        User teacher = SessionValidator.getLoggedInUser(request);
        Course course = courseDAO.getCourseById(courseId);
        
        if (course == null || course.getTeacherId() != teacher.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/teacher/courses");
            return;
        }
        
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(courseId);
        
        request.setAttribute("course", course);
        request.setAttribute("enrollments", enrollments);
        request.getRequestDispatcher("/WEB-INF/views/teacher/course-students.jsp").forward(request, response);
    }

    private void listAllStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> students = userDAO.getUsersByType(User.UserType.STUDENT);
        
        request.setAttribute("students", students);
        request.getRequestDispatcher("/WEB-INF/views/teacher/students.jsp").forward(request, response);
    }

    private void updateGrade(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int enrollmentId = Integer.parseInt(request.getParameter("enrollmentId"));
        String grade = request.getParameter("grade");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        if (enrollmentDAO.updateEnrollmentGrade(enrollmentId, grade)) {
            response.sendRedirect(request.getContextPath() + "/teacher/courses/students?courseId=" + courseId + "&success=grade_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/courses/students?courseId=" + courseId + "&error=update_failed");
        }
    }

    private void dropStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int enrollmentId = Integer.parseInt(request.getParameter("enrollmentId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        if (enrollmentDAO.dropEnrollment(enrollmentId)) {
            response.sendRedirect(request.getContextPath() + "/teacher/courses/students?courseId=" + courseId + "&success=student_dropped");
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/courses/students?courseId=" + courseId + "&error=drop_failed");
        }
    }

    private void viewTimetable(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User teacher = SessionValidator.getLoggedInUser(request);
        List<Course> myCourses = courseDAO.getCoursesByTeacher(teacher.getUserId());
        
        request.setAttribute("courses", myCourses);
        request.getRequestDispatcher("/WEB-INF/views/teacher/timetable.jsp").forward(request, response);
    }
}
