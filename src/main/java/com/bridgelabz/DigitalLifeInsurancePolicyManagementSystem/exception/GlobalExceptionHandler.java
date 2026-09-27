package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

 private ResponseEntity<ErrorResponse> error(
         HttpStatus status,
         String code,
         String message
 ) {

  return ResponseEntity
          .status(status)
          .body(
                  new ErrorResponse(
                          status.value(),
                          code,
                          message,
                          LocalDateTime.now()
                  )
          );
 }

 @ExceptionHandler(CustomerNotFoundException.class)
 public ResponseEntity<ErrorResponse> customer(
         CustomerNotFoundException exception
 ) {

  return error(
          HttpStatus.NOT_FOUND,
          "CUSTOMER_NOT_FOUND",
          exception.getMessage()
  );
 }

 @ExceptionHandler(PlanNotFoundException.class)
 public ResponseEntity<ErrorResponse> plan(
         PlanNotFoundException exception
 ) {

  return error(
          HttpStatus.NOT_FOUND,
          "PLAN_NOT_FOUND",
          exception.getMessage()
  );
 }

 @ExceptionHandler(PolicyNotFoundException.class)
 public ResponseEntity<ErrorResponse> policy(
         PolicyNotFoundException exception
 ) {

  return error(
          HttpStatus.NOT_FOUND,
          "POLICY_NOT_FOUND",
          exception.getMessage()
  );
 }

 @ExceptionHandler(PaymentNotFoundException.class)
 public ResponseEntity<ErrorResponse> payment(
         PaymentNotFoundException exception
 ) {

  return error(
          HttpStatus.NOT_FOUND,
          "PAYMENT_NOT_FOUND",
          exception.getMessage()
  );
 }

 @ExceptionHandler(PremiumNotFoundException.class)
 public ResponseEntity<ErrorResponse> premium(
         PremiumNotFoundException exception
 ) {

  return error(
          HttpStatus.NOT_FOUND,
          "PREMIUM_NOT_FOUND",
          exception.getMessage()
  );
 }

 @ExceptionHandler(InvalidCredentialsException.class)
 public ResponseEntity<ErrorResponse> credentials(
         InvalidCredentialsException exception
 ) {

  return error(
          HttpStatus.UNAUTHORIZED,
          "INVALID_CREDENTIALS",
          exception.getMessage()
  );
 }

 @ExceptionHandler(DuplicateCustomerException.class)
 public ResponseEntity<ErrorResponse> duplicate(
         DuplicateCustomerException exception
 ) {

  return error(
          HttpStatus.CONFLICT,
          "DUPLICATE_CUSTOMER",
          exception.getMessage()
  );
 }

 @ExceptionHandler({
         InvalidPolicyException.class,
         PaymentFailedException.class
 })
 public ResponseEntity<ErrorResponse> business(
         RuntimeException exception
 ) {

  return error(
          HttpStatus.BAD_REQUEST,
          "BUSINESS_ERROR",
          exception.getMessage()
  );
 }

 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ErrorResponse> validation(
         MethodArgumentNotValidException exception
 ) {

  String message = exception
          .getBindingResult()
          .getFieldErrors()
          .stream()
          .map(fieldError ->
                  fieldError.getField()
                          + ": "
                          + fieldError.getDefaultMessage()
          )
          .collect(Collectors.joining(", "));

  return error(
          HttpStatus.BAD_REQUEST,
          "VALIDATION_ERROR",
          message
  );
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<ErrorResponse> other(
         Exception exception
 ) {

  return error(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "INTERNAL_ERROR",
          exception.getMessage()
  );
 }
}