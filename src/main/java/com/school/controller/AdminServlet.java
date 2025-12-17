package com.school.controller;

import java.io.IOException;
import java.util.List;

import com.school.dao.CourseDAO;
import com.school.dao.UserDAO;
import com.school.model.Course;
import com.school.model.User;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
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
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            showDashboard(request, response);
        } else if (pathInfo.equals("/users")) {
            listUsers(request, response);
        } else if (pathInfo.equals("/users/add")) {
            showAddUserForm(request, response);
        } else if (pathInfo.equals("/users/edit")) {
            showEditUserForm(request, response);
        } else if (pathInfo.equals("/courses")) {
            listCourses(request, response);
        } else if (pathInfo.equals("/courses/add")) {
            showAddCourseForm(request, response);
        } else if (pathInfo.equals("/courses/edit")) {
            showEditCourseForm(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
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
        
        if (pathInfo.equals("/users/add")) {
            addUser(request, response);
        } else if (pathInfo.equals("/users/update")) {
            updateUser(request, response);
        } else if (pathInfo.equals("/users/delete")) {
            deleteUser(request, response);
        } else if (pathInfo.equals("/courses/add")) {
            addCourse(request, response);
        } else if (pathInfo.equals("/courses/update")) {
            updateCourse(request, response);
        } else if (pathInfo.equals("/courses/delete")) {
            deleteCourse(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> allUsers = userDAO.getAllUsers();
        List<Course> allCourses = courseDAO.getAllCourses();
        
        long adminCount = allUsers.stream().filter(u -> u.getUserType() == User.UserType.ADMIN).count();
        long teacherCount = allUsers.stream().filter(u -> u.getUserType() == User.UserType.TEACHER).count();
        long studentCount = allUsers.stream().filter(u -> u.getUserType() == User.UserType.STUDENT).count();
        
        request.setAttribute("totalUsers", allUsers.size());
        request.setAttribute("adminCount", adminCount);
        request.setAttribute("teacherCount", teacherCount);
        request.setAttribute("studentCount", studentCount);
        request.setAttribute("totalCourses", allCourses.size());
        
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        
        List<User> users = userDAO.getUsersWithPagination(page, pageSize);
        int totalUsers = userDAO.getTotalUserCount();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        
        request.setAttribute("users", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
    }

    private void showAddUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Remove any existing user attribute from both request and session to ensure clean form
        request.removeAttribute("user");
        if (request.getSession(false) != null) {
            request.getSession().removeAttribute("user");
        }
        // Explicitly set user to null
        request.setAttribute("user", null);
        request.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(request, response);
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        User user = userDAO.getUserById(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(request, response);
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        user.setUserType(User.UserType.valueOf(request.getParameter("userType")));

        if (userDAO.addUser(user)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=added");
        } else {
            request.setAttribute("error", "Failed to add user");
            request.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = new User();
        user.setUserId(Integer.parseInt(request.getParameter("userId")));
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        user.setUserType(User.UserType.valueOf(request.getParameter("userType")));

        if (userDAO.updateUser(user)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=updated");
        } else {
            request.setAttribute("error", "Failed to update user");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/user-form.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        
        if (userDAO.deleteUser(userId)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_failed");
        }
    }

    private void listCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        
        List<Course> courses = courseDAO.getCoursesWithPagination(page, pageSize);
        int totalCourses = courseDAO.getTotalCourseCount();
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.getRequestDispatcher("/WEB-INF/views/admin/courses.jsp").forward(request, response);
    }

    private void showAddCourseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> teachers = userDAO.getUsersByType(User.UserType.TEACHER);
        request.setAttribute("teachers", teachers);
        request.getRequestDispatcher("/WEB-INF/views/admin/course-form.jsp").forward(request, response);
    }

    private void showEditCourseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("id"));
        Course course = courseDAO.getCourseById(courseId);
        List<User> teachers = userDAO.getUsersByType(User.UserType.TEACHER);
        
        request.setAttribute("course", course);
        request.setAttribute("teachers", teachers);
        request.getRequestDispatcher("/WEB-INF/views/admin/course-form.jsp").forward(request, response);
    }

    private void addCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Course course = new Course();
        course.setCourseCode(request.getParameter("courseCode"));
        course.setCourseName(request.getParameter("courseName"));
        course.setDescription(request.getParameter("description"));
        course.setCredits(Integer.parseInt(request.getParameter("credits")));
        
        String teacherIdStr = request.getParameter("teacherId");
        if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
            course.setTeacherId(Integer.parseInt(teacherIdStr));
        }
        
        course.setMaxStudents(Integer.parseInt(request.getParameter("maxStudents")));
        course.setScheduleDays(request.getParameter("scheduleDays"));
        
        // Convert period selection to time format
        String startPeriod = request.getParameter("startPeriod");
        String numPeriods = request.getParameter("numPeriods");
        String scheduleTime = convertPeriodsToTime(startPeriod, numPeriods);
        course.setScheduleTime(scheduleTime);
        
        course.setRoomNumber(request.getParameter("roomNumber"));

        if (courseDAO.addCourse(course)) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?success=added");
        } else {
            request.setAttribute("error", "Failed to add course");
            List<User> teachers = userDAO.getUsersByType(User.UserType.TEACHER);
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("/WEB-INF/views/admin/course-form.jsp").forward(request, response);
        }
    }

    private void updateCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Course course = new Course();
        course.setCourseId(Integer.parseInt(request.getParameter("courseId")));
        course.setCourseCode(request.getParameter("courseCode"));
        course.setCourseName(request.getParameter("courseName"));
        course.setDescription(request.getParameter("description"));
        course.setCredits(Integer.parseInt(request.getParameter("credits")));
        
        String teacherIdStr = request.getParameter("teacherId");
        if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
            course.setTeacherId(Integer.parseInt(teacherIdStr));
        }
        
        course.setMaxStudents(Integer.parseInt(request.getParameter("maxStudents")));
        course.setScheduleDays(request.getParameter("scheduleDays"));
        
        // Convert period selection to time format
        String startPeriod = request.getParameter("startPeriod");
        String numPeriods = request.getParameter("numPeriods");
        String scheduleTime = convertPeriodsToTime(startPeriod, numPeriods);
        course.setScheduleTime(scheduleTime);
        
        course.setRoomNumber(request.getParameter("roomNumber"));

        if (courseDAO.updateCourse(course)) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?success=updated");
        } else {
            request.setAttribute("error", "Failed to update course");
            request.setAttribute("course", course);
            List<User> teachers = userDAO.getUsersByType(User.UserType.TEACHER);
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("/WEB-INF/views/admin/course-form.jsp").forward(request, response);
        }
    }

    private void deleteCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("id"));
        
        if (courseDAO.deleteCourse(courseId)) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/courses?error=delete_failed");
        }
    }
    
    private String convertPeriodsToTime(String startPeriod, String numPeriods) {
        if (startPeriod == null || startPeriod.isEmpty() || numPeriods == null || numPeriods.isEmpty()) {
            return "";
        }
        
        String[] periodTimes = {
            "08:00", "08:45", "09:30", "10:15", "11:00", 
            "11:45", "13:00", "13:45", "14:30", "15:15", "16:00"
        };
        
        try {
            int start = Integer.parseInt(startPeriod);
            int duration = Integer.parseInt(numPeriods);
            
            if (start < 1 || start > 10 || duration < 1 || duration > 4) {
                return "";
            }
            
            int endPeriod = start + duration;
            String startTime = periodTimes[start - 1];
            String endTime = periodTimes[endPeriod - 1];
            
            return "P" + start + "-P" + (endPeriod - 1) + " (" + startTime + "-" + endTime + ")";
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
