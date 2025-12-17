package com.school.model;

import java.sql.Timestamp;

public class ForumPost {
    private int postId;
    private int courseId;
    private String courseCode;
    private String courseName;
    private int authorId;
    private String authorName;
    private String authorType; // STUDENT, TEACHER
    private Integer parentPostId; // Nullable for top-level posts
    private String title; // Only for top-level posts
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int replyCount; // Number of replies to this post

    // Constructors
    public ForumPost() {
    }

    public ForumPost(int postId, int courseId, int authorId, Integer parentPostId, 
                     String title, String content) {
        this.postId = postId;
        this.courseId = courseId;
        this.authorId = authorId;
        this.parentPostId = parentPostId;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    public Integer getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(Integer parentPostId) {
        this.parentPostId = parentPostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    // Helper methods
    public boolean isTopLevel() {
        return parentPostId == null;
    }

    public boolean isReply() {
        return parentPostId != null;
    }
}
