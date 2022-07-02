import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

class ProjectManager {
  String projectBox = "viewprojects";

  OpenI get oi {
    return Get.find();
  }

  Future<void> cacheProject(List<emData> projects) async {
    // await oi.hivemanager.clear(projectBox);
    for (int i = 0; i < projects.length; i++) {
      await oi.hivemanager
          .saveData(projects[i].id, projects[i].properties, projectBox);
    }
  }

  Future<List<emData>> loadProjectCache() async {
    List<Map<String, dynamic>> cache =
        await oi.hivemanager.getAllHits(projectBox);
    List<emData> projects = [];
    for (var e in cache) {
      projects.add(emData.fromJson(e));
    }
    return projects;
  }

  Future<List<emData>> loadProject(int page) async {
    List<emData> projects = [];
    List<emData> result = await getUserProjects(page);
    if (result.isNotEmpty) {
      projects.addAll(result);
      await cacheProject(projects);
    }
    return projects;
  }

  Future<emData> getProjectById(String projectId) async {
    Map<String, dynamic> data =
        await oi.hivemanager.getData(projectId, projectBox);
    return emData.fromJson(data);
  }

  Future<List<emData>> getUserProjects(int page) async {
    Map params = {"page": "$page", "hitsperpage": "200"};
    //TODO; Call this part in an async way
    final Map? responded = await oi.postEntermedia(
      oi.app!["mediadb"] +
          '/services/module/librarycollection/viewprojects.json',
      params,
    );
    List<emData> projects = responded!["results"]!
        .map<emData>((json) => emData.fromJson(json))
        .toList();

    // for (var project in projects) {
    // List<emData> team = project.properties["team"].map((json) => emData.fromJson(json)).toList();
    // await oi.usermanager.saveUsers(team); 
    // }
    await oi.hivemanager
        .saveData("pages", responded["response"]["pages"], projectBox);
    return projects;
  }

  Future<List<emData>> searchProjects(String name) async {
    Map<String, dynamic> params = {
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "name", "operator": "contains", "value": name}
        ]
      }
    };

    final Map? responded = await oi.postEntermedia(
      oi.app!["mediadb"] + '/services/module/librarycollection/search',
      params,
    );
    List<emData> projects = responded!["results"]!
        .map<emData>((json) => emData.fromJson(json))
        .toList();
    return projects;
  }

  Future<void> createProject(String projectName, String projectDes) async {
    await oi.postEntermedia(
      oi.app!["mediadb"] + '/services/module/librarycollection/create',
      {
        "name": projectName,
      },
    );
  }
}
