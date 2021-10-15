package com.bjpowernode.util;

import java.util.regex.Pattern;

public class CommonUtil {

    //检查产品类型
    public static boolean checkProductType(Integer type){
        boolean flag = false;
        if( type != null && ( type >=0 && type <=2)){
            flag = true;
        }
        return flag;
    }

    //处理pageNo
    public static int defaultPageNo(Integer pageNo){
        if( pageNo == null || pageNo < 1 ){
            pageNo = 1;
        }
        return  pageNo;
    }

    //处理pageSize
    public static int defaultPageSize(Integer pageSize){
        if( pageSize == null || pageSize < 5 || pageSize > 100){
            pageSize = 9;
        }
        return pageSize;
    }

    //检查手机号的格式
    public static boolean checkPhone(String phone){
        boolean b= false;
        if( phone != null){
            b = Pattern.matches("^1[1-9]\\d{9}$",phone);
        }
        return b;
    }
}
