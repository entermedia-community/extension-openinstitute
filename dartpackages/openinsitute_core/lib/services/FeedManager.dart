import 'dart:convert';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:get/get.dart';

OpenI get oi {
  return Get.find();
}

class FeedManager {
  String feedsBox = "feeds";

  Future<List<emData>> loadCache() async {
    List<Map<String, dynamic>> cache =
        await  oi.hivemanager.getAllHits(feedsBox);
    return cache.map((e) => emData.fromJson(e)).toList();
  }

  Future<int?> getTotal() async {
    return  (await  oi.hivemanager.getData("total", feedsBox));
  }

  updateTotal(int total) async {
    await  oi.hivemanager.saveData("total", total, feedsBox);
  }

  Future<List<emData>> loadFeeds(int page) async {
    Map query = getQuery(page);
    String results = await getRemoteData(query);
    if (page == 1) {
      await saveCache(results);
    }
    return await parseData(results);
  }

  Future<List<emData>> parseData(String responseBody) async {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emData> results =
        parsed["uploads"].map<emData>((json) => emData.fromJson(json)).toList();
    await updateTotal(int.parse( parsed["total"]));
    return results;
  }

  saveCache(String cacheString) async {
    Map<String, dynamic> cache = json.decode(cacheString);
    await  oi.hivemanager.clear(feedsBox);
    await updateTotal(int.parse( cache["total"]));
    await  oi.hivemanager.saveData(
        "lastsync", DateTime.now().millisecondsSinceEpoch.toString(), feedsBox);
    List<emData> results =
        cache["uploads"].map<emData>((json) => emData.fromJson(json)).toList();
    for (var element in results) {
      await  oi.hivemanager.saveData(element.id!, element.properties, feedsBox);
    }
  }

  Map getQuery(int page) {
    return {
      "page": "$page",
      "hitsperpage": "30",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    };
  }

  Future<String> getRemoteData(
    Map? inQuery,
  ) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + "/services/feed/channelfeed.json",
        inQuery,
        RequestType.POST);
    return responsestring;
  }
}
