package com.school.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Grade {
    private int id;
    private int enrollmentId;
    private BigDecimal inclassScore;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;
    private String letterGrade;
    private BigDecimal gradePoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Grade() {
    }
    
    public Grade(int id, int enrollmentId, BigDecimal inclassScore,
                BigDecimal midtermScore, BigDecimal finalScore) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.inclassScore = inclassScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public BigDecimal getInclassScore() {
        return inclassScore;
    }
    
    public void setInclassScore(BigDecimal inclassScore) {
        this.inclassScore = inclassScore;
    }
    
    public BigDecimal getMidtermScore() {
        return midtermScore;
    }
    
    public void setMidtermScore(BigDecimal midtermScore) {
        this.midtermScore = midtermScore;
    }
    
    public BigDecimal getFinalScore() {
        return finalScore;
    }
    
    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public String getLetterGrade() {
        return letterGrade;
    }
    
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }
    
    public BigDecimal getGradePoint() {
        return gradePoint;
    }
    
    public void setGradePoint(BigDecimal gradePoint) {
        this.gradePoint = gradePoint;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public static String calculateLetterGrade(BigDecimal score) {
        if (score == null) return "N/A";
        
        double scoreValue = score.doubleValue();
        
        if (scoreValue >= 93.0) return "A";
        if (scoreValue >= 90.0) return "A-";
        if (scoreValue >= 87.0) return "B+";
        if (scoreValue >= 83.0) return "B";
        if (scoreValue >= 80.0) return "B-";
        if (scoreValue >= 77.0) return "C+";
        if (scoreValue >= 73.0) return "C";
        if (scoreValue >= 70.0) return "C-";
        if (scoreValue >= 67.0) return "D+";
        if (scoreValue >= 60.0) return "D";
        return "F";
    }
    
    public static BigDecimal calculateGradePoint(String letterGrade) {
        if (letterGrade == null || letterGrade.equals("N/A")) {
            return BigDecimal.ZERO;
        }
        
        switch (letterGrade) {
            case "A": return new BigDecimal("4.00");
            case "A-": return new BigDecimal("3.70");
            case "B+": return new BigDecimal("3.30");
            case "B": return new BigDecimal("3.00");
            case "B-": return new BigDecimal("2.70");
            case "C+": return new BigDecimal("2.30");
            case "C": return new BigDecimal("2.00");
            case "C-": return new BigDecimal("1.70");
            case "D+": return new BigDecimal("1.30");
            case "D": return new BigDecimal("1.00");
            case "F": return BigDecimal.ZERO;
            default: return BigDecimal.ZERO;
        }
    }
    
    public void calculateFinalGrade(GradeComponent component) {
        if (component == null || inclassScore == null || midtermScore == null || finalScore == null) {
            return;
        }
        
        // Calculate weighted total score
        this.totalScore = component.calculateWeightedScore(inclassScore, midtermScore, finalScore)
                                   .setScale(2, RoundingMode.HALF_UP);
        
        // Calculate letter grade and grade point
        this.letterGrade = calculateLetterGrade(totalScore);
        this.gradePoint = calculateGradePoint(letterGrade);
    }
    
    public boolean isComplete() {
        return inclassScore != null && midtermScore != null && finalScore != null && totalScore != null;
    }
    
    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", enrollmentId=" + enrollmentId +
                ", totalScore=" + totalScore +
                ", letterGrade='" + letterGrade + '\'' +
                ", gradePoint=" + gradePoint +
                '}';
    }
}
