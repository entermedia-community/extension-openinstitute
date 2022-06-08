import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:get/get_connect/http/src/status/http_status.dart';
import 'package:http/http.dart' as http;
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/models/oiChatMessage.dart';
import 'package:openinsitute_core/models/taskList.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

void main() {


  TestWidgetsFlutterBinding.ensureInitialized(); //Nasty!
  final oi = OpenI();
  Get.put<OpenI>(oi);
  oi.initialize();
  HttpOverrides.global = _MyHttpOverrides(); // Setting a customer override that'll use an unmocked HTTP client

  test('Load Settings', () async {
    Map? settings = await oi.loadAppSettings();
    expect(settings!.isNotEmpty, true);



    // Map<String, dynamic> datatest  = await oi.hiveTest();;
    // expect(datatest.isNotEmpty, true);




    var url = "${settings['dev']?['websocket_url']}";
    print(url);

    expect(url.isNotEmpty, true);
    EmUser? user = await oi.login("admin", "admin");
    expect(user != null, true);

    // bool? emailed = await oi.emEmailKey("support@openedit.org");
    // expect(emailed, true);

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

    List<emData> listsearch = await (await oi.datamanager.getDataModule("purpose")).getRemoteData(simplesearch);
    expect(listsearch.length > 0, true);

    List<emData> checkcache = (await oi.datamanager.getDataModule("purpose")).getAllHits();




    expect(user != null, true);

    List<ToDo> tasks = await oi.getOpenTasks();
    expect(tasks.length > 0, true);
    ToDo list = tasks.first;
    expect(list.tasks!.isNotEmpty,true);






//    List<Task> tasks = oi.getOpenTasks();


  });

  // test('Server Data Query', () async {
  //
  //   EmUser? user = await oi.login("admin", "admin");
  //   expect(user != null, true);
  //   Map chatsearch = {
  //     "runpath": "/services/some/command",
  //     "channel": "123",
  //     "user":"456"
  //   };
  //   // HitTracker hits = oi.dataManager.execute(chatsearch);
  // //
  //
  //
  // });


  test('Test Websocket Connection', () async {

    EmUser? user = await oi.login("admin", "admin");
    expect(user != null, true);

    oi.emSocketManager.connect();
    oi.emSocketManager.sendString("keepalive");
    oi.emSocketManager.sendMessage({"keepalive":"true"});

    await Future.delayed(const Duration(seconds: 20), (){});



  });
  test('Test Module Crud Operations', () async {
    DataModule testmodule = await oi.dataManager!.getDataModule("purpose");
    HitTracker hits = testmodule.getAllHits();
    emData data = new emData();


  });

  test('Test Project CHat loading', () async {

    EmUser? user = await oi.login("admin", "admin");
    expect(user != null, true);

    Map? settings = await oi.loadAppSettings();

    //TODO: Get list of all projects
    var projects = await oi.chatManager?.getUserProjects();
    expect(projects!.length > 0, true);

    var projectid = settings?['dev']?['test_collection_id'];
    var messages = await oi.chatManager?.getProjectChatMessages(projectid);
    expect(messages!.length > 5, true);

  });


  // test('Test Loading and Syncing Data', () async {
  //   Searcher searcher = await oi.datamanager.getSearcher("purpose");
  //   List<emData> checkcache = searcher.getAllHits();
  //
  // });

}

//https://timm.preetz.name/articles/http-request-flutter-test


class _MyHttpOverrides extends HttpOverrides {}