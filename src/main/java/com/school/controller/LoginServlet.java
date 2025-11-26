package com.school.controller;

import java.io.IOException;

import com.school.dao.UserDAO;
import com.school.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.validateUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userType", user.getUserType().name());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("fullName", user.getFullName());

            // Redirect based on user type
            switch (user.getUserType()) {
                case ADMIN:
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    break;
                case TEACHER:
                    response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
                    break;
                case STUDENT:
                    response.sendRedirect(request.getContextPath() + "/student/dashboard");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
