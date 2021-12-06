package exception;

import helper.ResponseBuilder;

public class FilterParamException extends Exception{
    public FilterParamException(String... messages){
        super(ResponseBuilder.buildErrorResponse(messages));
    }
}
