import 'dart:developer';

import 'package:get/get.dart';
import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

class ProjectManager {
  String projectBox = "viewprojects";

  OpenI get oi {
    return Get.find();
  }

  DataModule? projectsModule;

  createDataModule({String? boxString}) async {
    projectsModule = await oi.datamanager
        .getDataModule("librarycollection", boxString: boxString);
  }

  deleteProject(String id) async {
    await createDataModule();
    return await projectsModule!.deleteData(id);
  }

  updateProject(String id, Map project) async {
    await createDataModule();
    return await projectsModule!.updateData(id, project);
  }

  Future<List<emData>> loadProjectCache() async {
    await createDataModule();
    List<emData> cache = projectsModule!.getAllHits();
    return cache;
  }

  Future<emData> projectFromServer(String id) async {
    await createDataModule(boxString: "ProjectsInfo");
    // var data = projectsModule!.box.get(id);

    var data = await projectsModule!.getData(id);
    projectsModule!.box.put(id, data.properties);
    return data;
  }

  Future<emData> getProject(String id) async {
    await createDataModule();
    return projectsModule!.getDataById(id);
  }

  Future<List<emData>> loadProject(int page) async {
    await createDataModule();
    List<emData> projects = [];
    if (projectsModule!.page > projectsModule!.pages) {
      return [];
    }
    Map<String, dynamic> results = await projectsModule!.createModuleOperation(
        "viewprojects",
        RequestType.POST,
        {"page": "$page", "hitsperpage": "20"});

    results['data'] =
        results['results'].map((e) => emData.fromJson(e)).toList();
    results['totalhits'] = results['total'];
    results['pages'] = results['totalpages'];
    if (results['data'].isNotEmpty) {
      for (var item in results['data']) {
        projects.add(item);
      }
      log("load lenght: ${projects.length}");
      await projectsModule!.saveCache(results);
    }
    return projects;
  }

  Future<List<emData>> searchProjects(String name) async {
    await createDataModule();
    Map<String, dynamic> params = {
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "name", "operator": "contains", "value": name}
        ]
      }
    };

    final responded = await projectsModule!.getRemoteData(params, false);
    return responded;
  }

  Future<void> createProject(String projectName, String projectDes) async {
    await createDataModule();
    await projectsModule!.addData(
      {
        "name": projectName,
        "description": projectDes,
      },
    );
  }

  Future<List<emData>> searchYourProject(int page, String keyword) async {
    await createDataModule();
    List<emData> projects = [];
    if (projectsModule!.page > projectsModule!.pages) {
      return [];
    }
    Map<String, dynamic> results = await projectsModule!
        .createModuleOperation("viewprojects", RequestType.POST, {
      "page": "$page",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "name", "operator": "contains", "value": keyword}
        ]
      }
    });

    results['data'] =
        results['results'].map((e) => emData.fromJson(e)).toList();
    results['totalhits'] = results['total'];
    results['pages'] = results['totalpages'];
    if (results['data'].isNotEmpty) {
      for (var item in results['data']) {
        projects.add(item);
      }
      log("load lenght: ${projects.length}");
      // await projectsModule!.saveCache(results);
    }
    return projects;
  }
}
