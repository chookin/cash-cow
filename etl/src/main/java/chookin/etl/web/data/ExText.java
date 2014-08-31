package chookin.etl.web.data;

/**
 * text that contains format
 */
public class ExText {
	public static boolean isExTextElement(String elementName){
		if(elementName.contains("br") || elementName.contains("sub") || elementName.contains("sup")){
			return true;
		}
		return false;
	}
}
