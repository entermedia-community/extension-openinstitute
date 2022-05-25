import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/models/emChatroom.dart';

class MessageManager {
  late Box chatRoomsBox;

  Future<void> init() async {
    await Hive.initFlutter();
    chatRoomsBox = await Hive.openBox<emChatroom>('chatRooms');
    saveChatroomInHive();
  }

  saveChatroomInHive() {
    List<emChatroom> chatrooms =
        chatroomsListJson.map((e) => emChatroom.fromMap(e)).toList();
    chatRoomsBox = Hive.box<emChatroom>('chatRooms');
    chatRoomsBox.clear();
    for (var e in chatrooms) {
      chatRoomsBox.put(e.id, e);
    }
  }

  List<emChatroom> getChatroomsFromHive() {
    return chatRoomsBox.values.toList() as List<emChatroom>;
  }

  List<emChatroom> saveNewChatRoom(emChatroom chatroom) {
    chatRoomsBox.put(chatroom.id, chatroom);
    return chatRoomsBox.values.toList() as List<emChatroom>;
  }

}

List<Map<String, dynamic>> chatroomsListJson = [
  {
    "id": "1",
    "name": "Chatroom 1",
    "imageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    "recentChat": {
      "id": "1",
      "text": "Hello",
      "senderId": "sender1",
      "timestamp": "2020-01-01T00:00:00.000Z",
      "chatroomId": "1",
      "sent": false,
      'senderName': "Sender 1",
      "senderImageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    },
    "unReadChat": 3,
  },
  {
    "id": "2",
    "name": "Chatroom 2",
    "imageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    "recentChat": {
      "id": "2",
      "text": "Hi",
      "senderId": "sender2",
      "timestamp": "2020-01-01T00:00:00.000Z",
      "chatroomId": "2",
      "sent": false,
      'senderName': "Sender 2",
      "senderImageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    },
    "unReadChat": 4,
  },
  {
    "id": "3",
    "name": "Chatroom 3",
    "imageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    "recentChat": {
      "id": "3",
      "text": "Hello",
      "senderId": "sender3",
      "timestamp": "2020-01-01T00:00:00.000Z",
      "chatroomId": "3",
      "sent": false,
      'senderName': "Sender 3",
      "senderImageUrl": "https://www.w3schools.com/howto/img_avatar.png",
    },
    "unReadChat": 5,
  }
];
