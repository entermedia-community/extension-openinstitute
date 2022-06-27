import 'dart:convert';
import 'dart:developer';

import 'package:get/get.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/oiChatMessage.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

class OiChatManager {
  String chatBox = "oiChatManagerCache";

  OpenI get oi {
    return Get.find();
  }

  Future<void> cacheChat(String projectId, List<oiChatMessage> messages) async {
    messages.sort(
      (a, b) => DateTime.parse(a.properties["date"])
          .compareTo(DateTime.parse(b.properties["date"])),
    );
    for (int i = 0; i < messages.length; i++) {
    await oi.hivemanager.saveData(messages[i].messageid, messages[i].properties, chatBox + "_" + projectId);
    }
  }

 
  Future<void> saveSingleChat(oiChatMessage chatMessage, String projectId) async {
    await  oi.hivemanager.saveData(chatMessage.messageid, chatMessage.properties, chatBox + "_" + projectId);
  }  




  Future<List<oiChatMessage>> loadChatCache(String projectId) async {
    List<Map<String, dynamic>> cache =
        await  oi.hivemanager.getAllHits(chatBox + "_" + projectId);
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
    // get topics from json 
    List<emData> topics = responded!["topics"]!.map<emData>((json) => emData.fromJson(json)).toList(); 
    await saveTopics(topics, inProjectId);
    List<oiChatMessage> messages = responded["results"]!
        .map<oiChatMessage>((json) => oiChatMessage.fromJson(json))
        .toList();
    return Future.value(messages);
  }

  saveTopics(List<emData> topics, String projectId) async {
    // Delete topics from hive 
    await oi.hivemanager.clear(chatBox +"_"+ "topics" + "_" + projectId);
    for (var topic in topics) {
      await oi.hivemanager.saveData(topic.id, topic.properties, chatBox +"_"+ "topics" + "_" + projectId);
    }
  }

  getTopics(String projectId) async {
    List<Map<String, dynamic>> topics =  await oi.hivemanager.getAllHits(chatBox +"_"+ "topics" + "_" + projectId);
    List<emData> result = [];
    for (var topic in topics) {
      result.add(emData.fromJson(topic));
    }
    return result;
  }


  Future<oiChatMessage?> saveChat(Map<String, dynamic> inMessage,String projectId) async {
    // await saveSingleChat(inMessage, projectId);
       final String? responded = await oi.getEmResponse(
      oi.app!["mediadb"] +
          '/services/module/librarycollection/messagesave',
      inMessage,
      RequestType.PUT
    );
   log("Saved chat message: " + responded.toString());
   if(responded != null ) {
   Map<String, dynamic> map = jsonDecode(responded);
    oiChatMessage chatMessage = oiChatMessage.fromJson(map["data"]); 
   saveSingleChat(chatMessage, projectId);
   return chatMessage;
  } 
  return null; 
  } 
}

