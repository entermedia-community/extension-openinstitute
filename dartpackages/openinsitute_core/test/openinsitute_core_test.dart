import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:get/get_connect/http/src/status/http_status.dart';
import 'package:http/http.dart' as http;
import 'package:openinsitute_core/contact.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/models/taskList.dart';
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

    List<Contact> contacts  = await oi.saveSomeStuff();


    expect(contacts.isNotEmpty, true);


    var url = "${settings!['dev']?['websocket_url']}";
    print(url);

    expect(url.isNotEmpty, true);
    EmUser? user = await oi.login("admin", "admin");
    expect(user != null, true);

    bool? emailed = await oi.emEmailKey("support@openedit.org");

    expect(emailed, true);

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


    List<emData> listsearch = await oi.getEmData("purpose", simplesearch);
    expect(listsearch.length > 0, true);
    expect(user != null, true);

    List<TaskList> tasks = await oi.getOpenTasks();
    expect(tasks.length > 0, true);
    TaskList list = tasks.first;
    expect(list.tasks!.isNotEmpty,true);



//    List<Task> tasks = oi.getOpenTasks();


  });




}

//https://timm.preetz.name/articles/http-request-flutter-test


class _MyHttpOverrides extends HttpOverrides {}