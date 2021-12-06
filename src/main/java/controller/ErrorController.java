package controller;

import exception.ValidationException;
import helper.ResponseBuilder;
import helper.common.ErrorMessages;
import model.StudyGroup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.*;

public class ErrorController extends HttpServlet {
    private static Map<String, String> fieldsDescription  = new HashMap<String, String>() {{
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
    protected void handleException(HttpServletResponse response,
                                     Throwable throwable) throws IOException {
        String errorName = throwable.getClass().getName();
        switch (errorName) {
            case "javax.xml.bind.UnmarshalException":
                throwable.printStackTrace();
                handleMessageException(response, HttpServletResponse.SC_BAD_REQUEST, ErrorMessages.WARNING_INTEGER, ErrorMessages.WARNING_FLOAT);
                break;
            case "exception.ValidationException":
                handleValidationException(response, throwable);
                break;
            case "exception.InvalidParamsException":
                handleMessageException(response, HttpServletResponse.SC_BAD_REQUEST, throwable.getMessage());
                break;
            case "exception.DataNotFoundException":
                handleMessageException(response, HttpServletResponse.SC_NOT_FOUND, throwable.getMessage());
                break;
            case "exception.FilterParamException":
                response.getWriter().write(throwable.getMessage());
                break;

        }
    }

    private void handleMessageException(HttpServletResponse response, int statusCode, String... messages) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(ResponseBuilder.buildErrorResponse(messages));
    }

    private void handleValidationException(HttpServletResponse response, Throwable throwable) throws IOException {
        ValidationException exception = (ValidationException) throwable;
        List<String> errorList = new ArrayList<>();

        for (ConstraintViolation<StudyGroup> violation: exception.getViolation()){
            String message = "Field " + fieldsDescription.get(violation.getPropertyPath().toString()) + " " + violation.getMessage();
            errorList.add(message);
        }

        response.getWriter().write(ResponseBuilder.buildErrorResponse(errorList.toArray(new String[0])));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        handleException(resp, throwable);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
