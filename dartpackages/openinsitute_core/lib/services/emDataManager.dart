import 'dart:convert';

import 'package:get/get.dart';
import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

OpenI get oi {
  return Get.find();
}

class DataManager {
  bool initalized = false;
  Map<String, Searcher> searchers = {};

  DataManager() {
    //TODO:  Figure out best way to do this without an async race condition
    Hive.initFlutter();
  }

  Future<Searcher> getSearcher(String inSearchType) async {
    Searcher? searcher = searchers[inSearchType];
    if (searcher == null) {
      searcher = await Searcher.createSearcher(inSearchType);
      searchers[inSearchType] = searcher;
    }
    //https://stackoverflow.com/questions/53886304/understanding-factory-constructor-code-example-dart do this?
    return searcher;
  }





}

class Searcher {
  //https://stackoverflow.com/questions/54549235/dart-await-on-constructor

  late String searchtype;
  late Box box;
  bool cache = true;

  Searcher._();

  static Future<Searcher> createSearcher(String inSearchType) async {
    var searcher = Searcher._();
    searcher.searchtype = inSearchType;
    searcher.box = await searcher.getBox(inSearchType);
    return searcher;
  }

  Searcher(this.searchtype);

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

  List<emData> getAllHits(){
    List<emData> list = [];
    // List list = List.generate(box.keys.length, ,{"growable":true});
     box.keys.forEach((element) {
       emData newdata = emData.fromJson(box.get(element));
       list.add(newdata);
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
            {
              "field": "id",
              "operation": "matches",
              "value": "*"
            }
          ]
        }
      };

      List<emData> tosave = await getRemoteData(simplesearch);
      tosave.forEach((element) {
        box.put(element.id, element.properties);
      });
    } else {
      DateTime lastsync = box.get("lastsync"); //dunno if this works!
      List<emData> tosave =
          await getRemoteData({"emrecorddate???? neeed to chec": "*"});
    }
    //box.put("lastsync", DateTime.now());
    return true;
  }

  Future<List<emData>> getRemoteData(Map inQuery) async {
    final responsestring = await oi.getEmResponse(
      oi.app!["mediadb"] + '/services/lists/search/${searchtype}/',
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


  Stream<emData> updates() async * {

    Stream<dynamic> stream = oi.emSocketManager.stream;
    await for (final data in stream){
          json.decode(data);
    }

  }

}