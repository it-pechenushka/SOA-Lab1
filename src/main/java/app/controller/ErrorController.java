package app.controller;

import app.exception.ValidationException;
import app.helper.ResponseBuilder;
import app.helper.common.ErrorMessages;
import app.model.StudyGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.*;

@ControllerAdvice
public class ErrorController {
    private static final Map<String, String> fieldsDescription  = new HashMap<String, String>() {{
        put("groupAdmin.name", "'Name' in Group Admin");
        put("name", "'Name' in Study Group");
        put("xCoordinate", "'X' in Study Group");
        put("yCoordinate", "'Y' in Study Group");
        put("studentsCount", "'Students Count' in Study Group");
        put("expelledStudents", "'Expelled Students' in Study Group");
        put("shouldBeExpelled", "'Should Be Expelled' in Study Group");
        put("formOfEducation", "'Form Of Education' in Study Group");
        put("groupAdmin.weight", "'Weight' in Group Admin");
        put("groupAdmin.nationality", "'Nationality' in Group Admin");
        put("groupAdmin.location.xLocation", "'X' in Location");
        put("groupAdmin.location.yLocation", "'Y' in Location");
        put("groupAdmin.location.zLocation", "'Z' in Location");
    }};

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handleException(Exception exception, HttpServletResponse response) {
        response.setContentType("text/xml;charset=utf-8");
        String errorName = exception.getClass().getName();
        exception.printStackTrace();
        switch (errorName) {
            case "org.springframework.web.method.annotation.MethodArgumentTypeMismatchException":
                return handleMessageException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_ID);
            case "javax.xml.bind.UnmarshalException":
                exception.printStackTrace();
                return handleMessageException(HttpStatus.BAD_REQUEST, "Cannot parse params. Check this out:", ErrorMessages.WARNING_ID, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_FLOAT);
            case "app.exception.ValidationException":
                return handleValidationException(exception);
            case "app.exception.InvalidParamsException":
                return handleMessageException(HttpStatus.BAD_REQUEST, exception.getMessage());
            case "app.exception.DataNotFoundException":
                return handleMessageException(HttpStatus.NOT_FOUND, exception.getMessage());
            case "app.exception.FilterParamException":
                return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
            default:
                return new ResponseEntity<>(ResponseBuilder.buildErrorResponse("Unexpected Error: " + exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> handleMessageException(HttpStatus statusCode, String... messages) {
        return new ResponseEntity<>(ResponseBuilder.buildErrorResponse(messages), statusCode);
    }

    private ResponseEntity<String> handleValidationException(Exception e) {
        ValidationException exception = (ValidationException) e;
        List<String> errorList = new ArrayList<>();

        for (ConstraintViolation<StudyGroup> violation: exception.getViolation()){
            String message = "Field " + fieldsDescription.get(violation.getPropertyPath().toString()) + " " + violation.getMessage();
            errorList.add(message);
        }

        return new ResponseEntity<>(ResponseBuilder.buildErrorResponse(errorList.toArray(new String[0])), HttpStatus.BAD_REQUEST);
    }

}
