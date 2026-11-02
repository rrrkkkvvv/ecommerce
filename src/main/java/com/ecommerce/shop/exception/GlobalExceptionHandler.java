package com.ecommerce.shop.exception;

import com.ecommerce.shop.exception.payment.PaymentNotFoundException;
import com.ecommerce.shop.exception.product.ProductAlreadyInCartException;
import com.ecommerce.shop.exception.product.ProductNotFoundException;
import com.ecommerce.shop.exception.user.InvalidCredentialsException;
import com.ecommerce.shop.exception.user.UserAccessDeniedException;
import com.ecommerce.shop.exception.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex
    ){
        ErrorResponseDTO errorDTO = new ErrorResponseDTO("Internal server error");
        log.error("Handle exception ",ex);
        return ResponseEntity
                .status(500)
                .body(errorDTO);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(400)
                .body(errors);
    }

    @ExceptionHandler(exception = {InvalidCredentialsException.class, ProductAlreadyInCartException.class})
    public ResponseEntity<?> handleInvalidCredentials(RuntimeException ex) {
        log.error("Handle exception ",ex);
        return ResponseEntity.status(409).body(new ErrorResponseDTO(ex.getMessage() ));
    }
    @ExceptionHandler(exception ={UserAccessDeniedException.class})
    public ResponseEntity<?> handleAccessDenied(RuntimeException ex) {
        log.error("Handle exception ",ex);
        return ResponseEntity.status(403).body(new ErrorResponseDTO(ex.getMessage() ));
    }


    @ExceptionHandler(exception ={UserNotFoundException.class, ProductNotFoundException.class, PaymentNotFoundException.class})
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        log.error("Handle exception ",ex);
        return ResponseEntity.status(404).body(new ErrorResponseDTO(ex.getMessage() ));
    }

}
