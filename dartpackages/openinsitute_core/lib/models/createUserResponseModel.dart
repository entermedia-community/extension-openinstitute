class CreateUserResponseModel {
  late Response response;
  late CreateUserData data;

  CreateUserResponseModel({required this.response, required this.data});

  CreateUserResponseModel.fromJson(Map<String, dynamic> json) {
    if (json['response'] != null) {
      response = new Response.fromJson(json['response']);
    }
    data = (json['data'] != null ? new CreateUserData.fromJson(json['data']) : null)!;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.response != null) {
      data['response'] = this.response.toJson();
    }
    if (this.data != null) {
      data['data'] = this.data.toJson();
    }
    return data;
  }
}

class Response {
  late String status;
  late String id;

  Response({required this.status, required this.id});

  Response.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    data['id'] = this.id;
    return data;
  }
}

class CreateUserData {
  late String id;
  late String name;
  late String enabled;
  late String password;
  late String creationdate;

  CreateUserData({required this.id, required this.name, required this.enabled, required this.password, required this.creationdate});

  CreateUserData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    enabled = json['enabled'];
    password = json['password'];
    creationdate = json['creationdate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['enabled'] = this.enabled;
    data['password'] = this.password;
    data['creationdate'] = this.creationdate;
    return data;
  }
}
