package dna.rest.common;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;


/**
 * The Class GlobalControllerExceptionHandler.
 * 
 * <pre>
 * 
 * </pre>
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle rest exception.
   * 
   * @param ex the ex
   * @return the response entity
   */
  @ExceptionHandler(value = {RestException.class})
  protected ResponseEntity<RestResource<?>> handleRestException(RestException ex) {
    
    RestResource<?> body = ex.getBody();
    
    if (ex instanceof RestClientException) {
      body.setStatus(400);
    } else if (ex instanceof RestServerException) {
      body.setStatus(500);
    } else {
      body.setStatus(500);
    }

    if (body.getErrors() == null || body.getErrors().size() == 0) {
      RestError error = new RestError();
      error.setCode(1);
      error.setMessage(ex.getMessage());
      List<RestError> errors = new ArrayList<RestError>();
      errors.add(error);
      body.setErrors(errors);
    }

    logger.debug("handleRestException " + body);
    
    return new ResponseEntity<RestResource<?>>(body, HttpStatus.OK);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleMissingServletRequestParameter
   * (org.springframework.web.bind.MissingServletRequestParameterException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);

    logger.info("MissingServletRequestParameterException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleTypeMismatch(org.springframework.beans.TypeMismatchException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);

    logger.info("TypeMismatchException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleBindException(org.springframework.validation.BindException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    logger.debug("Error input " + ex.toString());

    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    List<RestError> errors = new ArrayList<RestError>();
    for (FieldError e : ex.getFieldErrors()) {
      RestError error = new RestError();
      error.setCode(1);
      error.setMessage(e.getDefaultMessage());
      errors.add(error);
    }
    body.setErrors(errors);

    logger.info("BindException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleConversionNotSupported(org.springframework.beans.ConversionNotSupportedException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);

    logger.info("ConversionNotSupportedException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleHttpMediaTypeNotAcceptable(org.springframework.web.HttpMediaTypeNotAcceptableException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage("ex.getMessage()");
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("HttpMediaTypeNotAcceptableException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleHttpMediaTypeNotSupported(org.springframework.web.HttpMediaTypeNotSupportedException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage("ex.getMessage()");
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("HttpMediaTypeNotSupportedException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleHttpMessageNotReadable
   * (org.springframework.http.converter.HttpMessageNotReadableException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("HttpMessageNotReadableException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleHttpMessageNotWritable
   * (org.springframework.http.converter.HttpMessageNotWritableException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("HttpMessageNotWritableException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleHttpRequestMethodNotSupported
   * (org.springframework.web.HttpRequestMethodNotSupportedException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage("ex.getMessage()");
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("HttpRequestMethodNotSupportedException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("MethodArgumentNotValidException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleMissingServletRequestPart
   * (org.springframework.web.multipart.support.MissingServletRequestPartException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage("ex.getMessage()");
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("MissingServletRequestPartException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleNoSuchRequestHandlingMethod
   * (org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(
      NoSuchRequestHandlingMethodException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage("ex.getMessage()");
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("NoSuchRequestHandlingMethodException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#
   * handleServletRequestBindingException
   * (org.springframework.web.bind.ServletRequestBindingException,
   * org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus,
   * org.springframework.web.context.request.WebRequest)
   */
  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(
      ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    RestResource<Object> body = RestResourceFactory.newInstance();
    body.setStatus(status.value());

    RestError error = new RestError();
    error.setCode(1);
    error.setMessage(ex.getMessage());
    List<RestError> errors = new ArrayList<RestError>();
    errors.add(error);
    body.setErrors(errors);
    logger.info("ServletRequestBindingException " + body);
    return new ResponseEntity<Object>(body, headers, status);
  }


}
