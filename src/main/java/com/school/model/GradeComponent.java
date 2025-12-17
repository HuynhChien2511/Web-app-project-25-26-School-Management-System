package com.school.model;

import java.math.BigDecimal;

public class GradeComponent {
    private int id;
    private int courseId;
    private int semesterId;
    private BigDecimal inclassPercentage;
    private BigDecimal midtermPercentage;
    private BigDecimal finalPercentage;
    
    // Constructors
    public GradeComponent() {
    }
    
    public GradeComponent(int id, int courseId, int semesterId,
                         BigDecimal inclassPercentage, BigDecimal midtermPercentage,
                         BigDecimal finalPercentage) {
        this.id = id;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.inclassPercentage = inclassPercentage;
        this.midtermPercentage = midtermPercentage;
        this.finalPercentage = finalPercentage;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public BigDecimal getInclassPercentage() {
        return inclassPercentage;
    }
    
    public void setInclassPercentage(BigDecimal inclassPercentage) {
        this.inclassPercentage = inclassPercentage;
    }
    
    public BigDecimal getMidtermPercentage() {
        return midtermPercentage;
    }
    
    public void setMidtermPercentage(BigDecimal midtermPercentage) {
        this.midtermPercentage = midtermPercentage;
    }
    
    public BigDecimal getFinalPercentage() {
        return finalPercentage;
    }
    
    public void setFinalPercentage(BigDecimal finalPercentage) {
        this.finalPercentage = finalPercentage;
    }
    
    // Helper methods
    public boolean isValidPercentages() {
        if (inclassPercentage == null || midtermPercentage == null || finalPercentage == null) {
            return false;
        }
        // Enforce integer percentages in range [0, 100]
        if (!isInteger(inclassPercentage) || !isInteger(midtermPercentage) || !isInteger(finalPercentage)) {
            return false;
        }
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal hundred = new BigDecimal("100");
        if (inclassPercentage.compareTo(zero) < 0 || inclassPercentage.compareTo(hundred) > 0) return false;
        if (midtermPercentage.compareTo(zero) < 0 || midtermPercentage.compareTo(hundred) > 0) return false;
        if (finalPercentage.compareTo(zero) < 0 || finalPercentage.compareTo(hundred) > 0) return false;

        // Sum must equal exactly 100 (no tolerance since integers)
        BigDecimal total = inclassPercentage.add(midtermPercentage).add(finalPercentage);
        return total.compareTo(hundred) == 0;
    }

    private boolean isInteger(BigDecimal value) {
        return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }
    
    public BigDecimal calculateWeightedScore(BigDecimal inclassScore, BigDecimal midtermScore, BigDecimal finalScore) {
        BigDecimal weightedInclass = inclassScore.multiply(inclassPercentage).divide(new BigDecimal("100"));
        BigDecimal weightedMidterm = midtermScore.multiply(midtermPercentage).divide(new BigDecimal("100"));
        BigDecimal weightedFinal = finalScore.multiply(finalPercentage).divide(new BigDecimal("100"));
        
        return weightedInclass.add(weightedMidterm).add(weightedFinal);
    }
    
    @Override
    public String toString() {
        return "GradeComponent{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", inclass=" + inclassPercentage + "%" +
                ", midterm=" + midtermPercentage + "%" +
                ", final=" + finalPercentage + "%" +
                '}';
    }
}
