package com.broll.gainea.server.init;

public enum GoalTypes {

    ALL("Alle"),ONLY_EASY("Nur Einfach"), ONLY_MEDIUM("Nur Mittel"), ONLY_HARD("Nur Schwer");
    private String name;

    GoalTypes(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
