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
  DataModule? useruploadModule;

  createFeedModule({String? collectionId}) async {
    if (collectionId != null) {
      feedsModule = await oi.datamanager
          .getDataModule("feed", boxString: "feed_$collectionId");
    } else {
      feedsModule = await oi.datamanager.getDataModule("feed");
    }
  }

  flagFeed(String id) async {
    useruploadModule = await oi.datamanager.getDataModule("userupload");
    return await useruploadModule!
        .createModuleOperation("report", RequestType.POST, {
      "id": id,
      "reportedby.add": oi.authenticationmanager.emUser!.userid,
      // "reportedby.remove": oi.authenticationmanager.emUser!.userid,
    });
  }

  Future<emData> createFeed(
      Map<String, dynamic> feed, String collectionId) async {
    DataModule createFeedModule =
        await oi.datamanager.getDataModule("userupload");
    Map<String, dynamic> result = await createFeedModule.createModuleOperation(
        "uploadfinish", RequestType.POST, feed);
    return emData.fromJson(result);
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

  Future<List<emData>> searchfeeds(String keyword, int page) async {
    await createFeedModule();

    Map inQuery = {
      "page": "$page",
      "hitsperpage": "10",
      "query": {
        "terms": [
          {"field": "description", "operator": "freeform", "value": keyword}
        ]
      }
    };

    Map<String, dynamic> results = await feedsModule!
        .createServiceOperation("channelfeed", RequestType.POST, inQuery);
    return parseData(results);
  }
}
