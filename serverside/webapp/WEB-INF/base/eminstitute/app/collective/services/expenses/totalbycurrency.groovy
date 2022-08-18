import org.elasticsearch.search.aggregations.AggregationBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder
import org.openedit.data.Searcher
import org.openedit.hittracker.HitTracker
import org.openedit.hittracker.SearchQuery

public void init()
{
	String collectionid = context.getRequestParameter("collectionid");

	Searcher searcher = mediaarchive.getSearcher(context.findValue("searchtype"));
	SearchQuery query = searcher.addStandardSearchTerms(context);

	if( query == null)
	{
		query = searcher.query().exact("collectionid",collectionid).named("expenses").sort("dateDown").getQuery();
	}

	AggregationBuilder b = AggregationBuilders.terms("currencytype_total").field("currencytype");
	SumBuilder sum = new SumBuilder("total_sum");
	sum.field("total");
	b.subAggregation(sum);
	query.setAggregation(b);
	query.setHitsPerPage(50);
	HitTracker hits = searcher.search(query);
	//hits.enableBulkOperations();  //Breaks aggregations, when logging all searches
	hits.getActiveFilterValues();
	Map currencymap = hits.getAggregationMap("currencytype_total");
	context.putPageValue("currencytype_total",currencymap);
	context.putPageValue("searcher", searcher);
	context.putPageValue("expenses", hits);
	
}

init();
	
