package com.nhson.examservice.exam.entities;

public enum Subject {
    MATH("Math"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    BIOLOGY("Biology"),
    HISTORY("History"),
    GEOGRAPHY("Geography"),
    CIVICS("Civics");

    private final String displayName;
    Subject(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

