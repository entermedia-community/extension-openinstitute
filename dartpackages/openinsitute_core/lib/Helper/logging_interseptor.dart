import 'dart:developer';

import 'package:http_interceptor/http_interceptor.dart';
import 'package:flutter/foundation.dart' as Foundation;

class LoggingInterceptor implements InterceptorContract {
  @override
  Future<RequestData> interceptRequest({required RequestData data}) async {
    if (Foundation.kDebugMode) {
      log("----> Request");
      log("${data.method} URL: ${data.baseUrl}");
      log("Headers: ${data.headers}");
      log("Params: ${data.params}");
      log("Body: ${data.body}");
    }
    return data;
  }

  @override
  Future<ResponseData> interceptResponse({required ResponseData data}) async {
    if (Foundation.kDebugMode) {
      log("<---- Response  ${data.statusCode}");
      log("URL: ${data.url}");
      log("Headers: ${data.headers}");
      log("Body: ${data.body}");
    }
    return data;
  }
}
