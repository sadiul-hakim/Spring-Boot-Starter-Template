package xyz.sadiulhakim.exception;

public class TokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = 1327197789919358869L;

	public TokenExpiredException(String msg) {
		super(msg);
	}
}
