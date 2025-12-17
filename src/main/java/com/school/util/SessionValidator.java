package com.school.util;

import com.school.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionValidator {
    
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null && user.getUserType() == User.UserType.ADMIN;
    }

    public static boolean isTeacher(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null && user.getUserType() == User.UserType.TEACHER;
    }

    public static boolean isStudent(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null && user.getUserType() == User.UserType.STUDENT;
    }

    public static boolean hasAccess(HttpServletRequest request, User.UserType requiredType) {
        User user = getLoggedInUser(request);
        return user != null && user.getUserType() == requiredType;
    }
}
