package billing;

import org.openedit.util.DateStorageUtil
import org.entermediadb.asset.MediaArchive
import org.openedit.*
import org.openedit.data.Searcher
import org.openedit.users.User

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");

	Searcher transactionSearcher = mediaArchive.getSearcher("transaction");

	Collection pendingNotification = transactionSearcher.query()
									.match("receiptstatus","new").search();
	
	for (Iterator receiptIterator = pendingNotification.iterator(); receiptIterator.hasNext();) {
		Data receipt = transactionSearcher.loadData(receiptIterator.next());
		receipt.setValue("receiptstatus", "");
		transactionSearcher.saveData(receipt);

	}
	log.info(pendingNotification.size() + " receipts updated");
}



init();
