class oiChatMessage {
  late Map<String, dynamic> properties;
  late String messageid;
  late Map<dynamic, dynamic> user;
  List<Map<String, dynamic>>? goals;

  oiChatMessage(this.messageid, this.user, this.properties, {this.goals});

  void addPortrait(String portrait) {
    user["portrait"] = portrait;
  }

  oiChatMessage.fromJson(Map<String, dynamic> json) {
    messageid = json["id"];
    user = json["user"];
    properties = json;
    List goalsList = json["goals"] ?? [];
    for (var goal in goalsList) {
      goals = [];
      goals!.add(Map<String, dynamic>.from(goal));
    }
  }
}
