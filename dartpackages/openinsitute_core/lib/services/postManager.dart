
import 'package:get/get.dart';
import 'package:openinsitute_core/models/emFeeds.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

OpenI get oi {
  return Get.find();
}

class FeedsManager {
  Future<List<emFeed>> getFeeds() async{
    Feeds feeds = await oi.datamanager.getFeeds();
    feeds.syncData(true);
    return feeds.getAllHits();
  }
}
