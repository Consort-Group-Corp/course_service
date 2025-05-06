package uz.consortgroup.course_service.handler;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.exception.FileSizeLimitExceededException;
import uz.consortgroup.course_service.exception.LessonNotFoundException;
import uz.consortgroup.course_service.exception.MismatchException;
import uz.consortgroup.course_service.exception.UnsupportedFileExtensionException;
import uz.consortgroup.course_service.exception.UnsupportedMimeTypeException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Illegal state", ex.getMessage()));
    }

    @ExceptionHandler(LessonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLessonNotFoundException(LessonNotFoundException ex) {
        log.error("LessonNotFoundException: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Lesson not found", ex.getMessage()));
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeLimitExceeded(FileSizeLimitExceededException ex) {
        log.error("FileSizeLimitExceededException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "File size limit exceeded", ex.getMessage()));
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException(EmptyFileException ex) {
        log.error("EmptyFileException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Empty file", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedFileExtensionException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedFileExtensionException(UnsupportedFileExtensionException ex) {
        log.error("UnsupportedFileExtensionException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Unsupported file extension", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedMimeTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMimeTypeException(UnsupportedMimeTypeException ex) {
        log.error("UnsupportedMimeTypeException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Unsupported mime type", ex.getMessage()));
    }

    // General Exception Handlers
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", "Произошла непредвиденная ошибка"));
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(JsonParseException ex) {
        log.error("JsonParseException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "JSON parse error", ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        log.error("Missing request part: {}", ex.getRequestPartName(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(), "Missing request part", "Required request part '" + ex.getRequestPartName() + "' is not present"));
    }

    @ExceptionHandler(MismatchException.class)
    public ResponseEntity<ErrorResponse> handleMismatchException(MismatchException ex) {
       log.error("MismatchException: ", ex);
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Mismatch error", ex.getMessage()));
    }


    // Validation Handlers
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("Validation error: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", String.join("; ", errors)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", ex.getMessage()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Database integrity violation: {}", ex.getMessage());
        if (ex.getMessage().contains("verification_codes_user_id_fkey")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid user ID", "User with the given ID does not exist"));
        }
        if (ex.getMessage().contains("unique_constraint")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Data conflict", "Duplicate data found or constraint violation"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid data", "Database integrity violation occurred"));
    }
}
