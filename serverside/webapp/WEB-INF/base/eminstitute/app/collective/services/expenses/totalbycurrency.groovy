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
	//log.info("ID1: ${hits.indexId} ${hits.getAggregations()} " );
	
	hits.getActiveFilterValues();

	log.info(searcher.getSearchType() + " " + query.toString() + " Found " + hits.size());
	//log.info("ID2: ${hits.indexId} ${hits.getAggregations()} " );
	Map currencymap = hits.getAggregationMap("currencytype_total");
	
//	Aggregations all = hits.getAggregations();
//	if( all != null)
//	{
//		org.elasticsearch.search.aggregations.bucket.terms.Terms terms = all.get("currencytype_total");
//		Collection currencytotals = terms.getBuckets();
//		log.info(currencytotals);
//		for(bucket in currencytotals)
//		{
//			log.info("Bucket: ${bucket.getKey()}");
//			for(subtotal in bucket.getAggregations())
//			{
//				log.info("ID2: ${subtotal} ${subtotal}");
//			}		
//		}
//		
////		#foreach($item in
////			$diskspacehits.getAggregations().get("fileformat_filesize").getBuckets())
////			#foreach($subitem in $item.getAggregations())
//
//		
//		context.putPageValue("currencytotals",currencytotals);
//		//Collection currencytotals = all.getBuckets();
//		//context.putPageValue("currencytotals",currencytotals);
//	}
	context.putPageValue("currencytotals",currencymap);
	context.putPageValue("searcher", searcher);
	context.putPageValue("expenses", hits);
	
	/*	
	#foreach($currency in $currencytotals)
		#foreach($subitem in $currency.getAggregations())
			"$item.key" = $subitem.getValue()
		#end
	#end
	*/
	
	
//	context.putPageValue("diskspacehits", hits)
//	context.putPageValue("hits", hits)
	
//	log.info("hits" + hits.size());
	
}

init();
	
