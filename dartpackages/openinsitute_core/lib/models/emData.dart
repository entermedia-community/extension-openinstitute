class emData{

    late String id;
    String? name;
    late Map <String, dynamic> properties;

    emData(this.id, this.name, this.properties);


    emData.fromJson(Map<String, dynamic> json){
        id = json["id"];

        name = json["name"]?["en"] ?? "No Name";
        properties = json;
    }

    Map<String, dynamic> toJson()  {
        properties[id] = id;
        return properties;
    }




}