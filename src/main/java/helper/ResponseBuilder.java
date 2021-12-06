package helper;

public class ResponseBuilder {
    public static String buildErrorResponse(String... msg){
        return String.format("<errorArray>%s</errorArray>", buildError(msg));
    }

    private static String buildError(String... msg){
        StringBuilder errors = new StringBuilder();
        for (String s: msg)
            errors.append(String.format("<error>%s</error>", s));

        return errors.toString();
    }

    public static String buildTextResponse(String msg){
        return String.format("<responseText>%s</responseText>", msg);
    }
}
