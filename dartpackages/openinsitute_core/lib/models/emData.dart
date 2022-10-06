class emData {
  late String id;
  late String name;
  late Map<dynamic, dynamic> properties;

  emData(this.id, this.name, this.properties);

  emData.fromJson(Map<dynamic, dynamic> json) {
    id = json['id'] ?? json["userid"] ?? "";
    var langname = json["name"];
    if (langname == null) {
      name = json["screenname"] ?? "No Name";
    } else if (langname.runtimeType == String) {
      name = langname;
    } else if (langname["en"] != null) {
      name = langname["en"];
    }
    properties = json;
  }

  Map<dynamic, dynamic> toJson() {
    return properties;
  }

  @override
  String toString() => 'emData(id: $id, name: $name, properties: $properties)';
}
