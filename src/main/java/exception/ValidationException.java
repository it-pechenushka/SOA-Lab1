package exception;

import lombok.Getter;
import model.StudyGroup;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Getter
public class ValidationException extends Exception{
    private Set<ConstraintViolation<StudyGroup>> violation;

    public ValidationException(Set<ConstraintViolation<StudyGroup>> violation) {
        this.violation = violation;
    }

}
