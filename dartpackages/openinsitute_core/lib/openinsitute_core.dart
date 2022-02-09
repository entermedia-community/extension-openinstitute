library openinsitute_core;

import 'dart:async' show Future;
import 'dart:convert';

import 'package:flutter/services.dart' show rootBundle;
import 'package:get/get.dart';
import 'package:http/http.dart' as http;


class OpenI {

  Map? settings;

  void initialize() async {
    await loadAppSettings();
    Get.put<OpenI>(this);

  }

  Future<Map?> loadAppSettings() async {
    if (settings == null) {
      String json = await rootBundle.loadString("system/appsettings.json");
      print("loaded ${json}");
      settings = jsonDecode(json);
    }
    return settings;
  }



}
