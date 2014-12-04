package org.scripps.branch.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.scripps.branch.controller.FileUploadController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordsNotEmptyValidator implements
		ConstraintValidator<PasswordsNotEmpty, Object> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PasswordsNotEmptyValidator.class);
	
	private String passwordFieldName;
	private String passwordVerificationFieldName;
	private String validationTriggerFieldName;

	@Override
	public void initialize(PasswordsNotEmpty constraintAnnotation) {
		validationTriggerFieldName = null;
		if(constraintAnnotation.triggerFieldName()!=null){
			LOGGER.debug(constraintAnnotation.triggerFieldName());
			validationTriggerFieldName = constraintAnnotation.triggerFieldName();
		}
		passwordFieldName = constraintAnnotation.passwordFieldName();
		passwordVerificationFieldName = constraintAnnotation
				.passwordVerificationFieldName();
	}

	private boolean isNullOrEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		try {
			Object validationTrigger = null;
			if(validationTriggerFieldName!=null){
				validationTrigger = ValidatorUtil.getFieldValue(value,
						validationTriggerFieldName);
			}
			if (validationTrigger == null) {
				return passwordFieldsAreValid(value, context);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Exception occurred during validation",
					ex);
		}
		return true;
	}

	private boolean passwordFieldsAreValid(Object value,
			ConstraintValidatorContext context) throws NoSuchFieldException,
			IllegalAccessException {
		boolean passwordWordFieldsAreValid = true;

		String password = (String) ValidatorUtil.getFieldValue(value,
				passwordFieldName);
		if (isNullOrEmpty(password)) {
			ValidatorUtil.addValidationError(passwordFieldName, context);
			passwordWordFieldsAreValid = false;
		}

		String passwordVerification = (String) ValidatorUtil.getFieldValue(
				value, passwordVerificationFieldName);
		if (isNullOrEmpty(passwordVerification)) {
			ValidatorUtil.addValidationError(passwordVerificationFieldName,
					context);
			passwordWordFieldsAreValid = false;
		}

		return passwordWordFieldsAreValid;
	}
}