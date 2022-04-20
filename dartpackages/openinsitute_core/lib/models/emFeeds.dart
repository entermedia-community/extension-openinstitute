class emFeed{

    late String id;
    String? name;
    late Map <String, dynamic> properties;

    emFeed(this.id, this.name, this.properties);


    emFeed.fromJson(Map<String, dynamic> json){
        id = json["id"];

        name = json["name"] ?? "No Name";
        properties = json;
    }

    Map<String, dynamic> toJson()  {
        properties[id] = id;
        return properties;
    }




}