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

  late Box box = Hive.openBox("oiChatManagerCache") as Box;

  List<oiChatMessage> getProjectChatMessages(String inProjectId)  {

     List<oiChatMessage>? results =  box.get(inProjectId);//Some cache system
     if( results == null) {
       results = []; //Make one list that is cached
       box.put(inProjectId,results);
     }

     Map params = {
       "page": "1",
       "hitsperpage": "20",
       "collectionid": inProjectId
     };

     //TODO; Call this part in an async way
    final Map? responded = oi.postEntermedia(oi.app!["mediadb"] + '/services/module/librarycollection/viewmessages.json', params) as Map?;

     //TODO: How do I create emChatMessages from json?
     List<oiChatMessage> messages =
     responded!["results"]!.map<oiChatMessage>((json) => emData.fromJson(json)).toList();

     results.clear();
     results.addAll(messages); //TODO: This should reload the UI with new entries?
     return results;
    //List<emData> results = parseData(responsestring);
    }
  }

  Future<Box> getBox(String inType) async {
       if (!Hive.isBoxOpen(inType)) {
         return await Hive.openBox(inType);
       } else {
         return Hive.box(inType);
       }
  }

