import 'dart:convert';

import 'package:hive/hive.dart';
import 'package:openinsitute_core/models/emChatMessage.dart';
part 'emChatroom.g.dart';

@HiveType(typeId: 0)
class emChatroom extends HiveObject {
  @HiveField(0)
  String? id;
  @HiveField(1)
  String? name;
  @HiveField(2)
  String? imageUrl;
  @HiveField(3)
  emChatMessage? recentChat;
  @HiveField(4)
  int? unReadChat;
  emChatroom({
    this.id,
    this.name,
    this.imageUrl,
    this.recentChat,
    this.unReadChat,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'imageUrl': imageUrl,
      'recentChat': recentChat?.toMap(),
      'unReadChat': unReadChat,
    };
  }

  factory emChatroom.fromMap(Map<String, dynamic> map) {
    return emChatroom(
      id: map['id'],
      name: map['name'],
      imageUrl: map['imageUrl'],
      recentChat: map['recentChat'] != null
          ? emChatMessage.fromMap(map['recentChat'])
          : null,
      unReadChat: map['unReadChat']?.toInt(),
    );
  }

  String toJson() => json.encode(toMap());

  factory emChatroom.fromJson(String source) =>
      emChatroom.fromMap(json.decode(source));
}
