import 'package:openinsitute_core/services/emDataManager.dart';

class FinanceManager {
  DataModule? module;

  createDataModule(String modulestring, {String? boxString}) async {
    module =
        await oi.datamanager.getDataModule(modulestring, boxString: boxString);
  }

  addBankDetails(String projectId, Map inQuery) async {
    await initBankDetails(projectId);
    return await addData(inQuery);
  }

  bankDetailsCache(String projectId) async {
    await initBankDetails(projectId);
    return module!.getAllHits();
  }

  initBankDetails(String projectId) async {
    await createDataModule("bankaccount", boxString: "bankaccount_$projectId");
  }

  getBankDetails(String projectId, int page) async {
    await initBankDetails(projectId);
    return await getData(projectId, page);
  }

  incomeCache(String projectId) async {
    await initIncome(projectId);
    return module!.getAllHits();
  }

  invoicesCache(String projectId) async {
    await initInvoices(projectId);
    return module!.getAllHits();
  }

  expencesCache(String projectId) async {
    await initExpenses(projectId);
    return module!.getAllHits();
  }

  reimbursementsCache(String projectId) async {
    await initReimbursements(projectId);
    return module!.getAllHits();
  }

  onlineDonationsCache(String projectId) async {
    await initOnlineDonations(projectId);
    return module!.getAllHits();
  }

  // TODO: Cash Income.
  initIncome(String id) async {
    await createDataModule("collectiveincome",
        boxString: "collectiveincome_$id");
  }

  getIncome(String id, int page) async {
    await initIncome(id);
    return await getData(id, page);
  }

  getInvoices(String id, int page) async {
    await initInvoices(id);
    return await getData(id, page);
  }

  getExpences(String id, int page) async {
    await initExpenses(id);
    return await getData(id, page);
  }

  getReimbursements(String id, int page) async {
    await initReimbursements(id);
    return await getData(id, page);
  }

  getOnlineDonations(String id, int page) async {
    await initOnlineDonations(id);
    return await getData(id, page);
  }

  getData(String id, int page) async {
    return await module!.getRemoteData({
      "page": "$page",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "collectionid", "operator": "matches", "value": id}
        ]
      }
    }, true);
  }

  addIncome(String id, Map inQuery) async {
    await initIncome(id);
    return await addData(inQuery);
  }

  addInvoices(String projectId, Map inQuery) async {
    await initInvoices(projectId);
    return await addData(inQuery);
  }

  addExpences(String projectId, Map inQuery) async {
    await initExpenses(projectId);
    return await addData(inQuery);
  }

  addReimbursements(String projectId, Map inQuery) async {
    await initReimbursements(projectId);
    return await addData(inQuery);
  }

  addOnlineDonations(String projectId, Map inQuery) async {
    await initOnlineDonations(projectId);
    return await addData(inQuery);
  }

  addData(Map inQuery) async {
    return await module!.addData(inQuery);
  }

  editIncome(String projectId, String id, Map inQuery) async {
    await initIncome(projectId);
    return await editData(id, inQuery);
  }

  editExpences(String projectId, String id, Map inQuery) async {
    await initExpenses(projectId);
    return await editData(id, inQuery);
  }

  editInvoices(String projectId, String id, Map inQuery) async {
    await initInvoices(projectId);
    return await editData(id, inQuery);
  }

  editOnlineDonations(String projectId, String id, Map inQuery) async {
    await initOnlineDonations(projectId);
    return await editData(id, inQuery);
  }

  editReimbursements(String projectId, String id, Map inQuery) async {
    await initOnlineDonations(projectId);
    return await editData(id, inQuery);
  }

  editData(String id, Map inQuery) async {
    return await module!.updateData(id, inQuery);
  }

  deleteIncome(String projectId, String id) async {
    await initIncome(projectId);
    return await deleteData(id);
  }

  deleteInvoices(String projectId, String id) async {
    await initInvoices(projectId);
    return await deleteData(id);
  }

  deleteReimbursements(String projectId, String id) async {
    await initReimbursements(projectId);
    return await deleteData(id);
  }

  deleteExpences(String projectId, String id) async {
    await initExpenses(projectId);
    return await deleteData(id);
  }

  deleteOnlineDonations(String projectId, String id) async {
    await initOnlineDonations(projectId);
    return await deleteData(id);
  }

  deleteData(String id) async {
    return await module!.deleteData(id);
  }

  // TODO: Project Wallet.
  // TODO: Transfers.
  // TODO: Work Points.
  // TODO: Invoices.
  initInvoices(String id) async {
    await createDataModule("collectiveinvoice",
        boxString: "collectiveinvoice_$id");
  }

  // TODO: Expenses.
  initExpenses(String id) async {
    await createDataModule("collectiveexpense",
        boxString: "collectiveexpense_$id");
  }

  // TODO: Reimbursements.
  initReimbursements(String id) async {
    await createDataModule("collectivereimbursement",
        boxString: "collectivereimbursement_$id");
  }

  initOnlineDonations(String id) async {
    await createDataModule("transaction", boxString: "transaction_$id");
  }
  // TODO: Investments.
}
