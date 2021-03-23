package com.eden.msutils.core.entity;


import com.eden.msutils.core.exception.BizException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class R<T> {

    public static final int SUCCESS_CODE = 20000;

    public static final int UNKOWN_ERROR_CODE = 4000;

    @ApiModelProperty(value = "响应编码:20000-请求处理成功")
    private int code;

    @ApiModelProperty(value = "提示消息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private T data;

    private R() {

    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> success(){
        return new R<>(SUCCESS_CODE,"调用成功", null);
    }

    public static <T> R<T> success(T data){
        return new R<>(SUCCESS_CODE,"调用成功", data);
    }

    public static <T> R<T> fail(Exception e){
        return new R<>(UNKOWN_ERROR_CODE, e.getMessage(), null);
    }

    public static <T> R<T> fail(BizException e){
        return new R<>(e.getCode(), e.getMessage(), null);
    }

}
