library openinsitute_core;

import 'dart:async' show Future, TimeoutException;
import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:http/io_client.dart';
import 'package:http_interceptor/http/intercepted_client.dart';
import 'package:internet_connection_checker/internet_connection_checker.dart';
import 'package:openinsitute_core/Helper/custom-multipart.dart';
import 'package:openinsitute_core/Helper/customException.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/services/FeedManager.dart';
import 'package:openinsitute_core/services/authentication_manager.dart';
import 'package:openinsitute_core/services/connectivity_manager.dart';
import 'package:openinsitute_core/services/emDataManager.dart';
import 'package:openinsitute_core/services/emSocketManager.dart';
import 'package:openinsitute_core/services/finance_manager.dart';
import 'package:openinsitute_core/services/hive_manager.dart';
import 'package:openinsitute_core/services/project_manager.dart';
import 'package:openinsitute_core/services/oiChatManager.dart';
import 'package:openinsitute_core/services/task_manager.dart';
import 'package:openinsitute_core/services/user_manager.dart';
import 'package:path/path.dart';

import 'Helper/logging_interseptor.dart';
import 'services/assets_manager.dart';

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
  UserManager? userManager;
  FinanceManager? financeManager;
  ConnectivityManager? connectivityManager;
  AssetsManager? assetsManager;
  late InterceptedClient httpClient;

  OpenI() {
    httpClient = InterceptedClient.build(
      interceptors: [
        LoggingInterceptor(),
      ],
      requestTimeout: const Duration(seconds: 20),
      client: IOClient(
        HttpClient()
          ..badCertificateCallback =
              ((X509Certificate cert, String host, int port) => true),
      ),
    );
  }

  Map? get app {
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
  AuthenticationManager get authenticationmanager =>
      Get.find<AuthenticationManager>();
  TaskManager get taskmanager => Get.find<TaskManager>();
  FeedManager get feedmanager => Get.find<FeedManager>();
  HiveManager get hivemanager => Get.find<HiveManager>();
  UserManager get usermanager => Get.find<UserManager>();
  FinanceManager get financemanager => Get.find();
  AssetsManager get assetsmanager => Get.find();

  Future<void> initialize({required FirebaseAuth firebaseAuth}) async {
    await loadAppSettings();
    hiveManager = HiveManager();
    Get.put<OpenI>(this, permanent: true);
    Get.put<HiveManager>(hiveManager!, permanent: true);
    dataManager = DataManager();
    Get.put<DataManager>(dataManager!, permanent: true);
    chatManager = OiChatManager();
    Get.put<OiChatManager>(chatManager!, permanent: true);
    socketManager = EmSocketManager();
    Get.put<EmSocketManager>(socketManager!, permanent: true);
    projectManager = ProjectManager();
    Get.put<ProjectManager>(projectManager!, permanent: true);
    authenticationManager = AuthenticationManager(firebaseAuth);
    Get.put<AuthenticationManager>(authenticationManager!, permanent: true);
    taskManager = TaskManager();
    Get.put<TaskManager>(taskManager!, permanent: true);
    feedManager = FeedManager();
    Get.put<FeedManager>(feedManager!, permanent: true);
    userManager = UserManager();
    Get.put(userManager!, permanent: true);
    financeManager = FinanceManager();
    Get.put<FinanceManager>(financeManager!, permanent: true);
    connectivityManager = ConnectivityManager();
    Get.put<ConnectivityManager>(connectivityManager!);
    assetsManager = AssetsManager();
    Get.put<AssetsManager>(assetsManager!);
    connectivityManager!.subscription.listen((event) {
      if (event == InternetConnectionStatus.connected) {
        chatManager!.sendAllNonSendChat();
      }
    });
  }

  Future<Map?> loadAppSettings() async {
    if (_settings == null) {
      String json = await rootBundle.loadString("system/appsettings.json");
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
      String tokenKey =
          handleTokenKey(authenticationmanager.emUser!.entermediakey);
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
    if (response != null && response.statusCode == 200) {
      final String responseString = response.body;
      return json.decode(responseString);
    } else {
      return null;
    }
  }

  // Generic post method to entermedias server
  Future<String> getEmResponse(
      String url, dynamic jsonBody, RequestType requestType,
      {String customError = "An Error Occured"}) async {
    //Set headers
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (authenticationmanager.emUser != null) {
      String tokenKey =
          handleTokenKey(authenticationmanager.emUser!.entermediakey);
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
    http.Response response;
    try {
      http.Response? responseJson;
      if (requestType == RequestType.PUT) {
        responseJson = await httpClient.put(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      } else if (requestType == RequestType.POST) {
        responseJson = await httpClient.post(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      } else if (requestType == RequestType.GET) {
        responseJson = await httpClient.get(
          Uri.parse(url),
          headers: headers,
        );
      } else if (requestType == RequestType.DELETE) {
        responseJson = await httpClient.delete(
          Uri.parse(url),
          headers: headers,
          body: body,
        );
      }
      response = await handleException(responseJson!);
      log('test response: $response');
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
    switch (response.statusCode) {
      case 200:
        final http.Response responseJson = response;
        return responseJson;
      case 201:
        final http.Response responseJson = response;
        return responseJson;
      case 302:
        break;
      case 400:
        throw BadRequestException(response.body.toString());
      case 403:
        throw UnauthorisedException(response.body.toString());
      case 408:
        throw TimeoutException(response.body.toString());
      case 500:
        throw HttpException(response.body.toString());
      default:
        // throw FetchDataException('Error occurred while Communication with Server with StatusCode : ${response.statusCode}');
        break;
    }
  }

  /// use to upload multiple files with assign data.
  Future<http.Response> postMultiPart(String mt, String url,
      Map<String, dynamic> body, Map<String, File>? files) async {
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (authenticationmanager.emUser != null) {
      String tokenKey =
          handleTokenKey(authenticationmanager.emUser!.entermediakey);
      headers.addAll({"X-token": tokenKey});
    }
    try {
      String uri = url;
      Uri validUri = Uri.parse(uri);
      var request = CustomMultipartRequest(mt, validUri);
      request.fields['jsonrequest'] =
          Field(jsonEncode(body), 'application/json');
      files?.forEach((fieldName, file) async {
        var stream = http.ByteStream(file.openRead())..cast();
        var length = await file.length();
        var multipartFileSign = http.MultipartFile(fieldName, stream, length,
            filename: basename(file.path));
        request.files.add(multipartFileSign);
      });
      request.headers.addAll(headers);
      return await http.Response.fromStream(await request.send());
    } catch (e) {
      throw Exception(e);
    }
  }
}
