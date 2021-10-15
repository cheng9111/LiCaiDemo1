package com.bjpowernode.util;

import java.awt.geom.FlatteningPathIterator;
import java.math.BigDecimal;

public class DecimalUtil {

    /**
     * @param n1 数字1
     * @param n2 数字2
     * @return  true(n1>=n2)
     */
    //判断 n1 >= n2 true, 其他false
    public static boolean ge(BigDecimal n1, BigDecimal n2){
        if( n1 == null || n2 == null){
            throw new RuntimeException("参数为null");
        }
        boolean res = false;
        if( n1.compareTo(n2) >= 0 ){
            res = true;
        }
        return res;
    }

    /**
     * @param n1 数字1
     * @param n2 数字2
     * @return  true(n1<=n2)
     */
    //判断 n1 <= n2 true, 其他false
    public static boolean le(BigDecimal n1, BigDecimal n2){
        if( n1 == null || n2 == null){
            throw new RuntimeException("参数为null");
        }
        boolean res = false;
        if( n1.compareTo(n2) < 1  ){
            res = true;
        }
        return res;
    }
}
