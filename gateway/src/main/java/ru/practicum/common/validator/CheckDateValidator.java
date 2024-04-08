package ru.practicum.common.validator;


import ru.practicum.booking.dto.NewBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<ValidateStartAndEndDate, NewBookingDto> {
    String message;

    @Override
    public void initialize(ValidateStartAndEndDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(NewBookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return end.isAfter(start) && !end.isEqual(start) &&
                !end.isEqual(LocalDateTime.now()) && start.isAfter(LocalDateTime.now());
    }
}