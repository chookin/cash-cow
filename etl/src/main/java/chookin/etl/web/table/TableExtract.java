package chookin.etl.web.table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chookin.etl.web.jsoup.LinkHelper;
import chookin.etl.web.jsoup.TableHelper;

public class TableExtract {

    /**
     * extract tables from jsoup element
     * @param doc jsoup element, may be jsoup document
     * @param title element's title
     * @return extracted tables
     * @throws IOException
     */
	public static List<Table> fromElement(Element doc, String title) throws IOException {
		Validate.notNull(doc);
		Elements leafTables = TableHelper.getLeafTables(doc);
		List<Table> tables = new ArrayList<Table>();
		for (Element table : leafTables) {
			String tableCaption = TableHelper.getTableCaption(table);
			Table dataTable = new Table();
			dataTable.setCaption(tableCaption);
			dataTable.setDocUrl(doc.baseUri());
			dataTable.setDocTile(title);

			for (Element row : table.select("tr")) {
				Elements tdAndTh = row.select("td, th");
				Elements myTdAndTh = new Elements();
				// filter empty row
				for (Element item : tdAndTh) {
					if (item.toString().trim().isEmpty()) {
						continue;
					}
					myTdAndTh.add(item);
				}
				if (myTdAndTh.isEmpty()) {
					continue;
				}
				List<TableData> cells = new ArrayList<TableData>();
				for (Element element : myTdAndTh) {
					cells.add(TableHelper.getTableData(element));
				}
				if (!cells.isEmpty()) {
					dataTable.addRow(new TableRow().Filled(cells));
				}
			}
			tables.add(dataTable);
		}
		return tables;
	}

    /**
     * extract table from the web page identified by the url
     * @param url
     * @return
     * @throws IOException
     */
	public static List<Table> fromUrl(String url) throws IOException {
		Document doc = LinkHelper.getDocument(url);
		return fromElement(doc, doc.title());
	}
    /**
	get tables from file
    @param filename          name of the file to load HTML from
    @param charsetName (optional) character set of file contents. Set to {@code null} to determine from {@code http-equiv} meta tag, if
    present, or fall back to {@code UTF-8} (which is often safe to do).
    @return tables find in the file

    @throws IOException if the file could not be found, or read, or if the charsetName is invalid.
    */
	public static List<Table> fromFile(String filename, String charsetName) throws IOException{
		Document doc = Jsoup.parse(new File(filename), charsetName );
		return fromElement(doc, doc.title());
	}
	public static List<Table> fromFile(String filename, String charsetName, String baseUri) throws IOException{
		Document doc = Jsoup.parse(new File(filename), charsetName, baseUri);
		return fromElement(doc, doc.title());
	}
}
