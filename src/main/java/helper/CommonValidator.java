package helper;

import exception.InvalidParamsException;
import helper.common.ErrorMessages;

public class CommonValidator {
    public static long validateId(String urlInfo) throws InvalidParamsException {
        try {
            long result = Long.parseLong(urlInfo.replace("/", ""));
            if (result <= 0)
                throw new InvalidParamsException(ErrorMessages.INVALID_ID);

            return result;
        } catch (NumberFormatException e) {
            throw new InvalidParamsException(ErrorMessages.INVALID_ID);
        }
    }
}
