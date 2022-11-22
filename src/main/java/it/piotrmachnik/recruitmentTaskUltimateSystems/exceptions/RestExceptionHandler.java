package it.piotrmachnik.recruitmentTaskUltimateSystems.exceptions;

import it.piotrmachnik.recruitmentTaskUltimateSystems.model.ErrorModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ErrorModel error = ErrorModel.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Validation Error").details(details).build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<ErrorModel> handleEntityNotFound(EntityNotFoundException ex) {
        List<String> details = Arrays.asList(ex.getMessage());
        ErrorModel error = ErrorModel.builder().httpStatus(HttpStatus.NOT_FOUND).message("Entity not found").details(details).build();
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncompleteDataException.class)
    private ResponseEntity<ErrorModel> handleIncompleteData(IncompleteDataException ex) {
        List<String> details = Arrays.asList(ex.getMessage());
        ErrorModel error = ErrorModel.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Incomplete data").details(details).build();
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    private ResponseEntity<ErrorModel> handleEntityAlreadyExists(EntityAlreadyExistsException ex) {
        List<String> details = Arrays.asList(ex.getMessage());
        ErrorModel error = ErrorModel.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Entity already exists").details(details).build();
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
