class CreateTeamModel {
  late Response response;

  CreateTeamModel({required this.response});

  CreateTeamModel.fromJson(Map<String, dynamic> json) {
    response = (json['response'] != null ? Response.fromJson(json['response']) : null)!;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.response != null) {
      data['response'] = this.response.toJson();
    }
    return data;
  }
}

class Response {
  late String status;

  Response({required this.status});

  Response.fromJson(Map<String, dynamic> json) {
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    return data;
  }
}
