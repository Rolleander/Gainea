package com.broll.gainea.misc;

import com.broll.networklib.network.NetworkException;
import com.broll.networklib.network.NetworkRegistry;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageLoader<T> {

    private List<Class<? extends T>> classes = new ArrayList<>();

    private int index;

    public PackageLoader(Class<T> clazz, String path) {
        try {
            ClassPath cp = ClassPath.from(PackageLoader.class.getClassLoader());
            cp.getTopLevelClasses(path).forEach(cl -> {
                Class javaClazz = cl.getClass();
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
            next = instantite(classes.get(index));
        }
        index++;
        return next;
    }

    public void resetIndex() {
        index = 0;
    }

    public T instantite(Class clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + clazz);
        }
    }

    public T instantiateRandom() {
        return instantite(getRandom());
    }

    public Class<? extends T> getRandom() {
        return classes.get((int) Math.random() * classes.size());
    }

    public List<Class<? extends T>> getClasses() {
        return classes;
    }
}
