//package ru.yandex.practicum.filmorate.customValidator;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//
//import java.time.LocalDate;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//class MinimumDateValidatorTest {
//    private static Validator validator;
//
//    static {
//        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
//        validator = validatorFactory.usingContext().getValidator();
//    }
//
//    @Test
//    public void testValidators() {
//        final Film film = new Film(12L, "Черные жабки", "Описание", LocalDate.of(1996, 1, 1), 45L);
//
//        Set<ConstraintViolation<Film>> validates = validator.validate(film);
//        validates.stream().map(v -> v.getMessage())
//                .forEach(System.out::println);
//        assertTrue(validates.size() > 0);
//
//    }
//
//}