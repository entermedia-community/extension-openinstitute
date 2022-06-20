import 'package:hive_flutter/hive_flutter.dart';
import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

class ProjectManager {

  OpenI get oi {
  return Get.find();
}

  Future<List> getUserProjects(int page) async {
    Map params = {"page": "$page", "hitsperpage": "200"};

    var box = await getBox("oicache");
    var results = box.get("viewprojects"); //Some cache system
    if (results == null) {
      results = <emData>[]; //Make one list that is cached
      box.put("viewprojects", results);
    }

    //TODO; Call this part in an async way
    final Map? responded = await oi.postEntermedia(
        oi.app!["mediadb"] +
            '/services/module/librarycollection/viewprojects.json',
        params,);

    //TODO: How do I create emChatMessages from json?
    List<emData> messages = responded!["results"]!
        .map<emData>((json) => emData.fromJson(json))
        .toList();
    box.put("pages", responded["response"]["pages"]);
    results.clear();
    results
        .addAll(messages); //TODO: This should reload the UI with new entries?
    box.put("viewprojects", results);
    return Future.value(results);
  }

}


Future<Box> getBox(String inType) async {
  if (!Hive.isBoxOpen(inType)) {
    return Hive.openBox(inType);
  } else {
    return Hive.box(inType);
  }
}
