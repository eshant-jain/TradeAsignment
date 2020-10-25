package io.eshu.tradeassignment.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidVersion.class)
    public ResponseEntity<Object> invalidVersion( InvalidVersion exception, WebRequest webRequest) {
        CustomError response = new CustomError();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Version is lower than the current version");
        ResponseEntity<Object> entity = new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        return entity;
    }
	
	@ExceptionHandler(InvalidMaturityDate.class)
    public ResponseEntity<Object> invaliMaturityDate( InvalidMaturityDate exception, WebRequest webRequest) {
        CustomError response = new CustomError();
        response.setDateTime(LocalDateTime.now());
        response.setMessage("Invalid Maturity Date");
        ResponseEntity<Object> entity = new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        return entity;
    }
}
