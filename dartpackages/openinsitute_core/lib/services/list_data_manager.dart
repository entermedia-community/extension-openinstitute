import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/services/emDataManager.dart';






class ListDataManager {
  ListDataManager(this.modulestring, {this.boxString});
  String modulestring;
  String? boxString;

  late DataModule module;

  createDataModule() async {
    module =
        await oi.datamanager.getDataModule(modulestring, boxString: boxString);
  }

  getCache() async {
    await createDataModule();
    List<emData> hits = module.getAllHits();
    if (hits.isEmpty) {
      hits = await load(1);
    }
    return hits;
  }

  load(int page) async {
    await createDataModule();
    List<emData> hits = await module.getRemoteData({
      "page": "$page",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operator": "matches", "value": "*"}
        ]
      }
    }, true);
    return hits;
  }

  add(Map<dynamic, dynamic> inQuery) async {
    await createDataModule();
    emData hit = await module.addData(inQuery);
    await load(1);
    return hit;
  }
}
