package chookin.etl.web.table;


public class TableHeading  extends TableData{
	public TableHeading(){
		
	}
	public TableHeading(TableData td){
		super.setValue(td.getValue()).setColSpan(td.getColSpan());
		if(td instanceof TableHeading){
			TableHeading th = (TableHeading)td;
			this.scope = th.scope;
		}
	}
	@Override
	public TableHeading setValue(String value) {
		super.setValue(value);
		return this;
	}
	@Override
	public TableHeading setColSpan(int colspan){
		super.setColSpan(colspan);
		return this;
	}
	@Override
	public TableHeading setRowSpan(int rowspan){
		super.setRowSpan(rowspan);
		return this;
	}
	private TableHeadingScope scope = TableHeadingScope.COL;
	public TableHeadingScope getSope(){
		return this.scope;
	}
	public TableHeading setTableHeading(TableHeadingScope scope){
		this.scope = scope;
		return this;
	}
	public TableHeading setTableHeading(String scope) throws IllegalArgumentException{
		scope = scope.trim();
		if(scope.isEmpty() || "row".equalsIgnoreCase(scope)){
			this.scope = TableHeadingScope.ROW;
		}else if("col".equalsIgnoreCase(scope)){
			this.scope = TableHeadingScope.COL;
		}else {
			throw new IllegalArgumentException("invalid th scope: "+scope);
		}
		return this;
	}
	@Override
	public int hashCode(){
		return this.getValue().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(!(obj instanceof TableHeading)){
			return false;
		}
		TableHeading th = (TableHeading)obj;
		return th.getValue() == this.getValue() && th.scope == this.scope;
	}
}
