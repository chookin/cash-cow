package chookin.etl.web.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.helper.Validate;

import chookin.etl.web.table.Table.Schema;


public class TableMerger {
	private TableMerger(){}
	public static Table mergeTable(Table tb1, Table tb2){
		Validate.notNull(tb1);
		Validate.notNull(tb2);
		if(!tb1.getSchema().equals(tb2.getSchema())){
			return null;
		}
		Table rst = tb1.clone();
		rst.addRows(tb2.getRows());
		return rst;
	}
	
	public static Map<Schema, Table> mergeTable(Set<Table> tables){
		Validate.notNull(tables);
		Map<Schema, Table> rst = new HashMap<Table.Schema, Table>(); 
		for(Table table : tables){
			Schema schema = table.getSchema();
			Table merged = rst.get(schema);
			if(merged == null){
				rst.put(schema, table);
			}else{
				merged = mergeTable(merged, table);
				rst.put(schema, merged);
			}
		}
		return rst;
	}
}
