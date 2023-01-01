package com.imooc.mall.exception;

import com.imooc.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理统一异常的handler
 */
// 拦截异常
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // 声明处理的是什么异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handlerException(Exception e){
        log.error("Default Exception: ", e);
        return ApiRestResponse.error(ImoocMallExceptionEnum.SYSTEM_EXCEPTION);
    }


    /**
     * 处理ImoocMallException异常
     * @param e 异常
     * @return 返回ApiRestResponse.error
     */
    @ExceptionHandler(ImoocMallException.class)
    @ResponseBody
    public Object handlerException(ImoocMallException e){
        log.error("ImoocMallException: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("handlerMethodArgumentNotValidException: ", e);
        return handlerBindingResult(e.getBindingResult());
    }

    private ApiRestResponse handlerBindingResult(BindingResult result){
        List<String> list = new ArrayList<>();
        if(result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError allError : allErrors) {
                list.add(allError.getDefaultMessage());
            }
        }
        if (list.size() == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
