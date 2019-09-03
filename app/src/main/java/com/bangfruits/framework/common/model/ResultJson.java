package com.bangfruits.framework.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LY on 2019/4/2.
 */
public class ResultJson<T> implements Serializable {

    private static final long serialVersionUID = -2849146113542167339L;

    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName(value = "result" , alternate = {"data"})
    private T result;


    @SerializedName("userId")
    private int userId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ResultJosn{\n" +//
                "\tcode=" + code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + result + "\n" +//
                '}';
    }
}
