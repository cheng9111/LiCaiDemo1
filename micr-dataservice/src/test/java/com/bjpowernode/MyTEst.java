package com.bjpowernode;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class MyTEst {

    @Test
    public void test01(){
        Date date = new Date();
        System.out.println("date="+date);

        Date d1 = DateUtils.truncate(date, Calendar.DATE);
        System.out.println("d1 = " + d1);
    }

    @Test
    public void test02(){
        String s="1.12";
        String r = String.valueOf(Double.parseDouble(s) * 100);

        BigDecimal b = new BigDecimal(s).multiply(new BigDecimal("100"));

        System.out.println("r=="+b.intValue()+","+b.toPlainString());

    }
}
