library openinsitute_core;

import 'dart:async' show Future, TimeoutException;
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

import 'package:flutter/services.dart' show rootBundle;
import 'package:get/get.dart';
import 'package:openinsitute_core/Helper/customException.dart';
import 'package:openinsitute_core/models/emUser.dart';

class OpenI {
  Map? _settings;
  EmUser? emUser;

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

  Map? get settings{
    if (_settings == null) {
      loadAppSettings();
    }
    return _settings;
  }


  Future<void> initialize() async {
    await loadAppSettings();
    Get.put<OpenI>(this);
  }

  Future<Map?> loadAppSettings() async {
    if (_settings == null) {
      String json = await rootBundle.loadString("system/appsettings.json");
      print("loaded ${json}");
      _settings = jsonDecode(json);
    }
    return _settings;
  }

  //TODO: Add prefix before the token
  String handleTokenKey(String token) {
    final String newToken = /*"em" + */ "$token";
    return newToken;
  }

  //Generic post method to entermedias server
  Future<Map?> postEntermedia(String url, dynamic jsonBody,
      {String customError = "An Error Occured"}) async {
    //Set headers
    Map<String, String> headers = <String, String>{};
    headers.addAll({"X-tokentype": "entermedia"});
    headers.addAll({"Content-type": "application/json"});
    if (this.emUser != null) {
      String tokenKey = handleTokenKey(this.emUser!.results.entermediakey);
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
      var responseJson;
      print("isPutMethod: $isPutMethod");
      if (isPutMethod != null && isPutMethod == true) {
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
      //save local emUser from response object
      this.emUser = emUserFromJson(json.encode(resMap));
      return this.emUser;
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
      //save local emUser from response object
      this.emUser = emUserFromJson(json.encode(resMap));
      return this.emUser;
    } else {
      return null;
    }
  }

  //Entermedia Login with key pasted in
  Future<bool?> emEmailKey(String email) async {
    this.emUser = null;
    // tempKey = null;
    // final resMap = await postEntermedia(EMFinder + '/services/authentication/sendmagiclink.json', {"to": email}, context);
    final resMap = await postEntermedia(app!["mediadb"] + '/services/authentication/emailonlysendmagiclinkfinish.json', {"to": email},);
    print("Sending email to..." + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return null;
    }
  }
}
