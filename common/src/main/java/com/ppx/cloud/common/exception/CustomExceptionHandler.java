package com.ppx.cloud.common.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ppx.cloud.common.contoller.ReturnMap;
import com.ppx.cloud.common.exception.security.PermissionParamsException;
import com.ppx.cloud.common.exception.security.PermissionUriException;



/**
 * 定义异常处理 分类异常，不是所有的异常都要全部信息，如NoSuchRequestHandlingMethodException
 * MissingServletRequestParameterException 和自定义的异常，不用打印到控制台，提高系统性能
 * 
 * @author mark
 *
 * @date 2018年10月28日
 */
@ControllerAdvice
// 如果使用了监控功能，则会在MonitorExceptionHandler里继承CustomExceptionHandler
@ConditionalOnMissingClass({"com.ppx.cloud.monitor.exception.MonitorExceptionHandler"})
public class CustomExceptionHandler implements HandlerExceptionResolver {

    @ExceptionHandler(value = Throwable.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object,
            Exception exception) {
        
        ErrorPojo error = ErrorUtils.getErroCode(exception);
        
        // errorCode=ErrorCode.IGNORE_ERROR的异常，不需要修改后端代码，不打印
        if (error.getErrcode() != ErrorUtils.ERROR_LEVEL_IGNORE) {
           exception.printStackTrace();
        }
        response.setStatus(500);
                
        if (PermissionUriException.class.getSimpleName().equals(error.getErrmsg())
                || PermissionParamsException.class.getSimpleName().equals(error.getErrmsg())) {
            response.setStatus(403);
        }

        // 
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("text/html") >= 0) {
        	ReturnMap.errorHtml(response, error.getErrcode(), error.getErrlevel(), error.getErrmsg() + "|" + request.getAttribute("marker"));
        } else {
        	ReturnMap.errorJson(response, error.getErrcode(), error.getErrlevel(), error.getErrmsg() + "|" + request.getAttribute("marker"));
        }

       
        
        return null;
    }

}
