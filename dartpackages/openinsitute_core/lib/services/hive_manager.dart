import 'dart:io';

import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/services/sharedpreferences.dart';
import 'package:path_provider/path_provider.dart';

class HiveManager {
  static const assets = "assets";
  static const hivechatrooms = "chatrooms";

  HiveManager();

  Future<void> init() async {
    String? path = await sharedPref.getEMPath();
    if (path != null) {
      Hive.init(path);
    } else {
      Directory appDir = await getApplicationDocumentsDirectory();
      path = appDir.path;
      await sharedPref.saveEMPath(path);
      Hive.init(path);
    }
  }

  Future<Box> openHiveBox(String boxString) async {
    return await Hive.openBox(boxString);
  }

  Future<Box> setBox(String boxString) async {
    if (Hive.isBoxOpen(boxString)) {
      return Hive.box(boxString);
    }
    return await openHiveBox(boxString);
  }

  Future<void> saveData(String id, dynamic data, String boxString) async {
    Box box;
    box = await setBox(boxString);
    box.put(id, data);
  }

  Future<dynamic> getData(String id, String boxString) async {
    Box box;
    box = await setBox(boxString);
    print(box.get(id).toString());
    return box.get(id);
  }

  Future<bool> isExist(String id, String boxString) async {
    Box box;
    box = await setBox(boxString);
    return box.containsKey(id);
  }

  Future<void> clear(String boxString) async {
    Box box;
    box = await setBox(boxString);
    await box.clear();
  }

  Future<List<Map<String, dynamic>>> getAllHits(String boxString) async {
    Box box;
    box = await setBox(boxString);
    List<Map<String, dynamic>> list = [];
    for (var element in box.keys) {
      if (element != "lastsync" && element != "total" && element != "pages") {
        Map<String, dynamic> newdata =
            (Map<String, dynamic>.from(box.get(element)));
        list.add(newdata);
      }
    }
    return list;
  }
}
