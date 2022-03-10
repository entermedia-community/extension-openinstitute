import 'dart:convert';
import 'package:get/get.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class EmSocketManager {

  WebSocketChannel? channel;


  OpenI get oi {
    return Get.find();
  }



  connect() async {
    String url = oi.app!["websocket"];
    channel = WebSocketChannel.connect(Uri.parse(url));
    handleMessages(channel!.stream);

  }


  sendMessage(Map inMessage){

  }



  Future<dynamic> handleMessages(Stream<dynamic> stream) async {
    await for (var value in stream) {
      print(value);
    }
  }
}
