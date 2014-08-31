package chookin.etl.web.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;

public class Table {
	class Schema {
		List<TableHeading> headings = new ArrayList<TableHeading>();

		public Schema setHeadings(Collection<TableHeading> headings) {
			this.headings.clear();
			this.headings.addAll(headings);
			return this;
		}

		@Override
		public boolean equals(Object rhs) {
			if (rhs == null) {
				return false;
			}
			if (rhs instanceof Schema) {
				Schema schema = (Schema) rhs;
				return this.headings.equals(schema.headings);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.headings.hashCode();
		}

		@Override
		public String toString() {
			return this.headings.toString();
		}
	}

	private static final Logger LOG = Logger.getLogger(Table.class);
	private String docUrl;

	public String getDocUrl() {
		return docUrl;
	}

	public Table setDocUrl(String url) {
		this.docUrl = url;
		return this;
	}

	private String docTitle;

	public String getDocTitle() {
		return this.docTitle;
	}

	public Table setDocTile(String docTitle) {
		this.docTitle = docTitle;
		return this;
	}

	private String name;

	public String getName() {
		return name;
	}

	public Table setCaption(String name) {
		this.name = name;
		return this;
	}

	private List<TableRow> rows = new ArrayList<TableRow>();

	public Table addRows(Collection<TableRow> rows) {
		for (TableRow row : rows) {
			this.addRow(row);
		}
		return this;
	}

	public Table addRow(TableRow row) {
		this.rows.add(row);
		return this;
	}

	public List<TableRow> getRows() {
		return this.rows;
	}

	@Override
	public Table clone() {
		try {
			Table tb = (Table) super.clone();
			return tb;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		if (this.rows.isEmpty()) {
			return this.docUrl.hashCode() * 1000 + this.name.hashCode() * 10;
		} else {
			return this.docUrl.hashCode() * 1000 + this.name.hashCode() * 10
					+ this.rows.size();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Table) {
			Table table = (Table) obj;
			if (table.rows.size() != this.rows.size()) {
				return false;
			}
			for (int i = 0; i < table.rows.size(); ++i) {
				if (table.rows.get(i) != this.rows.get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("{").append("\n");
		strb.append("name: ").append(this.name).append("\n");
		strb.append("doc_url: ").append(this.docUrl).append("\n");
		strb.append("doc_title: \'").append(this.docTitle).append("\'\n");

		for (TableRow row : this.rows) {
			strb.append(row).append("\n");
		}
		strb.append("}");
		return strb.toString();
	}

	public boolean isEmpty() {
		return this.rows.isEmpty();
	}

	/**
	 * @return true if this table's first row data are all table headings
	 */
	public boolean containstHeading() {
		if (this.rows.isEmpty()) {
			return false;
		}

		TableRow row = this.rows.get(0);
		for (TableData item : row) {
			if (item instanceof TableHeading) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return get row size, rows include the table heading row
	 */
	public int getRowSize() {
		return this.rows.size();
	}

	/**
	 * @return get column size
	 */
	public int getColSize() {
		int max = 0;
		for (TableRow row : this.rows) {
			if (row.size() > max) {
				max = row.size();
			}
		}
		return max;
	}

	public String toCSV() throws IOException {
		return new TableGrid(this).toCSV();
	}

	public String toHtml() {
		Document root = Jsoup.parse("<html/>");
		Element head = root.select("head").first();
		head.appendElement("title").text(this.docTitle);
		Element body = root.select("body").first();
		Element docUrl = body.appendElement("p");
		docUrl.attr("id", "doc-url").text(this.docUrl);
		Element table = body.appendElement("table");
		table.attr("border", "1");// to indicate the width of the border in
									// pixels.
		table.attr("summary", this.name);
		for (TableRow item : this.rows) {
			Element row = table.appendElement("tr");
			for (TableData subItem : item) {
				Element cell = null;
				if (subItem instanceof TableHeading) {
					TableHeading heading = (TableHeading) subItem;
					cell = row.appendElement("th")
							.attr("scope", heading.getSope().toString())
							.append(subItem.getValue());
				} else {
					cell = row.appendElement("td").append(subItem.getValue());
				}
				if (subItem.getColSpan() > 1) {
					cell.attr("colspan", Integer.toString(subItem.getColSpan()));
				}
				if (subItem.getRowSpan() > 1) {
					cell.attr("rowspan", Integer.toString(subItem.getRowSpan()));
				}
			}
		}
		return root.toString();
	}

	Schema getSchema() {
		if (this.rows.isEmpty()) {
			return new Schema();
		}
		List<TableHeading> headings = new ArrayList<TableHeading>();
		TableRow row = this.rows.get(0);
		for (TableData item : row) {
			if (item instanceof TableHeading) {
				headings.add((TableHeading) item);
			} else {
				LOG.warn(item + " is not a valid table heading");
				return new Schema();
			}
		}
		return new Schema();
	}

	@Deprecated
	public String toXml() {
		XMLTag doc = XMLDoc.newDocument(false).addRoot("table");
		doc.addTag("name").addText(this.name).addTag("doc_title")
				.addText(this.docTitle).addTag("doc_url").addText(this.docUrl);
		doc.addTag("table-data");
		for (TableRow item : this.rows) {
			XMLTag row = doc.addTag("tr");
			for (TableData subItem : item) {
				if (subItem instanceof TableHeading) {
					for (int i = 0; i < subItem.getColSpan(); ++i) {
						row.addTag("th").addText(subItem.getValue());
					}
				} else {
					for (int i = 0; i < subItem.getColSpan(); ++i) {
						row.addTag("td").addText(subItem.getValue());
					}
				}
			}
			row.gotoParent();
		}
		return doc.toString();
	}

}
