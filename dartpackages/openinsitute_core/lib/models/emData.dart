class emData{

    String? id;
    String? name;
    late Map <String, dynamic> properties;

    emData(this.id, this.name, this.properties);


    emData.fromJson(Map<String, dynamic> json){
        id = json["id"];

        var langname = json["name"];
        if( langname == null)
        {
            name = "No Name";
        }
        else if(  langname.runtimeType == String )
        {
            name = langname;
        }
        else if( langname["en"] != null )
        {
            name = langname["en"];
        }

        properties = json;
    }

    Map<String, dynamic> toJson()  {
        return properties;
    }
}