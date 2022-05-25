// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emChatMessage.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class emChatMessageAdapter extends TypeAdapter<emChatMessage> {
  @override
  final int typeId = 1;

  @override
  emChatMessage read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return emChatMessage(
      chatroomId: fields[0] as String?,
      id: fields[1] as String?,
      text: fields[2] as String?,
      timestamp: fields[3] as String?,
      sent: fields[4] as bool?,
      senderId: fields[5] as String?,
      senderName: fields[6] as String?,
      senderImageUrl: fields[7] as String?,
    );
  }

  @override
  void write(BinaryWriter writer, emChatMessage obj) {
    writer
      ..writeByte(8)
      ..writeByte(0)
      ..write(obj.chatroomId)
      ..writeByte(1)
      ..write(obj.id)
      ..writeByte(2)
      ..write(obj.text)
      ..writeByte(3)
      ..write(obj.timestamp)
      ..writeByte(4)
      ..write(obj.sent)
      ..writeByte(5)
      ..write(obj.senderId)
      ..writeByte(6)
      ..write(obj.senderName)
      ..writeByte(7)
      ..write(obj.senderImageUrl);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is emChatMessageAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
