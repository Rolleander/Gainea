package com.broll.gainea.test;

import com.broll.gainea.server.core.utils.StreamUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ConcModTest {

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        list.add("Hallo");
        list.add("Test");
        list.add("Hallo");
        StreamUtils.safeForEach(list.stream().filter(it -> it.startsWith("T")), it -> list.remove(it));
        Assert.assertEquals(2, list.size());
    }


}
