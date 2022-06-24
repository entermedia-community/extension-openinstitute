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

    // clear the cache

    //TODO; Call this part in an async way
    final Map? responded = await oi.postEntermedia(
      oi.app!["mediadb"] +
          '/services/module/librarycollection/viewprojects.json',
      params,
    );

    List<emData> projects = responded!["results"]!
        .map<emData>((json) => emData.fromJson(json))
        .toList();
    box.put("pages", responded["response"]["pages"]);
    
    results.clear();
    results.addAll(projects);
    return Future.value(results);
  }

  Future<void> createProject(String projectName, String projectDes) async {
    await oi.postEntermedia(
      oi.app!["mediadb"] + '/services/module/librarycollection/create',
      {
        "name": projectName,
        // "owner": {"id": oi.authenticationManager!.emUser!.userid, "name": oi.authenticationManager!.emUser!.screenname},
      },
    );
  }
}

Future<Box> getBox(String inType) async {
  if (!Hive.isBoxOpen(inType)) {
    return Hive.openBox(inType);
  } else {
    return Hive.box(inType);
  }
}
