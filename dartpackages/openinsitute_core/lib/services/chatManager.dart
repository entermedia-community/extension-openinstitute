



import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

OpenI get oi {
  return Get.find();
}



class ChatManager {

  Future<List<emData>> getChatMessages() async{
    Searcher searcher = await oi.datamanager.getSearcher("chatterbox");
    searcher.syncData(true);
    return searcher.getAllHits();

  }


}