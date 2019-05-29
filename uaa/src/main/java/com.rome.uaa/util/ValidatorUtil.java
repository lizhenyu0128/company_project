package com.rome.uaa.util;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author lizhenyu
 */
public class ValidatorUtil {
  private static Validator validator = Validation.buildDefaultValidatorFactory()
    .getValidator();


  public static <T> void checkEntity(T object) {

    Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
    System.out.println(constraintViolations);
    if (!constraintViolations.isEmpty()) {
      throw new RuntimeException(constraintViolations.iterator().next().getMessage());
    }

  }
}
