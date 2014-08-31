package chookin.etl.web.table;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class TableGrid {
	private Object[][] rows;
	private int rowsize;
	private int colsize;

	public TableGrid(int rowsize, int colsize) {
		this.rows = new Object[rowsize][colsize];
		this.rowsize = rowsize;
		this.colsize = colsize;
	}

	public TableGrid(Table table) {
		this(table.getRowSize(), table.getColSize());
		Collection<TableRow> rows = table.getRows();
		int indexRow = -1;
		for (TableRow tr : rows) {
			++indexRow;
			int indexCol = -1;
			for (TableData td : tr) {
				int myIndexRow = indexRow;
				for (int ir = 0; ir < td.getRowSpan(); ++ir) {
					Object[] myRowData = this.rows[myIndexRow];
					for (int i = 0; i < td.getColSpan(); ++i) {
						while (myRowData[++indexCol] != null) {
							// empty running to find null position
						}
						myRowData[indexCol] = td.getValue();
					}
					++myIndexRow;
				}
			}
		}
	}

	public Object getValue(int rowindex, int colindex) {
		if (rowindex >= this.rowsize) {
			throw new IllegalArgumentException(String.format(
					"rowindex %d is not less than rowsize %d", rowindex,
					this.rowsize));
		}
		if (colindex >= this.colsize) {
			throw new IllegalArgumentException(String.format(
					"colindex %d is not less than colsize %d", colindex,
					this.colsize));
		}
		return this.rows[rowindex][colindex];
	}

	public TableGrid setValue(int rowindex, int colindex, Object value) {
		if (rowindex >= this.rowsize) {
			throw new IllegalArgumentException(String.format(
					"rowindex %d is not less than rowsize %d", rowindex,
					this.rowsize));
		}
		if (colindex >= this.colsize) {
			throw new IllegalArgumentException(String.format(
					"colindex %d is not less than colsize %d", colindex,
					this.colsize));
		}
		this.rows[rowindex][colindex] = value;
		return this;
	}

	public String toCSV() throws IOException {
		Writer strw = new StringWriter();
		CsvListWriter csv = new CsvListWriter(strw,
				CsvPreference.EXCEL_PREFERENCE);
		for (Object[] item : this.rows) {
			csv.write(item);
		}
		csv.close();
		return strw.toString().trim();
	}
	public boolean isEmpty(){
		return this.rowsize == 0 || this.colsize == 0;
	}
	@Override
	public String toString() {
		if(isEmpty()){
			return "";
		}
		StringBuilder strb = new StringBuilder();
		for(Object[] item : this.rows){
			strb.append("{");
			for(Object subItem : item){
				strb.append("{").append(subItem).append("}, ");
			}
			strb.delete(strb.length() -2 , strb.length());
			strb.append("}").append("\n");
		}
		return strb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TableGrid)) {
			return false;
		}
		TableGrid rhs = (TableGrid) obj;
		return this.toString() == rhs.toString();
	}

	@Override
	public int hashCode() {
		return this.rowsize * 10000 + this.colsize;
	}
}
