package com.broll.gainea.misc;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PackageLoader<T> {

    private List<Class<? extends T>> classes = new ArrayList<>();

    private int index;

    public PackageLoader(Class<T> clazz, String path) {
        try {
            ClassPath cp = ClassPath.from(PackageLoader.class.getClassLoader());
            cp.getTopLevelClasses(path).forEach(cl -> {
                Class javaClazz = cl.load();
                classes.add(javaClazz);
            });
            shuffle();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load classes", e);
        }
    }

    public void shuffle() {
        Collections.shuffle(classes);
    }

    public T next() {
        T next = null;
        if (index < classes.size()) {
            next = instantiate(classes.get(index));
        }
        index++;
        return next;
    }

    public void resetIndex() {
        index = 0;
    }

    public List<T> instantiateAll() {
        return classes.stream().map(this::instantiate).collect(Collectors.toList());
    }

    public T instantiate(int index) {
        return instantiate(classes.get(index));
    }

    public T instantiate(Class clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + clazz,e);
        }
    }

    public T instantiateRandom() {
        return instantiate(getRandom());
    }

    public int getRandomIndex() {
        return (int) Math.random() * classes.size();
    }

    public Class<? extends T> getRandom() {
        return classes.get(getRandomIndex());
    }

    public List<Class<? extends T>> getClasses() {
        return classes;
    }
}
