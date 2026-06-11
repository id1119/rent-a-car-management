package model;

public enum LevelOfEducation {
    MIDDLE(100),
    HIGH(200),
    COLLEGE(500);

    private final double coefficient;

    LevelOfEducation(double coefficient){
        this.coefficient = coefficient;
    }

    public double getCoefficient() {
        return coefficient;
    }
}


