import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/oiChatMessage.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

import 'dart:convert';

import 'package:hive_flutter/hive_flutter.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/models/emFeeds.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

OpenI get oi {
  return Get.find();
}

class oiChatManager {



  Future<List> getUserProjects() async {

    Map params = {
      "page": "1",
      "hitsperpage": "200"
    };

    var box =  await getBox("oicache");
   var results =  box.get("viewprojects");//Some cache system
    if( results == null) {
      results = <emData>[]; //Make one list that is cached
      box.put("viewprojects",results);
    }

    //TODO; Call this part in an async way
    final Map? responded = await oi.postEntermedia(oi.app!["mediadb"] + '/services/module/librarycollection/viewprojects.json', params) as Map?;

    //TODO: How do I create emChatMessages from json?
    List<emData> messages =
    responded!["results"]!.map<emData>((json) => emData.fromJson(json)).toList();

    results.clear();
    results.addAll(messages); //TODO: This should reload the UI with new entries?
    return Future.value(results);
  }

  Future<List> getProjectChatMessages(String inProjectId)  async {
    var box =  await getBox("oiChatManagerCache");


     var results =  box.get(inProjectId);//Some cache system
     if( results == null) {
       results = <oiChatMessage>[]; //Make one list that is cached
       box.put(inProjectId,results);
     }

    Map params = {
       "page": "1",
       "hitsperpage": "20",
       "collectionid": inProjectId
     };

     //TODO; Call this part in an async way
    final Map? responded = await oi.postEntermedia(oi.app!["mediadb"] + '/services/module/librarycollection/viewmessages.json', params) as Map?;

     //TODO: How do I create emChatMessages from json?
     List<oiChatMessage> messages =
     responded!["results"]!.map<oiChatMessage>((json) => oiChatMessage.fromJson(json)).toList();

     results.clear();
     results.addAll(messages); //TODO: This should reload the UI with new entries?
      return Future.value(results);
     //return results;
    //List<emData> results = parseData(responsestring);
    }

    Future<List> sendMessage(String inProjectId, Map params)  async {
    var box =  await getBox("oiChatManagerCache");


     var results =  box.get(inProjectId);//Some cache system
     if( results == null) {
       results = <oiChatMessage>[]; //Make one list that is cached
       box.put(inProjectId,results);
     }

    final Map? responded = await oi.postEntermedia(oi.app!["mediadb"] + '/services/module/librarycollection/savemessage.json', params) as Map?;

     List<oiChatMessage> messages =
     responded!["results"]!.map<oiChatMessage>((json) => oiChatMessage.fromJson(json)).toList();

     results.clear();
     results.addAll(messages);
       box.put(inProjectId,results);
      return Future.value(results);
    }
  }

 Future<Box>  getBox(String inType) async  {
       if (!Hive.isBoxOpen(inType)) {
         return  Hive.openBox(inType);
       } else {
         return Hive.box(inType);
       }
  }

