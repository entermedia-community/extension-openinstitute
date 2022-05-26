import 'package:get/get.dart';
import 'package:openinsitute_core/models/emData.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/emDataManager.dart';

OpenI get oi {
  return Get.find();
}

class ChatManager {

  Future<List<emData>> getRemoteChatMessages(String inProjectId) async {

    final responsestring = await oi.getEmResponse(oi.app!["mediadb"] + '/services/projectsd/chat/${inProjectId}');
    List<emData> results = parseData(responsestring);
    // Searcher searcher = await oi.datamanager.getSearcher("chatterbox");
    // searcher.syncData(false);
    // Map chatsearch = {
    //   "page": "1",
    //   "hitsperpage": "20",
    //   "query": {
    //     "terms": [
    //       {"field": "channel", "operation": "exact", "value": inChannelId}
    //     ]
    //   }
    // };
    // return searcher.getRemoteData(chatsearch);
  }
}
