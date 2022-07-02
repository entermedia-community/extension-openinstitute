import 'package:openinsitute_core/models/emData.dart';

class oiChatMessage {

  late Map <String, dynamic> properties;
  late String messageid;
  late Map<dynamic,dynamic> user;

  oiChatMessage(this.messageid, this.user,this.properties);

  void addPortrait(String portrait ) {
    user["portrait"] = portrait;
  }

  oiChatMessage.fromJson(Map<String, dynamic> json){
    messageid = json["id"];
    user = json["user"];
    properties = json;
  }
}