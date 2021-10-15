package com.bjpowernode.pojo;

//ajax请求的统一返回结果对象
public class RespObject {
    private int code;//请求应答结果码 , 成功,失败,错误的状态码
    private String msg;//对code的解释
    private Object data;//返回给ajax请求的数据

    public RespObject() {
    }

    public RespObject(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static RespObject FAIL(String msg){
        RespObject ro  = new RespObject();
        ro.setCode(1000);//默认的,未知错误
        ro.setMsg(msg);
        ro.setData("");
        return ro;
    }

    public static RespObject SUCC(String msg){
        RespObject ro  = new RespObject();
        ro.setCode(0);//成功
        ro.setMsg(msg);
        ro.setData("");
        return ro;
    }

    public int getCode() {
        return code;
    }

    public RespObject setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RespObject setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RespObject setData(Object data) {
        this.data = data;
        return this;
    }
}
