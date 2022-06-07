
import 'package:hive_flutter/adapters.dart';
import 'package:openinsitute_core/models/emChatroom.dart';
import 'package:openinsitute_core/models/emChatMessage.dart';

class HiveService {
  static const assets = "assets";
  static const hivechatrooms = "chatrooms";

  HiveService._();

  static HiveService instance = HiveService._();

  init() async {
   await Hive.initFlutter();
   Hive.registerAdapter(emChatroomAdapter());
   Hive.registerAdapter(emChatMessageAdapter());
  }

  Future<Box> openHiveBox(String boxString) async {
     return await Hive.openBox(boxString);
  }

  setBox(String boxString){
    return Hive.box(boxString);
  }
  
  saveData(String id, dynamic data, String boxString) {
    Box box;
    box = setBox(boxString);
    box.put(id, data); 
  }

  dynamic getData(String id,String boxString) {
    Box box;
    box = setBox(boxString);
    box.get(id); 
  }

  bool isExist(String id,String boxString) {
    Box box;
    box = setBox(boxString);
    return box.containsKey(id); 
  }
}