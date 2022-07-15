import 'dart:convert';

import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/taskList.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:get/get.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

class TaskManager {
  OpenI get oi {
    return Get.find();
  }

  DataModule? projectStatus;
  DataModule? taskStatus;
  DataModule? projectDepartments;
  DataModule? taskModule;
  DataModule? goaltask;

  createModules() async {
    projectStatus = await oi.datamanager.getDataModule("projectstatus");
    taskStatus = await oi.datamanager.getDataModule("taskstatus");
    projectDepartments =
        await oi.datamanager.getDataModule("projectdepartment");
  }

  Future<Map<String, List<emData>>> getFields() async {
    Map<String, List<emData>> data = {};
    await createModules();
    data["projectstatus"] = await projectStatus!.getRemoteData({
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    }, true);
    data["taskstatus"] = await taskStatus!.getRemoteData({
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    }, true);
    data["projectdepartment"] = await projectDepartments!.getRemoteData({
      "page": "1",
      "hitsperpage": "20",
      "query": {
        "terms": [
          {"field": "id", "operation": "matches", "value": "*"}
        ]
      }
    }, true);

    return data;
  }

  editTask(emData task) async {
    taskModule ??= await oi.datamanager.getDataModule("goaltask");
    taskModule?.updateData(task.id, task.properties);
  }

  Future<List<ToDo>> getOpenTasks() async {
    final responsestring = await oi.getEmResponse(
        oi.app!["mediadb"] + '/services/users/tasks/mytasks.json',
        {},
        RequestType.POST);
    Map<String, dynamic> results = json.decode(responsestring);
    return results["tickets"].map<ToDo>((json) => ToDo.fromJson(json)).toList();
  }
}
