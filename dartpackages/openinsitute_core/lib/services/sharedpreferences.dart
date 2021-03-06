import 'dart:convert';

import 'package:openinsitute_core/models/emUser.dart';
import 'package:shared_preferences/shared_preferences.dart';

class sharedPref {
  static saveEMKey(String key) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('entermediakey', key);
  }

  static getEMKey() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //Return String
    String? stringValue = prefs.getString('entermediakey');
    return stringValue;
  }
  static saveEmUser(EmUser emUser) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('emUser', emUser.toJson());
  }

  static Future<EmUser?> getEmUser() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? stringValue = prefs.getString('emUser');
    if(stringValue == null){
      return null;
    }
    return EmUser.fromJson(jsonDecode(stringValue));
  }

  static saveWorkspaceKey(String key) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('workspacekey', key);
  }

  static getWorkspaceKey() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //Return String
    String? stringValue = prefs.getString('workspacekey');
    return stringValue;
  }

  saveRecentWorkspace(int colId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setInt('recent', colId);
  }

  clearRecentWorkspace() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.remove("recent");
  }

  getRecentWorkspace() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //Return index of workspace
    int? intValue = prefs.getInt('recent');
    return intValue;
  }

  setRecentEntity(String entity) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('recentEntity', entity);
  }

  getRecentEntity() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //Return String
    String? stringValue = prefs.getString('recentEntity');
    return stringValue;
  }

  clearRecentEntity() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.remove("recentEntity");
  }

  //todo; EX:Handle null int intValue= await prefs.getInt('intValue') ?? 0;
  //todo; EX: Check value SharedPreferences prefs = await SharedPreferences.getInstance();
  //todo; bool CheckValue = prefs.containsKey('value');
  //todo; containsKey will return true if persistent storage contains the given key and false if not.

  static resetValues() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    //Remove entermediakey
    prefs.remove("entermediakey");
    //Remove workspacekey
    prefs.remove("workspacekey");
    //Remove most recent workspace ID
    prefs.remove("recent");
    // remove emUser
    prefs.remove("emUser");
  }
}
