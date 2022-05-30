class oiChatMessage {

  late Map <String, dynamic> properties;
  late String messageid;
  late Map user;

  oiChatMessage(this.messageid, this.user,this.properties);

  oiChatMessage.fromJson(Map<String, dynamic> json){
    messageid = json["id"];
    user = json["user"];
    properties = json;
  }


}