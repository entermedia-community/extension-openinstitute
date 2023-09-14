import 'dart:developer';

import 'package:http_interceptor/http_interceptor.dart';
import 'package:flutter/foundation.dart' as Foundation;

class LoggingInterceptor implements InterceptorContract {
  @override
  Future<bool> shouldInterceptRequest() async {
    return true;
  }

  @override
  Future<bool> shouldInterceptResponse() async {
    return true;
  }

  @override
  Future<BaseRequest> interceptRequest({required BaseRequest request}) async {
    if (Foundation.kDebugMode) {
      log("----> Request");
      log("${request.method} URL: ${request.url}");
      log("Headers: ${request.headers}");
      // log("Params: ${request.params}");
      log("Body: ${request.contentLength}");
    }
    return request;
  }

  @override
  Future<BaseResponse> interceptResponse(
      {required BaseResponse response}) async {
    if (Foundation.kDebugMode) {
      log("<---- Response  ${response.statusCode}");
      log("URL: ${response.request?.url}");
      log("Headers: ${response.headers}");
      log("Body: ${response.contentLength}");
    }
    return response;
  }
}
