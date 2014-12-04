package org.scripps.branch.validation;

public class DuplicateEmailException extends Exception {

	public DuplicateEmailException(String message) {
		super(message);
	}
}