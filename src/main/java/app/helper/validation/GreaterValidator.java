package app.helper.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GreaterValidator implements ConstraintValidator<GreaterThan, Float> {
    private Float value;

    @Override
    public void initialize(GreaterThan constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Float number, ConstraintValidatorContext context) {
        boolean isValid = number != null && number > value;

        if ( !isValid ) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("должно иметь значение и должно быть больше чем " + value).addConstraintViolation();
        }

        return isValid;
    }
}
