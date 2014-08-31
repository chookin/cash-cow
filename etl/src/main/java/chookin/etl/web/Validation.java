package chookin.etl.web;

import javax.swing.text.JTextComponent;

import chookin.utils.StringUtil;

class Validation {
	/**
	 * validate JTextComponents not blank : null, empty, or only whitespace
	 * (" ", \r\n, \t, etc) </br><b>note:</b> please set name for every
	 * JTextComponent, default name is null
	 * 
	 * @param cntrs
	 *            JTextComponents to validate
	 * @throws ValidationException
	 *             if a JTextComponent is blank
	 */
	public static void notBlank(JTextComponent... cntrs)
			throws ValidationException {
		for (JTextComponent cntr : cntrs) {
			String text = cntr.getText();
			if (StringUtil.isBlank(text)) {
				throw new ValidationException(String.format(
						"text component '%s' cannot be blank!", cntr.getName()));
			}
		}
	}
}

class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
