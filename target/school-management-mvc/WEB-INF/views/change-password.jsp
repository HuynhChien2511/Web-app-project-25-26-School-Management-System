<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Change Password" scope="request"/>
<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div class="main-content" style="margin-left: 0; max-width: 600px; margin: 60px auto; padding: 30px;">
    <div class="card">
        <h1 style="color: #333; margin-bottom: 10px; font-size: 28px;">🔐 Change Password</h1>
        <p style="color: #7f8c8d; margin-bottom: 30px;">Update your account password</p>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">❌ ${error}</div>
        </c:if>
        
        <c:if test="${param.success == 'true'}">
            <div class="alert alert-success">✅ Password changed successfully!</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/change-password" method="post">
            <div class="form-group">
                <label for="currentPassword">Current Password *</label>
                <input type="password" 
                       id="currentPassword" 
                       name="currentPassword" 
                       placeholder="Enter your current password"
                       required
                       autofocus>
            </div>
            
            <div class="form-group">
                <label for="newPassword">New Password *</label>
                <input type="password" 
                       id="newPassword" 
                       name="newPassword" 
                       placeholder="Enter new password"
                       minlength="8"
                       required>
                <small style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 5px;">
                    Must be at least 8 characters
                </small>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Confirm New Password *</label>
                <input type="password" 
                       id="confirmPassword" 
                       name="confirmPassword" 
                       placeholder="Confirm new password"
                       minlength="8"
                       required>
            </div>
            
            <div style="margin-top: 30px; display: flex; gap: 10px;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Change Password</button>
                <a href="${pageContext.request.contextPath}/<c:choose><c:when test='${sessionScope.userType == \"ADMIN\"}'>admin</c:when><c:when test='${sessionScope.userType == \"TEACHER\"}'>teacher</c:when><c:otherwise>student</c:otherwise></c:choose>/dashboard" 
                   class="btn btn-danger" style="flex: 1; text-align: center; line-height: 20px;">Cancel</a>
            </div>
        </form>
    </div>
</div>

<style>
    .main-content {
        min-height: calc(100vh - 60px);
    }
    
    .card h1 {
        font-size: 28px;
    }
    
    .form-group input[type="password"] {
        width: 100%;
    }
    
    .btn {
        text-decoration: none;
        display: inline-block;
    }
</style>

<jsp:include page="/WEB-INF/includes/footer.jsp"/>
