import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:get/get_connect/http/src/status/http_status.dart';
import 'package:http/http.dart' as http;
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

void main() {


  TestWidgetsFlutterBinding.ensureInitialized(); //Nasty!
  final oi = OpenI();
  Get.put<OpenI>(oi);
  oi.initialize();

  HttpOverrides.global = _MyHttpOverrides(); // Setting a customer override that'll use an unmocked HTTP client


  test('Load Settings', () async {
    Map? settings = await oi.loadAppSettings();
    expect(settings!.isNotEmpty, true);

    var url = "${settings!['dev']?['websocket_url']}";
    print(url);

    expect(url.isNotEmpty, true);
    EmUser? user = await oi.login("admin", "admin");
    expect(user != null, true);

    


  });




}

//https://timm.preetz.name/articles/http-request-flutter-test


class _MyHttpOverrides extends HttpOverrides {}