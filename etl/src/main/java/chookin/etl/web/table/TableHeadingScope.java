package chookin.etl.web.table;

public enum TableHeadingScope {
	ROW("row"),
	COL("col");// notice ";"
	TableHeadingScope(String name) {
		this.name = name;
	}
	private String name;
	@Override
	public String toString(){
		return this.name;
	}

}
