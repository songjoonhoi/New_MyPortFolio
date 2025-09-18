// src/main/java/com/example/demo/exception/GlobalExceptionHandler.java
package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 포트폴리오 관련 예외 처리
     */
    @ExceptionHandler(PortfolioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handlePortfolioNotFound(PortfolioNotFoundException ex, HttpServletRequest request) {
        log.error("포트폴리오를 찾을 수 없음: {}", ex.getMessage());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("포트폴리오 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("message", "포트폴리오 정보를 찾을 수 없습니다.");
        mv.addObject("details", ex.getMessage());
        return mv;
    }

    /**
     * 파일 업로드 크기 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ModelAndView handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("파일 크기 초과: {}", ex.getMessage());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("업로드 파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        
        ModelAndView mv = new ModelAndView("error/413");
        mv.addObject("message", "파일 크기가 너무 큽니다.");
        mv.addObject("details", "최대 10MB까지 업로드 가능합니다.");
        return mv;
    }

    /**
     * 파일 업로드 관련 예외 처리
     */
    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleFileUploadException(FileUploadException ex, HttpServletRequest request) {
        log.error("파일 업로드 실패: {}", ex.getMessage());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        ModelAndView mv = new ModelAndView("error/400");
        mv.addObject("message", "파일 업로드에 실패했습니다.");
        mv.addObject("details", ex.getMessage());
        return mv;
    }

    /**
     * 유효성 검사 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("유효성 검사 실패: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        if (isAjaxRequest(request)) {
            ModelAndView mv = new ModelAndView("jsonView");
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "입력 정보를 확인해주세요.");
            response.put("errors", errors);
            mv.addObject(response);
            return mv;
        }
        
        ModelAndView mv = new ModelAndView("error/400");
        mv.addObject("message", "입력 정보가 올바르지 않습니다.");
        mv.addObject("errors", errors);
        return mv;
    }

    /**
     * 접근 권한 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.error("접근 권한 거부: {}", ex.getMessage());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        
        ModelAndView mv = new ModelAndView("error/403");
        mv.addObject("message", "접근 권한이 없습니다.");
        mv.addObject("details", "관리자 권한이 필요한 페이지입니다.");
        return mv;
    }

    /**
     * 404 Not Found 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("페이지를 찾을 수 없음: {}", ex.getRequestURL());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("요청한 페이지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("message", "페이지를 찾을 수 없습니다.");
        mv.addObject("details", "요청하신 페이지가 존재하지 않습니다.");
        mv.addObject("requestUrl", ex.getRequestURL());
        return mv;
    }

    /**
     * IO 예외 처리
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleIOException(IOException ex, HttpServletRequest request) {
        log.error("IO 오류 발생: {}", ex.getMessage(), ex);
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("파일 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("message", "파일 처리 중 오류가 발생했습니다.");
        mv.addObject("details", "잠시 후 다시 시도해주세요.");
        return mv;
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("잘못된 요청: {}", ex.getMessage());
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        ModelAndView mv = new ModelAndView("error/400");
        mv.addObject("message", "잘못된 요청입니다.");
        mv.addObject("details", ex.getMessage());
        return mv;
    }

    /**
     * 일반적인 예외 처리 (최종 catch-all)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("예상치 못한 오류 발생", ex);
        
        if (isAjaxRequest(request)) {
            return createAjaxErrorResponse("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("message", "시스템 오류가 발생했습니다.");
        mv.addObject("details", "잠시 후 다시 시도해주시거나 관리자에게 문의해주세요.");
        return mv;
    }

    /**
     * AJAX 요청 여부 확인
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * AJAX 에러 응답 생성
     */
    private ModelAndView createAjaxErrorResponse(String message, HttpStatus status) {
        ModelAndView mv = new ModelAndView("jsonView");
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("status", status.value());
        mv.addObject(response);
        return mv;
    }
}

/**
 * 포트폴리오를 찾을 수 없을 때 발생하는 예외
 */
class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String message) {
        super(message);
    }
    
    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * 파일 업로드 관련 예외
 */
class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}