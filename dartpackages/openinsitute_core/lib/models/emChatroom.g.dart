// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emChatroom.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class emChatroomAdapter extends TypeAdapter<emChatroom> {
  @override
  final int typeId = 0;

  @override
  emChatroom read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return emChatroom(
      id: fields[0] as String?,
      name: fields[1] as String?,
      imageUrl: fields[2] as String?,
      recentChat: fields[3] as emChatMessage?,
      unReadChat: fields[4] as int?,
    );
  }

  @override
  void write(BinaryWriter writer, emChatroom obj) {
    writer
      ..writeByte(5)
      ..writeByte(0)
      ..write(obj.id)
      ..writeByte(1)
      ..write(obj.name)
      ..writeByte(2)
      ..write(obj.imageUrl)
      ..writeByte(3)
      ..write(obj.recentChat)
      ..writeByte(4)
      ..write(obj.unReadChat);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is emChatroomAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
