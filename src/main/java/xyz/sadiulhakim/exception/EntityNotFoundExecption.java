package xyz.sadiulhakim.exception;

public class EntityNotFoundExecption extends RuntimeException {

	private static final long serialVersionUID = -5228575158133634057L;

	public EntityNotFoundExecption(String message) {
		super(message);
	}
}
