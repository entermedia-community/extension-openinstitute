import 'package:flutter_test/flutter_test.dart';
import 'package:get/get.dart';
import 'package:openinsitute_core/openinsitute_core.dart';

void main() {


  TestWidgetsFlutterBinding.ensureInitialized(); //Nasty!
  final oi = OpenI();
  Get.put<OpenI>(oi);
  oi.initialize();




  test('Load Settings', () async {
    Map? settings = await oi.loadAppSettings();
    expect(settings!.isNotEmpty, true);

    var url = "${settings!['dev']?['websocket_url']}";
    print(url);

    expect(url.isNotEmpty, true);
  });


  test('Login to OpenInstitute', () async {

  });


}
