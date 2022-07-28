import 'dart:developer';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:get/get.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

OpenI get oi {
  return Get.find();
}

class FeedManager {
  String feedsBox = "feed";

  DataModule? feedsModule;

  createFeedModule({String? collectionId}) async {
    if (collectionId != null) {
      feedsModule = await oi.datamanager
          .getDataModule("feed", boxString: "feed_$collectionId");
    } else {
      feedsModule = await oi.datamanager.getDataModule("feed");
    }
  }

  Future<List<emData>> loadCache({String? collectionId}) async {
    await createFeedModule(collectionId: collectionId);
    List<emData> result = [];
    List<emData>? data = feedsModule?.getAllHits();
    if (data != null && data.isNotEmpty) {
      result.addAll(data);
    }
    return result;
  }

  Future<List<emData>> loadFeeds(int page, {String? collectionId}) async {
    await createFeedModule(collectionId: collectionId);
    Map query = getQuery(page);
    if (collectionId != null) {
      query["collectionid"] = collectionId;
    }
    log("${feedsModule!.page} -> ${feedsModule!.pages}");
    if (page != 1 && feedsModule!.pages < page) {
      return [];
    }
    Map<String, dynamic> results = await feedsModule!
        .createServiceOperation("channelfeed", RequestType.POST, query);
    results['data'] =
        results['uploads'].map((e) => emData.fromJson(e)).toList();
    results['totalhits'] = results['total'];
    results['pages'] = results['totalpages'];
    await feedsModule!.saveCache(results);
    return parseData(results);
  }

  List<emData> parseData(Map<String, dynamic> parsed) {
    List<emData> results =
        parsed["uploads"].map<emData>((json) => emData.fromJson(json)).toList();
    return results;
  }

  Map getQuery(int page) {
    return {
      "page": "$page",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    };
  }
}
