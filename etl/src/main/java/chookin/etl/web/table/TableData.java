package chookin.etl.web.table;

public class TableData {
	private String value;

	public String getValue() {
		return value;
	}

	public TableData setValue(String value) {
		this.value = value;
		return this;
	}

	private int colSpan = 1;

	public int getColSpan() {
		return this.colSpan;
	}

	public TableData setColSpan(int colspan) {
		this.colSpan = colspan;
		return this;
	}

	private int rowSpan = 1;

	public int getRowSpan() {
		return this.rowSpan;
	}

	public TableData setRowSpan(int rowspan) {
		this.rowSpan = rowspan;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TableData) {
			TableData cell = (TableData) obj;
			return cell.value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	/*
	 * warning: not processing rowspan
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String trimed = this.value.trim();
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < this.colSpan; ++i) {
			strb.append("{").append(trimed).append("}, ");
		}
		strb.delete(strb.length() - 2, strb.length());
		return strb.toString();
	}
}
