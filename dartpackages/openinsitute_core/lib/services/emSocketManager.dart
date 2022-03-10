import 'package:get/get.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class EmSocketManager {

  WebSocketChannel? channel;


  OpenI get oi {
    return Get.find();
  }



  connect() async {
    String url = "${oi.app!["websocket_url"]}?entermedia.key=${oi.emUser!.entermediakey}";
    channel = WebSocketChannel.connect(Uri.parse(url));

    channel!.stream.listen((data) {
      print("!!!!new msg: $data");

    });

    //handleMessages(channel!.stream);

  }


  sendMessage(Map inMessage) async{
      if(channel == null ){
        await connect();
      }
      channel!.sink.add(inMessage);

  }
  sendString(String inString) async{
    if(channel == null ){
      await connect();
    }
    channel!.sink.add(inString);

  }




}
