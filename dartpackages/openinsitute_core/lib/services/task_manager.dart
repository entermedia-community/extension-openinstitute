import 'dart:convert';
import 'dart:developer';

import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
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
  DataModule? goalModule;

  addGoal(Map goal) async {
    goalModule = await createDataModule(goalModule, "projectgoal");

    /// [name] , [details],  [creationdate] , [resolveddate], [collectionid], [tickettype], [projectstatus], [goaltrackercolumn]
    return await goalModule!.addData(goal);
  }

  editGoal(String id, Map goal) async {
    goalModule = await createDataModule(goalModule, "projectgoal");
    return await goalModule!.updateData(id, goal);
  }

  /// required keys for add a task -> "[projectgoal]", "[goaltask] => [{we can have the data related to goaltask}]", "[projectstatus]", "[projectdepartment]", "[taskstatus]",
  Future<emData> addTask(Map task) async {
    taskModule = await createDataModule(taskModule, "goaltask");
    return await taskModule!.addData(task);
  }

  Future<bool> deleteTask(String id) async {
    try {
      taskModule = await createDataModule(taskModule, "goaltask");
      return await taskModule!.deleteData(id);
    } catch (e) {
      return false;
    }
  }

  Future<bool> deleteProjectGoal(id) async {
    try {
      goalModule = await createDataModule(goalModule, "projectgoal");
      return await goalModule!.deleteData(id);
    } catch (e) {
      return false;
    }
  }

  Future<List<dynamic>> taskRoleAdd(
      String projectId, String taskId, String roleId, String roleUserId) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "collectionid": projectId,
      "taskid": taskId,
      "collectiverole": roleId,
      "roleuserid": roleUserId,
    };
    dynamic response = await goalModule!.addTaskRole(inQuery);

    if (response['response']['status'] == 'ok' &&
        response['data']['taskroledetails'] != null) {
      return response['data']['taskroledetails'];
    } else {
      return [];
    }
  }

  Future<List<dynamic>> roleDetailsSave(String projectId, String taskId,
      String roleId, String roleUserId, String notes) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "collectionid": projectId,
      "taskid": taskId,
      "collectiverole": roleId,
      "roleuserid": roleUserId,
      "name": notes
    };
    final response = await goalModule!.saveRoleDetails(inQuery);
    if (response['response']['status'] == 'ok' &&
        response['data']['taskroledetails'] != null) {
      return response['data']['taskroledetails'];
    } else {
      return [];
    }
  }

  deleteTaskRole(
      String projectId, String taskId, String roleId, String roleUserId) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "collectionid": projectId,
      "taskid": taskId,
      "collectiverole": roleId,
      "roleuserid": roleUserId
    };

    final response = await goalModule!.removeTaskRole(inQuery);
    if (response['response']['status'] == 'ok') {
      return true;
    }
    return false;
  }

  Future<List<dynamic>> deleteTaskAction(String projectId, String id) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {"roleactionid": id};

    dynamic response = await goalModule!.removeTaskAction(inQuery);
    if (response['response']['status'] == 'ok' &&
        response['data']['taskroledetails'] != null) {
      return response['data']['taskroledetails'];
    } else {
      return [];
    }
  }

  Future<List<dynamic>> getActionLogHistory(
      String projectId, String taskId, String roleId, String roleUserId) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "collectionid": projectId,
      "taskid": taskId,
      "collectiverole": roleId,
      "roleuserid": roleUserId
    };

    final response = await goalModule!.getActionLogs(inQuery);
    List<dynamic> actions = response['actions'] ?? [];
    return actions;
  }

  addOneActionToRole(String projectId, taskId, roleId, roleUserId) async {
    await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "collectionid": projectId,
      "taskid": taskId,
      "collectiverole": roleId,
      "roleuserid": roleUserId
    };

    final response = await goalModule!.addActionRolePoint(inQuery);
    if (response['response']['status'] == 'ok') {
      return true;
    }
    return false;
  }

  loadCacheTask(String projectId) async {
    goalModule = await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");
    List<emData> tasks = goalModule!.getAllHits();
    return tasks;
  }

  loadTask(String projectId, int page) async {
    goalModule = await createDataModule(goalModule, "projectgoal",
        boxString: "projectgoal_$projectId");

    Map inQuery = {
      "page": "$page",
      "hitsperpage": "20",
      "projectgoalsortby": "creationdateDown",
      "query": {
        "terms": [
          {
            "field": "collectionid",
            "operation": "exact",
            "value": projectId,
          }
        ]
      }
    };
    // if (page > goalModule!.pages) {
    //   return [];
    // }
    List<emData> tasks = await goalModule!.getRemoteData(inQuery, true);
    return tasks;
  }

  createDataModule(DataModule? module, String moduleString,
      {String? boxString}) async {
    module =
        await oi.datamanager.getDataModule(moduleString, boxString: boxString);
    return module;
  }

  createModules() async {
    projectStatus = await oi.datamanager.getDataModule("projectstatus");
    taskStatus = await oi.datamanager.getDataModule("taskstatus");
    projectDepartments =
        await oi.datamanager.getDataModule("projectdepartment");
  }

  Future<Map<String, List<emData>>> loadFields() async {
    Map<String, List<emData>> data = {};
    await createModules();
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

  Future<List<emData>> getProjectStatus() async {
    List<emData> data = [];
    await createModules();
    data = projectStatus!.getAllHits();
    if (data.isEmpty) {
      data = await projectStatus!.getRemoteData({
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      }, true);
    }
    return data;
  }

  Future<Map<String, List<emData>>> getFields() async {
    Map<String, List<emData>> data = {};
    await createModules();
    data["projectstatus"] = projectStatus!.getAllHits();
    if (data["projectstatus"] == null || data["projectstatus"]!.isEmpty) {
      data["projectstatus"] = await projectStatus!.getRemoteData({
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      }, true);
    }
    data["taskstatus"] = taskStatus!.getAllHits();
    if (data["taskstatus"] == null || data["taskstatus"]!.isEmpty) {
      data["taskstatus"] = await taskStatus!.getRemoteData({
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      }, true);
    }
    data["projectdepartment"] = projectDepartments!.getAllHits();
    if (data["projectdepartment"] == null ||
        data["projectdepartment"]!.isEmpty) {
      data["projectdepartment"] = await projectDepartments!.getRemoteData({
        "page": "1",
        "hitsperpage": "20",
        "query": {
          "terms": [
            {"field": "id", "operation": "matches", "value": "*"}
          ]
        }
      }, true);
    }
    return data;
  }

  Future<emData> resolveTask(taskId) async {
    taskModule = await oi.datamanager.getDataModule("goaltask");
    return await taskModule!.updateData(taskId, {
      "taskstatus": "3",
    });
  }

  Future<emData> editTask(emData task) async {
    taskModule = await oi.datamanager.getDataModule("goaltask");
    log('task properties: ${task.properties}');
    return await taskModule!.updateData(task.id, task.properties);
  }

  Future<List<dynamic>> getMyTasks({String? userId}) async {
    goalModule = await oi.datamanager.getDataModule('projectgoal',
        boxString: userId != null ? 'mytasks$userId' : 'mytasks');
    return goalModule!.getAllHits();
  }

  Future<List<dynamic>> loadMyTasks({String? userId}) async {
    goalModule = await oi.datamanager.getDataModule('projectgoal',
        boxString: userId != null ? 'mytasks$userId' : 'mytasks');
    final responseString = await goalModule!.createModuleOperation(
      "mytasks",
      RequestType.POST,
      {"userid": userId ?? oi.authenticationmanager.emUser!.userid},
    );
    Map<String, dynamic> parsed = parseData(jsonEncode(responseString));
    goalModule!.saveCache(parsed);
    return parsed['data'];
  }

  Map<String, dynamic> parseData(String responseBody) {
    final Map<String, dynamic> parsed = json.decode(responseBody);
    List<emData> results =
        parsed["goals"].map<emData>((json) => emData.fromJson(json)).toList();
    Map<String, dynamic> resultData = {};
    resultData['data'] = results;
    resultData['page'] = parsed['page'] ?? parsed["response"]["page"] ?? 1;
    resultData['pages'] = parsed['pages'] ?? parsed["response"]["pages"] ?? 1;
    resultData['totalhits'] =
        parsed['totalhits'] ?? parsed["response"]["totalhits"] ?? 0;
    return resultData;
  }
}
