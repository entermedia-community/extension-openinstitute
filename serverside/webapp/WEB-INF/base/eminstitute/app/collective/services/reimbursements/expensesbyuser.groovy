import org.elasticsearch.search.aggregations.AggregationBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder
import org.openedit.data.Searcher
import org.openedit.hittracker.HitTracker
import org.openedit.hittracker.SearchQuery

public void init()
{
	String collectionid = context.getRequestParameter("collectionid");

	Searcher searcher = mediaarchive.getSearcher("collectiveexpense");
	SearchQuery query = searcher.addStandardSearchTerms(context);

	if( query == null)
	{
		query = searcher.query().exact("collectionid",collectionid).exact("reimbursedstatus","1").sort("dateDown").getQuery();
	}

	query.setHitsName("reimbursements");
	
	String name = "pendingexpensesreimburseuser_total";
	
	AggregationBuilder b = AggregationBuilders.terms(name).field("reimburseuser");
	SumBuilder sum = new SumBuilder("total_sum");
	sum.field("total");
	b.subAggregation(sum);
	query.setAggregation(b);
	query.setHitsPerPage(50);
	HitTracker hits = searcher.search(query);
	hits.getActiveFilterValues();
	Map currencymap = hits.getAggregationMap(name);
	context.putPageValue(name,currencymap);
	context.putPageValue("expenses",hits);
	
	context.putPageValue("searcher", searcher);
	context.putPageValue(hits.getHitsName(), hits);
	
}

init();
	