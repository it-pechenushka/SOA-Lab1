package app.helper.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GreaterValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GreaterThan {
    String message() default "должно быть меньше чем";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    float value();
}
