import 'dart:convert';

import 'package:hive/hive.dart';
part 'emChatMessage.g.dart';


@HiveType(typeId: 1)
class emChatMessage extends HiveObject {
  @HiveField(0)
  String? chatroomId;
  @HiveField(1)
  String? id;
  @HiveField(2)
  String? text;
  @HiveField(3)
  String? timestamp;
  @HiveField(4)
  bool? sent;
  @HiveField(5)
  String? senderId;
  @HiveField(6)
  String? senderName;
  @HiveField(7)
  String? senderImageUrl;
  emChatMessage({
    this.chatroomId,
    this.id,
    this.text,
    this.timestamp,
    this.sent,
    this.senderId,
    this.senderName,
    this.senderImageUrl,
  });

  Map<String, dynamic> toMap() {
    return {
      'chatroomId': chatroomId,
      'id': id,
      'text': text,
      'timestamp': timestamp,
      'sent': sent,
      'senderId': senderId,
      'senderName': senderName,
      'senderImageUrl': senderImageUrl,
    };
  }

  factory emChatMessage.fromMap(Map<String, dynamic> map) {
    return emChatMessage(
      chatroomId: map['chatroomId'],
      id: map['id'],
      text: map['text'],
      timestamp: map['timestamp'],
      sent: map['sent'],
      senderId: map['senderId'],
      senderName: map['senderName'],
      senderImageUrl: map['senderImageUrl'],
    );
  }

  String toJson() => json.encode(toMap());

  factory emChatMessage.fromJson(String source) =>
      emChatMessage.fromMap(json.decode(source));

  // save in hive database using box reference
}
