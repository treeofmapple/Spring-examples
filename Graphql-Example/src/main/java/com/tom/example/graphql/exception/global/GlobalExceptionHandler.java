package com.tom.example.graphql.exception.global;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tom.example.graphql.exception.InternalException;
import com.tom.example.graphql.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ NotFoundException.class })
	public ResponseEntity<String> handleNotFoundException(RuntimeException exp) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(((CustomGlobalException) exp).getMsg());
	}

	@ExceptionHandler({ })
	public ResponseEntity<String> handleBadRequestException(RuntimeException exp) {
		log.error("Error during data processing", exp);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(((CustomGlobalException) exp).getMsg());
	}
	
	@ExceptionHandler({ InternalException.class })
	public ResponseEntity<String> handleInternalSystemException(RuntimeException exp) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(((CustomGlobalException) exp).getMsg());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exp) {

		var errors = new HashMap<String, String>();

		exp.getBindingResult().getAllErrors().forEach(error -> {
			var fieldName = ((FieldError) error).getField();
			var errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
	}

}