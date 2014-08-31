package chookin.etl.common;

public class HtmlHelper {
	/**
	 * not implemented completely
	 * @param html
	 * @return
	 */
	@Deprecated
	private static String getInnerMostHtml(String html) {
		if (html == null || html.isEmpty()) {
			return "";
		}
		char ch[] = html.toCharArray();
		boolean isFindOpenAngle = false;
		boolean isFindMatchedCloseAngle = false;
		int indexCloseAngle = -1;
		int indexNextOpenAngle = -1; // index of next close neighbor '<' of '>'
		for (int i = 0; i < ch.length; ++i) {
			if (!isFindOpenAngle) {
				if (ch[i] == '<') {
					isFindOpenAngle = true;
				}
			} else {
				if (!isFindMatchedCloseAngle) {
					if (ch[i] == '>') {
						isFindMatchedCloseAngle = true;
						indexCloseAngle = i;
					}
				} else {
					if (ch[i] == '<') {
						if (indexCloseAngle + 1 == i) {
							isFindMatchedCloseAngle = false;
							indexCloseAngle = -1;
						} else {
							indexNextOpenAngle = i;
							break;
						}
					}
				}
			}
		}
		if (indexCloseAngle < indexNextOpenAngle) {
			return html.substring(indexCloseAngle + 1, indexNextOpenAngle);
		} else {
			return "";
		}
	}
}
