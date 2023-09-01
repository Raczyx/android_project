package com.guet.photo_sharing.entity;


public class ResponseBody <T>{
    private Integer code;
    private String msg;
    private T data;

    public static final Integer SUCCESS = 600;
    public static final Integer ERROR = 700;

    public ResponseBody success(String msg, T data){
        this.code = SUCCESS;
        this.msg = msg ;
        this.data = data;
        return this;
    }

    public ResponseBody error(String msg, T data){
        this.code = ERROR;
        this.msg = msg ;
        this.data = data;
        return this;
    }

    public ResponseBody(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseBody success(String msg){
        this.code = SUCCESS;
        this.msg = msg ;
        return this;
    }

    public ResponseBody error(String msg){
        this.code = ERROR;
        this.msg = msg;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccessful(){
        return this.code.equals(SUCCESS);
    }
}
