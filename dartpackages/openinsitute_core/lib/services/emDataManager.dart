import 'dart:convert';

import 'package:get/get.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/emFeeds.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

OpenI get oi {
  return Get.find();
}

class DataManager {
  bool initalized = false;
  Map<String, DataModule> searchers = {};
  Feeds? feeds;

  DataManager() {
    //TODO:  Figure out best way to do this without an async race condition
    Hive.initFlutter();
  }

  Future<DataModule> getSearcher(String inSearchType) async {
    DataModule? searcher = searchers[inSearchType];
    if (searcher == null) {
      searcher = await DataModule.createDataModule(inSearchType);
      searchers[inSearchType] = searcher;
    }
    //https://stackoverflow.com/questions/53886304/understanding-factory-constructor-code-example-dart do this?
    return searcher;
  }

  Future<Feeds> getFeeds() async {
    feeds ??= await Feeds.getFeeds();
    return feeds!;
  }
}

class Feeds {
  static const String feedString = "feeds";
  late Box box;
  bool cache = true;

  Feeds._();

  static Future<Feeds> getFeeds() async {
    var feeds = Feeds._();
    feeds.box = await feeds.getBox(feedString);
    return feeds;
  }

  Future<Box> getBox(String inType) async {
    if (!Hive.isBoxOpen(inType)) {
      return await Hive.openBox(inType);
    } else {
      return Hive.box(inType);
    }
  }

  List<emFeed> getAllHits() {
    List<emFeed> list = [];
    box.keys.forEach((element) {
      if (element != "lastsync") {
        emFeed newdata = emFeed.fromJson(box.get(element));
        list.add(newdata);
      }
    });
    return list;
  }

  Future<List<emFeed>> getRemoteData(
    Map? inQuery,
  ) async {
    final responsestring = await oi.getEmResponse(
      oi.app!["mediadb"] + "/services/feed/channelfeed.json",
      inQuery,
    );
    List<emFeed> results = parseData(responsestring);
    results.forEach((element) {
      box.put(element.id, element.properties);
    });

    return results;

    //return compute(parseData, responsestring);  Figure this out, it keeps everything responsive
  }

  List<emFeed> parseData(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emFeed> results =
        parsed["uploads"].map<emFeed>((json) => emFeed.fromJson(json)).toList();
    return results;
  }

  Future<bool> syncData(bool full) async {
    if (full) {
      await box.clear();
      List<emFeed> tosave = await getRemoteData(null);
      for (var element in tosave) {
        box.put(element.id, element.properties);
      }
    } else {
      DateTime lastsync = box.get("lastsync");
      List<emFeed> tosave = await getRemoteData(null);
      for (var element in tosave) {
        box.put(element.id, element.properties);
      }
    }
    box.put("lastsync", DateTime.now());
    return true;
  }
}

class DataModule {
  //https://stackoverflow.com/questions/54549235/dart-await-on-constructor

  late String searchtype;
  late Box box;
  bool cache = true;

  DataModule._();

  static Future<DataModule> createDataModule(String inSearchType) async {
    var searcher = DataModule._();
    searcher.searchtype = inSearchType;
    searcher.box = await searcher.getBox(inSearchType);
    return searcher;
  }

  DataModule(this.searchtype);

  Future<Box> getBox(String inType) async {
    if (!await Hive.isBoxOpen(inType)) {
      return await Hive.openBox(inType);
    } else {
      return await Hive.box(inType);
    }
  }

  Future<Map<String, dynamic>> hiveTest() async {
    Map<String, dynamic> map = {
      "ian": "washere",
      "bob": {"was": "alsohere"}
    };
    var box = await Hive.openBox("test");
    box.put("test", map);

    return box.get("test");
  }

  List<emData> getAllHits() {
    List<emData> list = [];
    // List list = List.generate(box.keys.length, ,{"growable":true});
    box.keys.forEach((element) {
      if(element != "lastsync") {
        emData newdata = emData.fromJson(box.get(element));
        list.add(newdata);
      }
    });
    return list;
  }

  Future<bool> syncData(bool full) async {
    if (full) {
      await box.clear();

      Map simplesearch = {
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      };

      List<emData> tosave = await getRemoteData(simplesearch);
      tosave.forEach((element) {
        box.put(element.id, element.properties);
      });
      //check if there are more pages of hits and run next search

    } else {
      DateTime lastsync = box.get("lastsync");
      Map aftersearch = {
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"},
            {"field": "emrecordstatus.recordmodificationdate", "operation": "after", "value": lastsync.toIso8601String()}

          ]
        }
      };


      List<emData> tosave =
          await getRemoteData({"emrecorddate???? neeed to chec": "*"});
    }
    box.put("lastsync", DateTime.now());
    return true;
  }

  Future<List<emData>> getRemoteData(
    Map inQuery,
  ) async {
    final responsestring = await oi.getEmResponse(
      oi.app!["mediadb"] + '/services/modules/${searchtype}/search',
      inQuery,
    );
    List<emData> results = parseData(responsestring);

    results.forEach((element) {
      box.put(element.id, element.properties);
    });

    return results;

    //return compute(parseData, responsestring);  Figure this out, it keeps everything responsive
  }



  List<emData> parseData(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emData> results =
        parsed["results"].map<emData>((json) => emData.fromJson(json)).toList();

    return results;
  }

  Stream<emData> updates() async* {
    Stream<dynamic> stream = oi.emSocketManager.stream;
    await for (final data in stream) {
      json.decode(data);
    }
  }
}
