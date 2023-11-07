import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:get/get.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/Helper/merge_map.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:logger/logger.dart';

OpenI get oi {
  return Get.find();
}

class DataManager {
  bool initalized = false;
  Map<String, DataModule> searchers = {};
  DataManager();
  Future<DataModule> getDataModule(String inSearchType,
      {String? boxString}) async {
    boxString ??= inSearchType;
    DataModule? searcher = searchers[boxString];
    if (searcher == null) {
      searcher = await DataModule.createDataModule(inSearchType, boxString);
      searchers[boxString] = searcher;
      return searcher;
    }
    searcher = await DataModule.createDataModule(inSearchType, boxString);
    return searcher;
  }

  dispose() {
    searchers.clear();
  }
}

class DataModule {
  late String searchtype;
  late String boxString;
  late Box box;
  bool cache = true;
  int total = 0;
  int page = 1;
  int pages = 1;
  DataModule._();

  emData getDataById(String id) {
    emData newdata = emData.fromJson(box.get(id));
    return newdata;
  }

  static Future<DataModule> createDataModule(
    String inSearchType,
    String boxString,
  ) async {
    var searcher = DataModule._();
    searcher.searchtype = inSearchType;
    searcher.boxString = boxString;
    searcher.box = await oi.hivemanager.setBox(boxString);
    return searcher;
  }

  DataModule(this.searchtype);

  List<emData> getAllHits() {
    List<emData> list = [];
    total = box.get("totalhits") ?? 0;
    page = box.get("page") ?? 0;
    pages = box.get("pages") ?? 1;
    for (var element in box.keys) {
      if (element != "totalhits" &&
          element != "lastsync" &&
          element != "page" &&
          element != "pages") {
        emData newdata = emData.fromJson(box.get(element));
        list.add(newdata);
      }
    }
    return list.reversed.toList();
  }

  Future<bool> syncData(bool full, int page) async {
    if (full) {
      await box.clear();
      Map simplesearch = {
        "page": "$page",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      };
      List<emData> tosave = await getRemoteData(simplesearch, true);
      for (var element in tosave) {
        box.put(element.id, element.properties);
      }
      //check if there are more pages of hits and run next search
    } else {
      DateTime lastsync = box.get("lastsync");
      Map aftersearch = {
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"},
            {
              "field": "emrecordstatus.recordmodificationdate",
              "operation": "after",
              "value": lastsync.toIso8601String()
            }
          ]
        }
      };

      List<emData> tosave =
          await getRemoteData({"emrecorddate???? neeed to chec": "*"}, false);
    }
    box.put("lastsync", DateTime.now());
    return true;
  }

  Future<List<emData>> getRemoteData(Map inQuery, bool cache) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/search',
        inQuery,
        RequestType.POST);
    Map<String, dynamic> resultsData = parseData(responsestring);
    List<emData> results = resultsData["data"];
    if (cache) {
      await saveCache(resultsData);
    }
    return results;
  }

  Future<emData> updateData(String id, Map inQurey) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/data/$id',
        inQurey,
        RequestType.PUT);
    emData result = parseDataSingle(responsestring);
    Map<dynamic, dynamic> cache = box.get(
          result.id,
        ) ??
        {};
    Map<dynamic, dynamic> data = mergeMaps(cache, result.properties);
    await box.put(result.id, data);
    return result;
  }

  Future<bool> deleteData(String? id) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/data/$id',
        {},
        RequestType.DELETE);
    Map<dynamic, dynamic> results = jsonDecode(responsestring);
    if (results['response']['deleted'] == "true") {
      await box.delete(id);
      return true;
    }
    return false;
  }

  Future<emData> addData(Map inQuery) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/create',
        inQuery,
        RequestType.POST);
    emData results = parseDataSingle(responsestring);

    box.put(results.id, results.properties);
    return results;
  }

  Future<emData> getData(String id) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/data/$id',
        {},
        RequestType.GET);
    emData results = parseDataSingle(responsestring);
    box.put(results.id, results.properties);
    return results;
  }

  Map<String, dynamic> parseData(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emData> results =
        parsed["results"].map<emData>((json) => emData.fromJson(json)).toList();
    Map<String, dynamic> resultData = {};
    resultData['data'] = results;
    resultData['page'] = parsed['page'] ?? parsed["response"]["page"];
    resultData['pages'] = parsed['pages'] ?? parsed["response"]["pages"];
    resultData['totalhits'] =
        parsed['totalhits'] ?? parsed["response"]["totalhits"];
    return resultData;
  }

  emData parseDataSingle(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    emData result = emData.fromJson(parsed['data'] ?? {});
    return result;
  }

  Future<Map<String, dynamic>> createModuleOperation(
      String endpoint, RequestType requestType, Map inQuery,
      {bool cache = false}) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/module/$searchtype/$endpoint',
        inQuery,
        requestType);
    Map<String, dynamic> map =
        await compute(jsonDecode, responsestring) as Map<String, dynamic>;
    if (cache) {
      await cacheAllData(map);
    }
    return map;
  }

  Future<Map<String, dynamic>> createServiceOperation(
      String endpoint, RequestType requestType, Map inQuery,
      {bool cache = false}) async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/$searchtype/$endpoint',
        inQuery,
        requestType);
    Map<String, dynamic> map =
        await compute(jsonDecode, responsestring) as Map<String, dynamic>;
    if (cache) {
      await cacheAllData(map);
    }
    return map;
  }

  saveCache(Map<String, dynamic> resultsData) async {
    List<dynamic> results = resultsData["data"] ??
        resultsData['results'].map((e) => emData.fromJson(e));
    await box.put(
        "page",
        int.parse(
            (resultsData["page"] ?? resultsData["response"]["page"].toString())
                .toString()));
    await box.put(
        "pages",
        int.parse((resultsData["pages"] ??
                resultsData["response"]["pages"].toString())
            .toString()));
    await box.put(
        "totalhits",
        int.parse((resultsData["totalhits"] ??
                resultsData["response"]["totalhits"].toString())
            .toString()));
    total = box.get("totalhits") ?? 0;
    page = box.get("page") ?? 0;
    pages = box.get("pages") ?? 0;
    if (page == 1) {
      await box.clear();
      for (var element in results) {
        await box.put(element.id, element.properties);
      }
    }
  }

  cacheAllData(Map<String, dynamic> resultsData) async {
    await box.clear();
    List<dynamic> results = resultsData["data"] ??
        resultsData['results']?.map((e) => emData.fromJson(e)).toList() ??
        resultsData['goals'].map((e) => emData.fromJson(e)).toList();
    await box.put(
        "page",
        int.parse(
            (resultsData["page"] ?? resultsData["response"]["page"] ?? "0")
                .toString()));
    await box.put(
        "pages",
        int.parse(
            (resultsData["pages"] ?? resultsData["response"]["pages"] ?? "0")
                .toString()));
    await box.put(
        "totalhits",
        int.parse((resultsData["totalhits"] ??
                resultsData["response"]["totalhits"] ??
                "0")
            .toString()));
    total = box.get("totalhits") ?? 0;
    page = box.get("page") ?? 0;
    pages = box.get("pages") ?? 0;
    for (var element in results) {
      await box.put(element.id, element.properties);
    }
  }
}
