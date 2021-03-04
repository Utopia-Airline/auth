package com.ss.utopia.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle all Exceptions in all controllers
 */
@RestControllerAdvice
public class ExceptionHandlerController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

  @Bean
  public ErrorAttributes errorAttributes() {
    // Hide exceptions field in the return object
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(
        WebRequest webRequest,
        ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<String, Object>();
        Object errorMessage = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);
        if (errorMessage != null) {
          errorAttributes.put("message", errorMessage);
        }
        return errorAttributes;
      }
    };
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  public void handleInsufficientAuthenticationException(InsufficientAuthenticationException ex,
                                                        HttpServletResponse res) throws IOException {
    LOGGER.error("Handled Insufficient Authentication Exception", ex);
    res.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient Authentication");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(AccessDeniedException ex,
                                          HttpServletResponse res) throws IOException {
    res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
  }

  @ExceptionHandler(HttpServerErrorException.class)
  public void handleHttpServerErrorException(HttpServerErrorException ex,
                                             HttpServletResponse res) throws IOException {
    res.sendError(ex.getStatusCode().value(), ex.getStatusText());
  }

//  @ExceptionHandler(Exception.class)
//  public void handleException(Exception ex, HttpServletResponse res) throws IOException {
//    LOGGER.error("Handled Exception", ex);
//    res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
//  }
}
