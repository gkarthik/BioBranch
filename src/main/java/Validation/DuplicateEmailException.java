package Validation;

public class DuplicateEmailException extends Exception {

	public DuplicateEmailException(String message) {
		super(message);
	}
}