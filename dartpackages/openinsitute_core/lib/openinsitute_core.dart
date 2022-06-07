library openinsitute_core;

import 'dart:async' show Future, TimeoutException;
import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart' show rootBundle;
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:openinsitute_core/Helper/customException.dart';
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/models/taskList.dart';
import 'package:openinsitute_core/services/authentication.dart';
import 'package:openinsitute_core/services/emDataManager.dart';
import 'package:openinsitute_core/services/emSocketManager.dart';
import 'package:openinsitute_core/services/message_manager.dart';
import 'package:openinsitute_core/services/oiChatManager.dart';
import 'package:openinsitute_core/services/sharedpreferences.dart';

//import 'contact.dart';

class OpenI {

  Map? _settings;
  EmUser? emUser; 
  DataManager? dataManager;
  oiChatManager? chatManager;
  EmSocketManager? socketManager;
  String? email;
  MessageManager? messageManager;


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
  MessageManager get messagemanager => Get.find<MessageManager>();

  Future<void> initialize() async {
    await loadAppSettings();
    Get.put<OpenI>(this,permanent: true);

    dataManager = DataManager();
    Get.put<DataManager>(dataManager!,permanent: true);

    chatManager = oiChatManager();
    Get.put<oiChatManager>(chatManager!,permanent: true);

    socketManager = EmSocketManager();
    Get.put<EmSocketManager>(socketManager!,permanent: true);
    chatManager = oiChatManager();
    Get.put<oiChatManager>(chatManager!,permanent: true);
    messageManager = MessageManager();
    Get.put<MessageManager>(messageManager!,permanent: true);
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
    if (emUser != null) {
      String tokenKey = handleTokenKey(emUser!.entermediakey);
      headers.addAll({"X-token": tokenKey});
    }
    //make API post
    final response = await httpRequest(
      requestUrl: url,
      body: json.encode(jsonBody),
      headers: headers,
      customError: customError,
    );
    if (response != null && response.statusCode == 200) {
      print("Success user info is:" + response.body);
      final String responseString = response.body;
      return json.decode(responseString);
    } else {
      return null;
    }
  }


  //Generic post method to entermedias server
  Future<String> getEmResponse(String url, dynamic jsonBody,
      {String customError = "An Error Occured"}) async {
    //Set headers
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (emUser != null) {
      String tokenKey = handleTokenKey(emUser!.entermediakey);
      headers.addAll({"X-token": tokenKey});
    } else{
      //TODO: Remove this ASAP
      String tokenKey = handleTokenKey("adminmd5421c0af185908a6c0c40d50fd5e3f16760d5580bc");
      headers.addAll({"X-token": tokenKey});

    }
    //make API post
    final response = await httpRequest(
      requestUrl: url,
      body: json.encode(jsonBody),
      headers: headers,
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
    bool isPutMethod = false,
    String customError = "Some Error",
  }) async {
    String url = requestUrl;
      print(url);
    http.Response response;
    try {
      http.Response responseJson;
        print("isPutMethod: $isPutMethod");
      if (isPutMethod == true) {
        print("isPutMethod: $isPutMethod");
        responseJson = await http.put(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      } else {
        responseJson = await http.post(
          Uri.parse(url),
          body: body,
          headers: headers,
        );
      }
      print(responseJson.statusCode);
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

//This gets the Entermedia user information called when logging in. - Mando Oct 14th 2020
  Future<EmUser?> firebaseLogin(String email, String password) async {
    final resMap = await postEntermedia(
        app!["mediadb"] + '/services/authentication/firebaselogin.json',
        {"email": email, "password": password},
        customError: "Invalid credentials. Please try again!");
    print("Logging in");
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser!.firebasepassword = results["firebasepassword"];
      return emUser;
    } else {
      return null;
    }
  }

  Future<EmUser?> login(String id, String password) async {
    final resMap = await postEntermedia(
        app!["mediadb"] + '/services/authentication/login.json',
        {"id": id, "password": password},
        customError: "Invalid credentials. Please try again!");
    print("Logging in");
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser = EmUser.fromJson(results);
      print("complete");
      await sharedPref.saveEmUser(emUser!);
      return emUser;
    } else {
      print("login failed");
      return null;
    }
  }





  //Entermedia Login with key pasted in
  Future<bool?> emEmailKey(String email) async {
    emUser = null;
    // tempKey = null;
    // final resMap = await postEntermedia(EMFinder + '/services/authentication/sendmagiclink.json', {"to": email}, context);
    final resMap = await postEntermedia(app!["mediadb"] + '/services/authentication/emailonlysendmagiclinkfinish.json', {"to": email},);
    print("Sending email to..." + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }



  // todo; Entermedia send 6 digit code to email.
  Future<bool?> emEmailLoginCode(String email) async {
    this.emUser = null;
    this.email = email;
    // tempKey = null;
    final resMap = await postEntermedia(app!["mediadb"] + '/services/authentication/sendmagiclink.json', {"to": email},);
    print("Sending email with login code to..." + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }

  //todo; Create NEW user and send 6 digit temp code to email.
  Future<bool?> emCreateNewUser(String email, String firstName, String lastName) async {
    this.emUser = null;

    final resMap = await postEntermedia(app!["mediadb"] + '/services/authentication/sendnewuseremail.json',
      {
      "email": email,
      "firstName": firstName,
      "lastName": lastName
      },
    );
    print("Creating new user " + firstName + " with email: " + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }

  //todo; Entermedia login with 6 digit code from email. Returns EM User.
  Future<EmUser?> loginCode( String logincode) async {
    await AuthenticationService.instance.signOut();
    final resMap = await postEntermedia(
        app!["mediadb"] + '/services/authentication/login.json',
        {
          "email": email,
          "templogincode": logincode

        },
        customError: "Invalid credentials. Please try again!");
    print("Logging in with code: " + logincode);
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser = EmUser.fromJson(results);
      print("complete");
      emUser  = await firebaseLogin(email!, emUser!.entermediakey);
      await AuthenticationService.instance.signIn(email: email!, password: emUser!.firebasepassword! );
      await sharedPref.saveEmUser(emUser!);
      return emUser;
    } else {
      print("login failed");
      return null;
    }
  }

  // todo: logout
  Future<bool?> logout() async {
    await sharedPref.resetValues();
    return true;
  }



  Future<List<ToDo>> getOpenTasks() async {
    final responsestring = await getEmResponse(app!["mediadb"] + '/services/users/tasks/mytasks.json', {"goaltrackerstaff": "admin"},);
    Map<String, dynamic> results = json.decode(responsestring);
    return results["tickets"].map<ToDo>((json) => ToDo.fromJson(json)).toList();

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


  Future<bool> isAuthenticated() async {
    emUser ??= await sharedPref.getEmUser();
    return emUser != null;
  }
}