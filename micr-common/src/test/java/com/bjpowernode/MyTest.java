package com.bjpowernode;

import org.junit.Test;

import java.util.regex.Pattern;

public class MyTest {

    @Test
    public void test(){
        String phone="136000000k0";
        boolean b = Pattern.matches("^1[1-9]\\d{9}$",phone);
        System.out.println(phone + ","+b);
    }
}
