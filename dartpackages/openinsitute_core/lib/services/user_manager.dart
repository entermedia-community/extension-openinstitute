import 'package:openinsitute_core/Helper/request_type.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:get/get.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

class UserManager {
  static String usersBox = "usersBox";

  DataModule? userModule;
  DataModule? userProfileModule;

  OpenI get oi {
    return Get.find();
  }

  createDataModule() async {
    userModule = await oi.datamanager.getDataModule("user");
  }

  toggleBlockUser(String id) async {
    userProfileModule = await oi.datamanager.getDataModule("userprofile");
    return await userProfileModule!.createModuleOperation(
        "block",
        RequestType.POST,
        {"field": "blockedusers", "profilepreference.value": id});
  }

  Future<emData> loadBlockedUsers() async {
    userProfileModule = await oi.datamanager.getDataModule("userprofile");
    return await userProfileModule!
        .getData(oi.authenticationmanager.emUser!.userid);
  }

  deleteUser() async {
    userProfileModule = await oi.datamanager.getDataModule("userprofile");
    return await userProfileModule!.createModuleOperation("deleteall",
        RequestType.POST, {"userid": oi.authenticationmanager.emUser!.userid});
  }

  Future<void> saveUser(emData user) async {
    await oi.hivemanager.saveData(user.id, user.properties, usersBox);
  }

  Future<void> saveUsers(List<emData> users) async {
    for (var user in users) {
      await saveUser(user);
    }
  }

  Future<emData> getUser(String id) async {
    await createDataModule();
    return await userModule!.getData(id);
  }

  Future<emData?> getUserFromHive(String id) async {
    Map<String, dynamic> map = await oi.hivemanager.getData(id, usersBox);
    if (map != null) {
      return emData.fromJson(map);
    }
  }

  Future<List<emData>> getUsersFromHive() async {
    List<Map<String, dynamic>> cache =
        await oi.hivemanager.getAllHits(usersBox);
    List<emData> usersList = [];
    for (var user in cache) {
      usersList.add(emData.fromJson(user));
    }
    return usersList;
  }
}