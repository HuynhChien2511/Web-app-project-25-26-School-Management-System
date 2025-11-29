package com.school.model;

import java.sql.Timestamp;

public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private int authorId;
    private String authorName;
    private Integer courseId; // Nullable for school-wide announcements
    private String courseName;
    private String courseCode;
    private boolean isImportant;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public Announcement() {
    }

    public Announcement(int announcementId, String title, String content, 
                        int authorId, Integer courseId, boolean isImportant) {
        this.announcementId = announcementId;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.courseId = courseId;
        this.isImportant = isImportant;
    }

    // Getters and Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
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

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
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

    // Helper method to check if announcement is course-specific
    public boolean isCourseSpecific() {
        return courseId != null;
    }

    // Helper method to check if announcement is school-wide
    public boolean isSchoolWide() {
        return courseId == null;
    }
}
