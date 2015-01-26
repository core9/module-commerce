package io.core9.commerce.cart;

public class CartException extends Exception {
	
	private static final long serialVersionUID = 8309731180625481243L;

	private Object[] args;

	public CartException(String string, Object... args) {
		super(string);
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

}
