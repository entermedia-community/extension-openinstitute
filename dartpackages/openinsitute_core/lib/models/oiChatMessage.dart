class oiChatMessage {

  late Map <String, dynamic> properties;
  late String entermediakey;
  String? firstname;
  String? lastname;
  String? email;
  String? firebasepassword;
  late String userid;
  String? screenname;

  oiChatMessage(this.userid, this.email,this.properties);

  oiChatMessage.fromJson(Map<String, dynamic> json){
    userid = json["id"];
    email = json["email"];
    entermediakey = json["entermediakey"];
    screenname = json["screenname"];
    properties = json;
  }


}