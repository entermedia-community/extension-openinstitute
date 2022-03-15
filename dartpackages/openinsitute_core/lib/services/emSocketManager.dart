import 'package:get/get.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class EmSocketManager {

  WebSocketChannel? channel;
  WebSocketChannel? chat;



  OpenI get oi {
    return Get.find();
  }



  connect() async {
    String url = "${oi.app!["websocket_url"]}?entermedia.key=${oi.emUser!.entermediakey}";
    channel = WebSocketChannel.connect(Uri.parse(url));
    channel!.stream.listen((data) {
      print("!!!!new msg: $data");

    });

    String chaturl =  "${oi.app!["chat_socket_url"]}?entermedia.key=${oi.emUser!.entermediakey}";
    chat = WebSocketChannel.connect(Uri.parse(chaturl));
    chat!.stream.listen((data) {
      print("!!!!new msg: $data");

    });
    //handleMessages(channel!.stream);

  }

  Stream<dynamic> get stream {
    return channel!.stream;
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
