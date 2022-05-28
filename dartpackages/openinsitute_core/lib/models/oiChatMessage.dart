class oiChatMessage {

  late Map <String, dynamic> properties;
  late String messageid;
  late String userid;

  oiChatMessage(this.messageid, this.userid,this.properties);

  oiChatMessage.fromJson(Map<String, dynamic> json){
    messageid = json["id"];
    userid = json["userid"];
    properties = json;
  }


}