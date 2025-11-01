package com.health_fitness.exception;

import java.time.LocalDate;

public class UpdateMealBeforeCurrentDateException extends RuntimeException {
    public UpdateMealBeforeCurrentDateException(LocalDate date) {
        super("The created date of meal is"+date.toString()+" before today");
    }
}
