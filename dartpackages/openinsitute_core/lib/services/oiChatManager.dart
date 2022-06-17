import 'dart:developer';

import 'package:get/get.dart';
import 'package:openinsitute_core/models/oiChatMessage.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/hive_manager.dart';

class OiChatManager {
  String chatBox = "oiChatManagerCache";
  List? fieldProjectChatChangeListeners;

  OpenI get oi {
    return Get.find();
  }

  Future<void> cacheChat(String projectId, List<oiChatMessage> messages) async {
    messages.sort(
      (a, b) => DateTime.parse(a.properties["date"])
          .compareTo(DateTime.parse(b.properties["date"])),
    );
    for (int i = 0; i < messages.length; i++) {
    await HiveManager.instance.saveData(messages[i].messageid, messages[i].properties, chatBox + "_" + projectId);
    }
  }

  Future<void> saveSingleChat(oiChatMessage chatMessage, String projectId) async {
    await HiveManager.instance.saveData(chatMessage.messageid, chatMessage.properties, chatBox + "_" + projectId);
  }

  Future<List<oiChatMessage>> loadChatCache(String projectId) async {
    List<Map<String, dynamic>> cache =
        await HiveManager.instance.getAllHits(chatBox + "_" + projectId);
    List<oiChatMessage> messages = [];
    for (var e in cache) {
      messages.add(oiChatMessage.fromJson(e));
    }
    return messages;
  }

  Future<void> loadChat(String projectId, int page) async {
    List<oiChatMessage> messages = [];
    List<oiChatMessage> result  = await getProjectChatMessages(projectId, page);
    if (result.isNotEmpty) {
      messages.addAll(result);
      await cacheChat(projectId, result);
    }
  }

  /**
   * TODO: Create a call back
   */
  // void addProjectChatChangeListener(ChatUiListener inListener)
  // {
  //   fieldProjectChatChangeListeners.add(inListener);
  // }

  /**
   * Firebase can call this when it sees that a chat event came in
   * so we can invalidate our local cache and update our list of chats
   */
  void chatMessageEdited(String inMessageId,  String inUserId) async {
    // var box = await getBox("oiChatManagerCache");

    //fieldProjectChatChangeListeners;
  }

  Map getParams(int page, String inProjectId) {
    return  {
      "page": "$page",
      "hitsperpage": "20",
      "collectionid": inProjectId
    };
  }

  Future<List<oiChatMessage>> getProjectChatMessages(String inProjectId, int page) async {
    final Map? responded = await oi.postEntermedia(
      oi.app!["mediadb"] +
          '/services/module/librarycollection/viewmessages.json',
      getParams(page, inProjectId),
    );
    List<oiChatMessage> messages = responded!["results"]!
        .map<oiChatMessage>((json) => oiChatMessage.fromJson(json))
        .toList();
    return Future.value(messages);
  }


  Future<void> saveChat(oiChatMessage inMessage,String projectId) async {
    await saveSingleChat(inMessage, projectId);
    try {
       final Map? responded = await oi.postEntermedia(
      oi.app!["mediadb"] +
          '/services/module/librarycollection/savemessage.json',
      inMessage.properties,
    );
   log("Saved chat message: " + responded.toString()); 
    } catch (e) {
      print(e);
    }
   
  }
}

