
import 'dart:convert';

import 'package:get/get.dart';
import 'package:openinsitute_core/models/emFeeds.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:hive/hive.dart';

OpenI get oi {
  return Get.find();
}

class Feeds {
  static const String feedString = "feeds";
  late Box box;
  bool cache = true;
  int? total = 0;

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

  List<emFeed> getAllHits(){
    List<emFeed> list = [];
     for (var element in box.keys) {
      if(element != "lastsync" && element != "total") {
          emFeed newdata = emFeed.fromJson(Map<String, dynamic>.from(box.get(element)));
       list.add(newdata);
      }
     }
     total = box.get("total");
     return list;
  }
    Future<List<emFeed>> getRemoteData(Map? inQuery,) async {
    final responsestring = await oi.getEmResponse(
      oi.app!["mediadb"] + "/services/feed/channelfeed.json",
      inQuery,
    );
    List<emFeed> results = parseData(responsestring);
    box.put("total",total);
    return results;

    //return compute(parseData, responsestring);  Figure this out, it keeps everything responsive
  }

  List<emFeed> parseData(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emFeed> results =
        parsed["uploads"].map<emFeed>((json) => emFeed.fromJson(json)).toList();
    total = int.parse(parsed["total"]);
    return results;
  }
    Future<bool> syncData(bool full, int page) async {
      Map simplesearch = {
        "page": "$page",
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
    if (full) {
      await box.clear();
      List<emFeed> tosave = await getRemoteData(simplesearch);
      for (var element in tosave) {
        box.put(element.id, element.properties);
      }
      
    } else {
      DateTime lastsync = box.get("lastsync"); 
      List<emFeed> tosave =
          await getRemoteData(null);
      for (var element in tosave) {
        box.put(element.id, element.properties);
      }
    }
    box.put("lastsync", DateTime.now());
    return true;
  }


}
