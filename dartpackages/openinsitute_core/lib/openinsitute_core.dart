library openinsitute_core;

import 'dart:async' show Future, TimeoutException;
import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart' show rootBundle;
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:openinsitute_core/Helper/customException.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/services/FeedManager.dart';
import 'package:openinsitute_core/services/authentication_manager.dart';
import 'package:openinsitute_core/services/emDataManager.dart';
import 'package:openinsitute_core/services/emSocketManager.dart';
import 'package:openinsitute_core/services/hive_manager.dart';
import 'package:openinsitute_core/services/project_manager.dart';
import 'package:openinsitute_core/services/oiChatManager.dart';
import 'package:openinsitute_core/services/pushnotification_manager.dart';
import 'package:openinsitute_core/services/task_manager.dart';

//import 'contact.dart';

class OpenI {

  Map? _settings;
  DataManager? dataManager;
  OiChatManager? chatManager;
  EmSocketManager? socketManager;
  ProjectManager? projectManager;
  AuthenticationManager? authenticationManager;
  TaskManager? taskManager;
  FeedManager? feedManager;
  HiveManager? hiveManager;
  PushNotificationManager? pushNotificationManager;
  
  Map? get app  {
    if (_settings == null) {
       loadAppSettings();
    }
    String appmode = _settings!["devmode"];
    if ("dev" == appmode) {
      return _settings!["dev"];
    } else {
      return _settings!["prod"];
    }
  }

  Map? get settings {
    if (_settings == null) {
      loadAppSettings();
    }
    return _settings;
  }

  DataManager get datamanager => Get.find<DataManager>();
  EmSocketManager get emSocketManager => Get.find<EmSocketManager>();
  ProjectManager get projectmanager => Get.find<ProjectManager>();
  AuthenticationManager get authenticationmanager => Get.find<AuthenticationManager>();
  TaskManager get taskmanager => Get.find<TaskManager>();
  FeedManager get feedmanager => Get.find<FeedManager>();
  HiveManager get hivemanager => Get.find<HiveManager>();
  PushNotificationManager get pushnotificationmanager => Get.find<PushNotificationManager>();

  Future<void> initialize() async {
    await loadAppSettings();
    hiveManager = HiveManager();
    await hiveManager!.init();
    Get.put<OpenI>(this,permanent: true);
    Get.put<HiveManager>(hiveManager!, permanent: true);
    dataManager = DataManager();
    Get.put<DataManager>(dataManager!,permanent: true);
    chatManager = OiChatManager();
    Get.put<OiChatManager>(chatManager!,permanent: true);
    socketManager = EmSocketManager();
    Get.put<EmSocketManager>(socketManager!,permanent: true);
    projectManager = ProjectManager();
    Get.put<ProjectManager>(projectManager!,permanent: true);
    authenticationManager = AuthenticationManager();
    Get.put<AuthenticationManager>(authenticationManager!,permanent: true);
    taskManager = TaskManager();
    Get.put<TaskManager>(taskManager!,permanent: true);
    feedManager = FeedManager();
    Get.put<FeedManager>(feedManager!,permanent: true);
    pushNotificationManager = PushNotificationManager();
    Get.put<PushNotificationManager>(pushNotificationManager!,permanent: true);
    pushNotificationManager!.registerNotificationListeners();
  }

  Future<Map?> loadAppSettings() async {
    if (_settings == null) {
      String json = await rootBundle.loadString("system/appsettings.json");
        print("loaded $json");
      _settings = jsonDecode(json);
    }
    return _settings;
  }

  //TODO: Add prefix before the token
  String handleTokenKey(String token) {
    final String newToken = /*"em" + */ token;
    return newToken;
  }

  //Generic post method to entermedias server
  Future<Map?> postEntermedia(String url, dynamic jsonBody,
      {String customError = "An Error Occured"}) async {
    //Set headers
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (authenticationmanager.emUser != null) {
      String tokenKey = handleTokenKey(authenticationmanager.emUser!.entermediakey);
      headers.addAll({"X-token": tokenKey});
    }
    //make API post
    final response = await httpRequest(
      requestUrl: url,
      body: json.encode(jsonBody),
      headers: headers,
      requestType: RequestType.POST,
      customError: customError,
    );
      print(" user info is:" + response!.body);
    if (response != null && response.statusCode == 200) {
      print("Success user info is:" + response.body);
      final String responseString = response.body;
      return json.decode(responseString);
    } else {
      return null;
    }
  }


  //Generic post method to entermedias server
  Future<String> getEmResponse(String url, dynamic jsonBody, RequestType requestType, 
      {String customError = "An Error Occured"}) async {
    //Set headers
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (authenticationmanager.emUser != null) {
      String tokenKey = handleTokenKey(authenticationmanager.emUser!.entermediakey);
      headers.addAll({"X-token": tokenKey});
    }

    final response = await httpRequest(
      requestUrl: url,
      body: jsonEncode(jsonBody),
      headers: headers,
      requestType: requestType,
      customError: customError,
    );
    if (response != null && response.statusCode == 200) {
      print("Success user info is:" + response.body);
      final String responseString = response.body;
      return responseString;
    } else {
      return "{}";
    }
  }




  Future<http.Response?> httpRequest({
    required String requestUrl,
    required dynamic body,
    required Map<String, String> headers,
    required RequestType requestType,
    String customError = "Some Error",
  }) async {
    String url = requestUrl;
      print(url);
    http.Response response;
    try {
      http.Response? responseJson;
      if (requestType == RequestType.PUT) {
        responseJson = await http.put(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      } else if (requestType == RequestType.POST) {
        responseJson = await http.post(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      } else if (requestType == RequestType.GET) {
        responseJson = await http.get(
          Uri.parse(url),
          headers: headers,
        );
      } else if (requestType == RequestType.DELETE) {
        responseJson = await http.delete(
          Uri.parse(url),
          headers: headers,
          body: body,
        );
      }
      print(responseJson!.statusCode);
      response = await handleException(responseJson);
      return response;
    } on BadRequestException catch (error) {
      //showErrorFlushbar( "Bad request! Please try again later.");
    } on UnauthorisedException catch (error) {
      //showErrorFlushbar( "Unauthorized user. Please try again.");
    } on TimeoutException catch (error) {
      //showErrorFlushbar(context, "Request timed out. Please try again!");
    } on SocketException catch (error) {
      // showErrorFlushbar(context, "Unable to connect to server. Please try again!");
    } on HttpException catch (error) {
      // showErrorFlushbar(
      //  context, customError != null ? customError : "Error occurred while communication with server. Please try again after some time.");
    }
    return null;
  }

  dynamic handleException(http.Response response) {
    print("Response code: " + response.statusCode.toString());
    switch (response.statusCode) {
      case 200:
        final http.Response responseJson = response;
        return responseJson;
        break;
      case 201:
        final http.Response responseJson = response;
        return responseJson;
        break;
      case 302:
        break;
      case 400:
        throw BadRequestException(response.body.toString());
        break;
      case 403:
        throw UnauthorisedException(response.body.toString());
        break;
      case 408:
        throw TimeoutException(response.body.toString());
        break;
      case 500:
        throw HttpException(response.body.toString());
        break;
      default:
        // throw FetchDataException('Error occurred while Communication with Server with StatusCode : ${response.statusCode}');
        break;
    }
  }



  // void registerBinaries() {
  //     final dartToolDir = path.join(Directory.current.path, '.dart_tool');
  //     try {
  //       Isar.initializeLibraries(
  //         libraries: {
  //           'windows': path.join(dartToolDir, 'libisar_windows_x64.dll'),
  //           'macos': path.join(dartToolDir, 'libisar_macos_x64.dylib'),
  //           'linux': path.join(dartToolDir, 'libisar_linux_x64.so'),
  //         },
  //       );
  //     } catch (e) {
  //       // ignore. maybe this is an instrumentation test
  //     }
  //
  // }

  // Future<List<Contact>> testIstarSave() async {
  //   registerBinaries();
  //   final dir = await getApplicationSupportDirectory();
  //
  //   final isar = await Isar.open(
  //     schemas: [ContactSchema],
  //     directory: dir.path,
  //   );
  //
  //   final contact = Contact()
  //     ..name = "My first contact";
  //
  //   await isar.writeTxn((isar) async {
  //     contact.id = await isar.contacts.put(contact) ;
  //   });
  //
  //   final allContacts = await isar.contacts.where().findAll();
  //   print(allContacts);
  //   return allContacts;
  //
  // }

}