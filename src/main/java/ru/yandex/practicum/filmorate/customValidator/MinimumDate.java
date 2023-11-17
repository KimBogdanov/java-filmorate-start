package ru.yandex.practicum.filmorate.customValidator;

import javax.validation.Constraint;
import javax.validation.constraints.Past;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
@Past
public @interface MinimumDate {
    String message() default "Дата не должна быть раньше {minDate}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String minDate();
}
