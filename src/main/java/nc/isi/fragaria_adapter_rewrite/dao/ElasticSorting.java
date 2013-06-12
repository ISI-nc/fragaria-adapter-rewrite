package nc.isi.fragaria_adapter_rewrite.dao;

import org.elasticsearch.search.sort.SortOrder;

public class ElasticSorting {
	private final String field;
	private final SortOrder sortOrder;
	
	public ElasticSorting(String field, SortOrder sortOrder) {
		super();
		this.field = field;
		this.sortOrder = sortOrder;
	}

	public String getField() {
		return field;
	}
	
	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
