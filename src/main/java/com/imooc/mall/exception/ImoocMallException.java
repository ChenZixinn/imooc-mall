package com.imooc.mall.exception;

/**
 *  异常类
 */
public class ImoocMallException extends RuntimeException{
    private final Integer code;
    private final String message;

    public ImoocMallException(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public ImoocMallException(ImoocMallExceptionEnum imoocMallExceptionEnum){
        this(imoocMallExceptionEnum.getCode(), imoocMallExceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
