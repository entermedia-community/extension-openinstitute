



import 'package:get/get.dart';
import 'package:hive/hive.dart';
import 'package:openinsitute_core/models/emChatMessage.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

OpenI get oi {
  return Get.find();
}
class ChatManager {
  late Box chatMessages;
   Future<List<emChatMessage>> getChatMessagesFromHive(String inChatroomId) async {
    chatMessages = await Hive.openBox<emChatMessage>(inChatroomId);
    return chatMessages.values.toList() as List<emChatMessage>;
  }
  Future<void> saveChatMessageInHive(emChatMessage chatMessage) async {
    chatMessages = await Hive.openBox<emChatMessage>(chatMessage.chatroomId!);
    chatMessages.put(chatMessage.timestamp!, chatMessage);
  }
}
