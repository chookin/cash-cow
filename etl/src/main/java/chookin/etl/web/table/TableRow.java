package chookin.etl.web.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TableRow implements Iterable<TableData> {
	private List<TableData> cells = new ArrayList<TableData>();

	public int size() {
		return this.cells.size();
	}

	public TableRow Filled(Collection<TableData> cells) {
		this.cells.clear();
		this.cells.addAll(cells);
		return this;
	}

	@Override
	public int hashCode(){
		if(this.cells.isEmpty()){
			return 0;
		}
		return this.cells.size() * (1 + this.cells.iterator().next().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TableRow) {
			TableRow row = (TableRow) obj;
			if (this.size() != row.size()) {
				return false;
			} else {
				return this.toString() == row.toString();
			}
		}
		return false;
	}

	/*
	 * warning: not processing rowspan
	 */
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("{");
		for (TableData cell : this.cells) {
			strb.append(cell).append(", ");
		}
		if (!this.cells.isEmpty()) {
			strb.delete(strb.length() - 2, strb.length());
		}
		strb.append("}");
		return strb.toString();
	}

	@Override
	public Iterator<TableData> iterator() {
		return new Iterator<TableData>() {
			private Iterator<TableData> iter = TableRow.this.cells.iterator();

			public boolean hasNext() {
				return this.iter.hasNext();
			}

			public TableData next() {
				return this.iter.next();
			}

			public void remove() {
				this.iter.remove();
			}
		};
	}
}
