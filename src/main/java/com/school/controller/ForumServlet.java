package com.school.controller;

import java.io.IOException;
import java.util.List;

import com.school.dao.CourseDAO;
import com.school.dao.EnrollmentDAO;
import com.school.dao.ForumDAO;
import com.school.model.Course;
import com.school.model.Enrollment;
import com.school.model.ForumPost;
import com.school.model.User;
import com.school.util.SessionValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ForumServlet extends HttpServlet {
    private ForumDAO forumDAO;
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() throws ServletException {
        forumDAO = new ForumDAO();
        courseDAO = new CourseDAO();
        enrollmentDAO = new EnrollmentDAO();
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
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/student/courses");
            return;
        } else if (pathInfo.equals("/course")) {
            viewCourseForum(request, response, user);
        } else if (pathInfo.equals("/post")) {
            viewPost(request, response, user);
        } else if (pathInfo.equals("/create")) {
            showCreatePostForm(request, response, user);
        } else if (pathInfo.equals("/delete")) {
            deletePost(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/courses");
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
            createPost(request, response, user);
        } else if (pathInfo.equals("/reply")) {
            createReply(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/courses");
        }
    }

    private void viewCourseForum(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        // Verify user has access to this course
        if (!hasAccessToCourse(user, courseId)) {
            response.sendRedirect(request.getContextPath() + "/error?message=unauthorized");
            return;
        }
        
        Course course = courseDAO.getCourseById(courseId);
        List<ForumPost> posts = forumDAO.getTopLevelPostsByCourse(courseId);
        
        request.setAttribute("course", course);
        request.setAttribute("posts", posts);
        
        String viewPath = getViewPath("forum");
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void viewPost(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        ForumPost post = forumDAO.getPostById(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/error?message=notfound");
            return;
        }
        
        // Verify user has access to this course
        if (!hasAccessToCourse(user, post.getCourseId())) {
            response.sendRedirect(request.getContextPath() + "/error?message=unauthorized");
            return;
        }
        
        List<ForumPost> replies = forumDAO.getRepliesByPostId(postId);
        Course course = courseDAO.getCourseById(post.getCourseId());
        
        request.setAttribute("post", post);
        request.setAttribute("replies", replies);
        request.setAttribute("course", course);
        
        String viewPath = getViewPath("forum-post");
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void showCreatePostForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        // Verify user has access to this course
        if (!hasAccessToCourse(user, courseId)) {
            response.sendRedirect(request.getContextPath() + "/error?message=unauthorized");
            return;
        }
        
        Course course = courseDAO.getCourseById(courseId);
        request.setAttribute("course", course);
        
        String viewPath = getViewPath("forum-create-post");
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void createPost(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        // Verify user has access to this course
        if (!hasAccessToCourse(user, courseId)) {
            response.sendRedirect(request.getContextPath() + "/error?message=unauthorized");
            return;
        }
        
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        ForumPost post = new ForumPost();
        post.setCourseId(courseId);
        post.setAuthorId(user.getUserId());
        post.setTitle(title);
        post.setContent(content);
        
        if (forumDAO.createTopLevelPost(post)) {
            response.sendRedirect(request.getContextPath() + "/forum/course?courseId=" + courseId + "&success=created");
        } else {
            response.sendRedirect(request.getContextPath() + "/forum/create?courseId=" + courseId + "&error=failed");
        }
    }

    private void createReply(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        String content = request.getParameter("content");
        
        ForumPost parentPost = forumDAO.getPostById(postId);
        if (parentPost == null) {
            response.sendRedirect(request.getContextPath() + "/error?message=notfound");
            return;
        }
        
        // Verify user has access to this course
        if (!hasAccessToCourse(user, parentPost.getCourseId())) {
            response.sendRedirect(request.getContextPath() + "/error?message=unauthorized");
            return;
        }
        
        ForumPost reply = new ForumPost();
        reply.setCourseId(parentPost.getCourseId());
        reply.setAuthorId(user.getUserId());
        reply.setParentPostId(postId);
        reply.setContent(content);
        
        if (forumDAO.createReply(reply)) {
            response.sendRedirect(request.getContextPath() + "/forum/post?postId=" + postId + "&success=replied");
        } else {
            response.sendRedirect(request.getContextPath() + "/forum/post?postId=" + postId + "&error=failed");
        }
    }

    private void deletePost(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        ForumPost post = forumDAO.getPostById(postId);
        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/error?message=notfound");
            return;
        }
        
        // Only post author or course teacher can delete
        Course course = courseDAO.getCourseById(post.getCourseId());
        boolean canDelete = post.getAuthorId() == user.getUserId() || 
                           (course != null && course.getTeacherId() == user.getUserId());
        
        if (!canDelete) {
            response.sendRedirect(request.getContextPath() + "/forum/post?postId=" + postId + "&error=unauthorized");
            return;
        }
        
        int courseId = post.getCourseId();
        if (forumDAO.deletePost(postId)) {
            response.sendRedirect(request.getContextPath() + "/forum/course?courseId=" + courseId + "&success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/forum/course?courseId=" + courseId + "&error=deletefailed");
        }
    }

    // Helper method to check if user has access to a course
    private boolean hasAccessToCourse(User user, int courseId) {
        if (user.getUserType() == User.UserType.ADMIN) {
            return true;
        } else if (user.getUserType() == User.UserType.TEACHER) {
            Course course = courseDAO.getCourseById(courseId);
            return course != null && course.getTeacherId() == user.getUserId();
        } else {
            // Student must be enrolled
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(user.getUserId());
            return enrollments.stream().anyMatch(e -> e.getCourseId() == courseId && 
                                                      e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE);
        }
    }

    // Helper method to get the correct view path
    private String getViewPath(String viewName) {
        // All users use student forum views (forums are the same for all roles)
        return "/WEB-INF/views/student/" + viewName + ".jsp";
    }
}
