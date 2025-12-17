package com.school.controller;

import java.io.IOException;
import java.util.List;

import com.school.dao.AnnouncementDAO;
import com.school.dao.CourseDAO;
import com.school.model.Announcement;
import com.school.model.Course;
import com.school.model.User;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AnnouncementServlet extends HttpServlet {
    private AnnouncementDAO announcementDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws ServletException {
        announcementDAO = new AnnouncementDAO();
        courseDAO = new CourseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = SessionValidator.getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            listAnnouncements(request, response, user);
        } else if (pathInfo.equals("/create")) {
            showCreateForm(request, response, user);
        } else if (pathInfo.equals("/delete")) {
            deleteAnnouncement(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/announcements/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = SessionValidator.getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo.equals("/create")) {
            createAnnouncement(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/announcements/list");
        }
    }

    private void listAnnouncements(HttpServletRequest request, HttpServletResponse response, User user)
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
        
        List<Announcement> announcements;
        int totalAnnouncements;
        
        if (user.getUserType() == User.UserType.ADMIN) {
            announcements = announcementDAO.getAllAnnouncementsWithPagination(page, pageSize);
            totalAnnouncements = announcementDAO.getTotalAnnouncementCount();
        } else if (user.getUserType() == User.UserType.TEACHER) {
            // For teachers, get school-wide + their own announcements with pagination
            List<Announcement> allTeacherAnnouncements = announcementDAO.getSchoolWideAnnouncements();
            allTeacherAnnouncements.addAll(announcementDAO.getAnnouncementsByTeacher(user.getUserId()));
            totalAnnouncements = allTeacherAnnouncements.size();
            
            // Manual pagination for teacher announcements
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalAnnouncements);
            announcements = totalAnnouncements > 0 ? allTeacherAnnouncements.subList(startIndex, endIndex) : allTeacherAnnouncements;
        } else {
            // Students see school-wide announcements + announcements from enrolled courses
            announcements = announcementDAO.getAnnouncementsForStudentWithPagination(user.getUserId(), page, pageSize);
            totalAnnouncements = announcementDAO.getTotalAnnouncementCountForStudent(user.getUserId());
        }
        
        int totalPages = (int) Math.ceil((double) totalAnnouncements / pageSize);
        
        request.setAttribute("announcements", announcements);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        
        String viewPath;
        if (user.getUserType() == User.UserType.ADMIN) {
            viewPath = "/WEB-INF/views/admin/announcements.jsp";
        } else if (user.getUserType() == User.UserType.TEACHER) {
            viewPath = "/WEB-INF/views/teacher/announcements.jsp";
        } else {
            viewPath = "/WEB-INF/views/student/announcements.jsp";
        }
        
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        // Only admins and teachers can create announcements
        if (user.getUserType() != User.UserType.ADMIN && user.getUserType() != User.UserType.TEACHER) {
            response.sendRedirect(request.getContextPath() + "/announcements/list");
            return;
        }
        
        // Get courses for dropdown (for teacher: their courses only, for admin: all courses)
        List<Course> courses;
        if (user.getUserType() == User.UserType.ADMIN) {
            courses = courseDAO.getAllCourses();
        } else {
            courses = courseDAO.getCoursesByTeacher(user.getUserId());
        }
        
        request.setAttribute("courses", courses);
        
        String viewPath = user.getUserType() == User.UserType.ADMIN 
            ? "/WEB-INF/views/admin/announcement-form.jsp"
            : "/WEB-INF/views/teacher/announcement-form.jsp";
        
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void createAnnouncement(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        // Only admins and teachers can create announcements
        if (user.getUserType() != User.UserType.ADMIN && user.getUserType() != User.UserType.TEACHER) {
            response.sendRedirect(request.getContextPath() + "/announcements/list");
            return;
        }
        
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String courseIdStr = request.getParameter("courseId");
        boolean isImportant = request.getParameter("isImportant") != null;
        
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setAuthorId(user.getUserId());
        announcement.setImportant(isImportant);
        
        // Handle course-specific vs school-wide announcement
        if (courseIdStr != null && !courseIdStr.isEmpty() && !courseIdStr.equals("0")) {
            int courseId = Integer.parseInt(courseIdStr);
            
            // Verify teacher owns this course (admins can post to any course)
            if (user.getUserType() == User.UserType.TEACHER) {
                Course course = courseDAO.getCourseById(courseId);
                if (course == null || course.getTeacherId() != user.getUserId()) {
                    response.sendRedirect(request.getContextPath() + "/announcements/list?error=unauthorized");
                    return;
                }
            }
            
            announcement.setCourseId(courseId);
        } else {
            // School-wide announcement (courseId is null)
            // Only admins can create school-wide announcements
            if (user.getUserType() == User.UserType.TEACHER) {
                response.sendRedirect(request.getContextPath() + "/announcements/list?error=unauthorized");
                return;
            }
        }
        
        if (announcementDAO.createAnnouncement(announcement)) {
            response.sendRedirect(request.getContextPath() + "/announcements?success=created");
        } else {
            response.sendRedirect(request.getContextPath() + "/announcements/create?error=failed");
        }
    }

    private void deleteAnnouncement(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        int announcementId = Integer.parseInt(request.getParameter("id"));
        Announcement announcement = announcementDAO.getAnnouncementById(announcementId);
        
        if (announcement == null) {
            response.sendRedirect(request.getContextPath() + "/announcements/list?error=notfound");
            return;
        }
        
        // Check permissions with enhanced security
        boolean canDelete = false;
        
        if (user.getUserType() == User.UserType.ADMIN) {
            // Admin can delete any announcement
            canDelete = true;
        } else if ("ADMIN".equals(announcement.getAuthorType())) {
            // Only admin can delete admin's announcements
            // Teachers and students cannot delete admin announcements
            canDelete = false;
        } else if (announcement.getAuthorId() == user.getUserId()) {
            // Users can delete their own announcements (non-admin only)
            canDelete = true;
        }
        
        if (!canDelete) {
            response.sendRedirect(request.getContextPath() + "/announcements/list?error=unauthorized");
            return;
        }
        
        if (announcementDAO.deleteAnnouncement(announcementId)) {
            response.sendRedirect(request.getContextPath() + "/announcements?success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/announcements?error=deletefailed");
        }
    }
}
