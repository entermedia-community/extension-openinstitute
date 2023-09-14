import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';

import '../openinsitute_core.dart';
import 'emDataManager.dart';

class AssetsManager {
  String assestString = 'assets';

  OpenI get oi {
    return Get.find();
  }

  DataModule? module;

  createDataModule() async {
    module =
        await oi.datamanager.getDataModule("asset", boxString: assestString);
  }

  Future<List<emData>> search(int page,
      {String des = '*', List<Map<String, String>>? terms}) async {
    await createDataModule();
    return await module!.getRemoteData({
      "page": "$page",
      "hitsperpage": "20",
      'sortby': 'assetaddeddateDown',
      "query": {
        "terms": [
          {"field": "description", "operator": "freeform", "value": des},
          if (terms != null && terms.length > 0) ...terms
        ]
      }
    }, false);
  }
}
