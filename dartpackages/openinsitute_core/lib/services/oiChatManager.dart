import 'dart:developer';
import 'package:get/get.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/oiChatMessage.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

class OiChatManager { 
  String chatBox = "oiChatManagerCache";

  DataModule? chatterBox;

  OpenI get oi {
    return Get.find();
  }

  Future<List<oiChatMessage>> getRecentMessages(
      {bool fromCache = false}) async {
    List<oiChatMessage> messages = [];
    chatterBox = await oi.datamanager
        .getDataModule("librarycollection", boxString: "Recent Chat");
    if (fromCache) {
      var datalist = chatterBox!.getAllHits();
      for (var message in datalist) {
        messages.add(oiChatMessage.fromJson(message.properties));
      }
    } else {
      Map<String, dynamic> results = await chatterBox!.createModuleOperation(
          "viewtopmessages",
          RequestType.PUT,
          {"userid": oi.authenticationmanager.emUser!.userid},
          cache: true);
      for (var message in results["results"]) {
        messages.add(oiChatMessage.fromJson(message));
      }
    }
    return messages;
  }

  createDataModule(String collectionId,
      {String moduleString = "librarycollection"}) async {
    chatterBox = await oi.datamanager
        .getDataModule(moduleString, boxString: "chatterbox_$collectionId");
  }

  Future<oiChatMessage> editChat(
      String id, oiChatMessage message, String projectId) async {
    await createDataModule(projectId);
    emData updated = await chatterBox!.updateData(id, message.properties);
    oiChatMessage updatedMessage = oiChatMessage.fromJson(updated.properties);
    return updatedMessage;
  }

  Future<List<oiChatMessage>> loadChatCache(String projectId) async {
    await createDataModule(projectId);
    List<emData> cache = await chatterBox!.getAllHits();
    List<oiChatMessage> messages = [];
    for (var e in cache) {
      messages.add(oiChatMessage.fromJson(e.properties));
    }
    return messages;
  }

  Future<List<oiChatMessage>> loadChat(String projectId, int page) async {
    await createDataModule(projectId);
    List<oiChatMessage> messages = [];
    if (page != 1 && chatterBox!.pages < page) {
      return [];
    }
    Map<String, dynamic> results = await chatterBox!.createModuleOperation(
        "viewmessages", RequestType.POST, getParams(page, projectId));
    messages.addAll(await parseData(results, projectId));
    return messages;
  }

  Future<List<oiChatMessage>> parseData(
      Map<String, dynamic> responded, String projectId) async {
    List<emData>? topics = responded["topics"]
        .map<emData>((json) => emData.fromJson(json))
        .toList();
    if (topics != null) {
      await saveTopics(topics, projectId);
    }
    List<emData> goals = responded["goals"]!
        .map<emData>((json) => emData.fromJson(json))
        .toList();
    List<oiChatMessage> messages =
        responded["results"]!.map<oiChatMessage>((json) {
      oiChatMessage chatMessage = oiChatMessage.fromJson(json);
      if (responded["users"] != null) {
        int index = responded["users"].indexWhere(
            (element) => element["userid"] == chatMessage.user["id"]);
        if (index != -1) {
          chatMessage.user["portrait"] = responded["users"][index]["portrait"];
          chatMessage.properties["user"]["portrait"] =
              responded["users"][index]["portrait"];
        }
        chatMessage.properties["goals"] = <Map<String, dynamic>>[];
        for (int i = 0; i < goals.length; i++) {
          if (goals[i].properties["chatparentid"]["id"] ==
              chatMessage.messageid) {
            chatMessage.properties["goals"]!.add(goals[i].properties);
          }
        }
      }
      return chatMessage;
    }).toList();

    responded["results"] = null;
    responded["data"] = messages
        .map(
          (e) => emData.fromJson(e.properties),
        )
        .toList();

    await chatterBox!.saveCache(responded);
    List<oiChatMessage> messageList = [];
    for (var e in responded["data"]) {
      messageList.add(oiChatMessage.fromJson(e.properties));
    }
    return messageList;
  }

  Map getParams(int page, String inProjectId) {
    return {"page": "$page", "hitsperpage": "20", "collectionid": inProjectId};
  }

  saveTopics(List<emData> topics, String projectId) async {
    await oi.hivemanager.clear(chatBox + "_" + "topics" + "_" + projectId);
    for (var topic in topics) {
      await oi.hivemanager.saveData(topic.id, topic.properties,
          chatBox + "_" + "topics" + "_" + projectId);
    }
  }

  getTopics(String projectId) async {
    List<Map<String, dynamic>> topics = await oi.hivemanager
        .getAllHits(chatBox + "_" + "topics" + "_" + projectId);
    List<emData> result = [];
    for (var topic in topics) {
      result.add(emData.fromJson(topic));
    }
    return result.toSet().toList();
  }

  Future<bool> deleteChat(String inMessageId, String projectId) async {
    await createDataModule(projectId, moduleString: "chatterbox");
    try {
      return await chatterBox!.deleteData(inMessageId);
    } catch (e) {
      log(e.toString());
      return false;
    }
  }

  Future<oiChatMessage?> saveChat(
      Map<String, dynamic> inMessage, String projectId, String tempId) async {
    await createDataModule(projectId);
    final Map<String, dynamic>? responded =
        await chatterBox!.createModuleOperation(
      'messagesave',
      RequestType.PUT,
      inMessage,
    );
    if (responded != null && responded.isNotEmpty) {
      Map<String, dynamic> map = responded;
      oiChatMessage chatMessage = oiChatMessage.fromJson(map["data"]);
      await chatterBox!.box.put(chatMessage.messageid, chatMessage.properties);
      return chatMessage;
    }
    return null;
  }

  Future<void> updateChat(String id, Map data) async {
    await createDataModule(id);
    await chatterBox!.box.put(id, data);
  }

  sendAllNonSendChat() async {
    List<Map<String, dynamic>> list =
        await oi.hivemanager.getAllHits('UnSentChat');
    for (var chat in list) {
      oiChatMessage? sentChat = await saveChat({
        "collectionid": chat['collectionid'],
        "channel": chat['channel'],
        "message": chat['message'],
        "messagestatus": "pendingbroadcast",
        "date": chat['date'],
      }, chat["collectionid"], chat['id']);
      if (sentChat != null) {
        await oi.hivemanager.removeData(chat['id'], 'UnSentChat');
        await oi.hivemanager.saveData(chat['id'], sentChat.properties,
            'chatterbox_${chat["collectionid"]}');
      }
    }
  }

  saveChatLocal(oiChatMessage chat, String projectId) async {
    await oi.hivemanager
        .saveData(chat.messageid, chat.properties, 'UnSentChat');
  }
}
