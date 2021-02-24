package com.broll.gainea.server.core.fractions;

import com.broll.gainea.misc.PackageLoader;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class FractionFactory {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.fractions.impl";
    private static PackageLoader<Fraction> fractionLoader = new PackageLoader<>(Fraction.class, PACKAGE_PATH);

    public static Fraction create(FractionType type) {
        return fractionLoader.instantiateAll().stream().filter(it -> it.getType() == type).findFirst().orElseThrow(() -> new RuntimeException("No fraction factory for type " + type));
    }

    public static List<Fraction> createAll() {
        return fractionLoader.instantiateAll();
    }

}
