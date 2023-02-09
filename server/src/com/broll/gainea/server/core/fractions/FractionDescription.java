package com.broll.gainea.server.core.fractions;

import java.util.ArrayList;
import java.util.List;

public class FractionDescription {

    private String general;
    private List<String> plus = new ArrayList<>();
    private List<String> contra = new ArrayList<>();

    public FractionDescription(String general) {
        this.general = general;
    }

    public void plus(String strength) {
        plus.add(strength);
    }

    public void contra(String weakness) {
        contra.add(weakness);
    }

    public List<String> getContra() {
        return contra;
    }

    public List<String> getPlus() {
        return plus;
    }

    public String getGeneral() {
        return general;
    }
}

