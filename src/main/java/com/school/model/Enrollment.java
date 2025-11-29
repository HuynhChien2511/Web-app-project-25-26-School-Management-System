package com.school.model;

import java.sql.Timestamp;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int courseId;
    private int semesterId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private Timestamp enrollmentDate;
    private String grade;
    private EnrollmentStatus status;
    private Course course; // Full course object with all details

    public enum EnrollmentStatus {
        ACTIVE, COMPLETED, DROPPED
    }

    // Constructors
    public Enrollment() {
    }

    public Enrollment(int enrollmentId, int studentId, int courseId, int semesterId,
                      Timestamp enrollmentDate, String grade, EnrollmentStatus status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
        this.status = status;
    }

    // Getters and Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public Timestamp getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Timestamp enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", status=" + status +
                '}';
    }
}
