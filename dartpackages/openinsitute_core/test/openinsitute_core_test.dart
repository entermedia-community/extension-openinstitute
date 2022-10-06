import 'dart:io';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:hive_test/hive_test.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';
import 'package:firebase_auth_mocks/firebase_auth_mocks.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized(); //Nasty!
  final oi = OpenI();
  Get.put<OpenI>(oi);
  FirebaseAuth firebaseAuth = MockFirebaseAuth();
  oi.initialize(
    firebaseAuth: firebaseAuth,
  );
  HttpOverrides.global =
      _MyHttpOverrides(); // Setting a customer override that'll use an unmocked HTTP client

  setUp(() async {
    await setUpTestHive();
    SharedPreferences.setMockInitialValues({});
  });

  tearDown(() async {
    await tearDownTestHive();
  });

  test(
    'check User Settings',
    () async {
      Map? settings = await oi.loadAppSettings();
      EmUser? user = await oi.authenticationManager?.login("admin", "admin");
      emData data =
          await oi.authenticationmanager.editUser({'firstName': "Harshit"});
      expect(data != null, true);
    },
  );

  test("test String Url", () async {
    oi.getProjectID();
  });

  test('Load Settings', () async {
    Map? settings = await oi.loadAppSettings();
    expect(settings!.isNotEmpty, true);

    // Map<String, dynamic> datatest  = await oi.hiveTest();;
    // expect(datatest.isNotEmpty, true);

    var url = "${settings['dev']?['websocket_url']}";
    print(url);

    expect(url.isNotEmpty, true);
    EmUser? user = await oi.authenticationManager?.login("admin", "admin");
    if (user != null) {
      var email = user.email;
      expect(user != null, true);
    }
    // bool? emailed = await oi.emEmailKey("support@openedit.org");
    // expect(emailed, true);

    Map simplesearch = {
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    };

    List<emData> listsearch =
        await (await oi.datamanager.getDataModule("purpose"))
            .getRemoteData(simplesearch, false);
    expect(listsearch.length > 0, true);

    List<emData> checkcache =
        (await oi.datamanager.getDataModule("purpose")).getAllHits();

    expect(user != null, true);

    List<dynamic>? tasks = await oi.taskManager?.loadMyTasks();
    expect(tasks != null, true);
    if (tasks != null) {
      expect(tasks.isEmpty, true);
      emData list = tasks.first;
      expect(list.properties['task']!.isNotEmpty, true);
    }

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
    EmUser? user = await oi.authenticationManager?.login("admin", "admin");
    expect(user != null, true);

    oi.emSocketManager.connect();
    oi.emSocketManager.sendString("keepalive");
    oi.emSocketManager.sendMessage({"keepalive": "true"});

    await Future.delayed(const Duration(seconds: 20), () {});
  });
  test('Test Module Crud Operations', () async {
    DataModule testmodule = await oi.dataManager!.getDataModule("purpose");
    List<emData> hits = testmodule.getAllHits();
    expect(hits.length > 0, true);
    Map tosave = {"name": "Test"};
    emData saved = await testmodule.addData(tosave);
    testmodule.deleteData(saved.id);
  });

  test('Test Project CHat loading', () async {
    Map? settings = await oi.loadAppSettings();
    EmUser? user = await oi.authenticationManager?.login("admin", "admin");
    expect(user != null, true);
    //TODO: Get list of all projects
    var projects = await oi.projectManager?.loadProject(1);
    expect(projects!.length > 0, true);

    var projectid = settings?['dev']?['test_collection_id'];
    var messages = await oi.chatManager?.loadChat(projectid, 1);
    expect(messages!.length > 5, true);

    //Create new chat

    //oi.chatManager?.saveMessage()
  });

  // test('Test Loading and Syncing Data', () async {
  //   Searcher searcher = await oi.datamanager.getSearcher("purpose");
  //   List<emData> checkcache = searcher.getAllHits();
  //
  // });
}

//https://timm.preetz.name/articles/http-request-flutter-test

class _MyHttpOverrides extends HttpOverrides {}
