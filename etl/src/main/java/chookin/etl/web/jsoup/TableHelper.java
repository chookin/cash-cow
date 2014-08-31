package chookin.etl.web.jsoup;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import chookin.etl.web.data.ExText;
import chookin.etl.web.table.TableData;
import chookin.etl.web.table.TableHeading;

public class TableHelper {
	/**
	 * get all leaf tables
	 * 
	 * @param element
	 * @return if element is null, throw IllegalArgumentException
	 */
	public static Elements getLeafTables(Element element) {
		Validate.notNull(element);
		Elements leafTables = new Elements();
		for (Element item : element.select("table")) {
			if (item.select("table").size() > 1) {
				continue;
			}
			leafTables.add(item);
		}
		return leafTables;
	}

	/**
	 * get table name. Priority: thead > summary > id > notEmptyPreviousSibling
	 * 
	 * @param tableElem
	 * @return
	 */
	public static String getTableCaption(Element tableElem) {
		if (tableElem == null) {
			return "";
		}
		String caption;
		// fetch the first td in thead. if exists valid text, return the its
		// text as caption
		Elements theads = tableElem.select("thead");
		if (!theads.isEmpty()) {
			Elements theadRows = theads.select("tr");
			if (!theadRows.isEmpty()) {
				Elements ths = theadRows.select("th, td");
				if (!ths.isEmpty()) {
					caption = getExText(ths.first());
					if (!caption.isEmpty()) {
						return caption;
					}
				}
			}
		}
		caption = tableElem.attr("summary");
		if (!caption.isEmpty()) {
			return caption;
		}
		caption = tableElem.attr("id");
		if (!caption.isEmpty()) {
			return caption;
		}
		Node prevNode = getNotEmptyPreviousSibling(tableElem);
		if (prevNode != null) {
			caption = getExText(prevNode);
		}
		return caption;
	}

	/**
	 * get table column headings by HTML mark when tagname = "th" and attribute
	 * scope="col"
	 * 
	 * @param elements
	 * @return
	 */
	public static List<TableHeading> getTableColHeading(Elements elements) {
		List<TableHeading> ths = new ArrayList<TableHeading>();
		for (Element item : elements) {
			if (item.tagName() != "th") {
				continue;
			}
			String scope = item.attr("scope");
			if (scope.isEmpty() || "col".equalsIgnoreCase(scope)) {
				ths.add(new TableHeading(getTableData(item)));
			}
		}
		return ths;
	}

	public static List<TableData> getTableData(Elements elements) {
		List<TableData> tds = new ArrayList<TableData>();
		for (Element item : elements) {
			tds.add(getTableData(item));
		}
		return tds;
	}

	/**
	 * get table data by HTML mark when tagname = "th" or tagname = "td"
	 * 
	 * @param element
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static TableData getTableData(Element element)
			throws IllegalArgumentException {
		String tagName = element.tagName();
		if (tagName != "th" && tagName != "td") {
			return null;
		}

		int colspan = 1;
		String strColSpan = element.attr("colspan");
		if (strColSpan.isEmpty()) {
		} else {
			colspan = Integer.parseInt(strColSpan);
			Validate.isTrue(colspan > 0, "colspan must be bigger than zero");
		}

		int rowspan = 1;
		String strRowSpan = element.attr("rowspan");
		if (!strRowSpan.isEmpty()) {
			rowspan = Integer.parseInt(strRowSpan);
			Validate.isTrue(rowspan > 0, "rowspan must be bigger than zero");
		}
		String inner = getExText(element);

		if ("th".equalsIgnoreCase(tagName)) {
			String scope = element.attr("scope");
			return new TableHeading().setValue(inner).setColSpan(colspan)
					.setRowSpan(rowspan).setTableHeading(scope);
		} else {
			return new TableData().setValue(inner).setColSpan(colspan)
					.setRowSpan(rowspan);
		}
	}

	private static String getNodeText(Node node) {
		String html = node.toString().trim();
		if (html.isEmpty()) {
			return "";
		}
		StringBuilder strb = new StringBuilder();
		if (node instanceof Element) {
			if (ExText.isExTextElement(((Element) node).tagName())) {
				strb.append(html);
			} else {
				strb.append(getExText(node));
			}
		} else if (node instanceof Comment) {
			// ignored
		} else {
			strb.append(html);
		}
		return strb.toString();
	}

	/**
	 * get rich text (compare to plain text, support HTML tag "sub", "sup", "br"
	 * )
	 * 
	 * @param node
	 * @return
	 */
	public static String getExText(Node node) {
		if (node == null) {
			return "";
		}
		List<Node> myNodes = getNodesContainNonElementNode(node);
		StringBuilder strb = new StringBuilder();
		for (Node item : myNodes) {
			List<Node> subNodes = item.childNodes();
			if (subNodes.isEmpty()) {
				strb.append(getNodeText(item));
			} else {
				for (Node subItem : subNodes) {
					strb.append(getNodeText(subItem));
				}
			}
		}
		return strb.toString();
	}

	public static List<Node> getNodesContainNonElementNode(Node node) {
		Validate.notNull(node);
		List<Node> rst = new ArrayList<Node>();
		if (!(node instanceof Element)) {
			rst.add(node);
			return rst;
		}
		Element element = (Element) node;
		Elements children = new Elements();
		for (Element item : element.children()) {
			if (item.toString().trim().isEmpty()) {
				continue;
			}
			children.add(item);
		}
		List<Node> myNodes = new ArrayList<Node>();
		for (Node item : node.childNodes()) {
			if (item.toString().trim().isEmpty()) {
				continue;
			}
			myNodes.add(item);
		}
		if (myNodes.size() != children.size()) {// stand for existing other node
												// except for element
			rst.add(node);
			return rst;
		}
		for (Element item : children) {
			List<Node> itemInner = getNodesContainNonElementNode(item);
			if (itemInner.isEmpty()) {
				if (item.tagName().equalsIgnoreCase("p")) {// convert empty
															// <p></p> to <br
															// />
					itemInner
							.add(new Element(Tag.valueOf("br"), item.baseUri()));
				}
			}
			rst.addAll(itemInner);
		}
		return rst;
	}

	@Deprecated
	public static String getInnerMostHtml(Node node) {
		Validate.notNull(node);
		List<Node> myNodes = getNodesContainNonElementNode(node);
		List<String> inners = new ArrayList<String>();
		StringBuilder strb = new StringBuilder();
		for (Node item : myNodes) {
			List<Node> subNodes = item.childNodes();
			for (Node subItem : subNodes) {
				String html = subItem.toString().trim();

				if (!html.isEmpty()) {
					inners.add(html);
				}
			}
		}
		if (inners.size() > 1) {
			for (String item : inners) {
				strb.append("{").append(item).append("}, ");
			}
			strb.delete(strb.length() - 2, strb.length());
		} else if (!inners.isEmpty()) {
			strb.append(inners.get(0));
		}
		return strb.toString();
	}

	public static Node getNotEmptyPreviousSibling(Node node) {
		Node prevNode = node.previousSibling();
		while (prevNode != null && prevNode.toString().trim().isEmpty()) {
			prevNode = prevNode.previousSibling();
		}
		return prevNode;
	}
}
