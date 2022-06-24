import 'package:firebase_auth/firebase_auth.dart';
import 'package:openinsitute_core/models/emUser.dart';
import 'package:openinsitute_core/openinsitute_core.dart';
import 'package:openinsitute_core/services/sharedpreferences.dart';
import 'package:get/get.dart';

//firebase log in code - Mando
class AuthenticationManager {
  AuthenticationManager(this._firebaseAuth);
  final FirebaseAuth _firebaseAuth;
  Stream<User?> get authStateChanges => _firebaseAuth.authStateChanges();
  EmUser? emUser;
  String? email;
  OpenI get oi {
    return Get.find();
  }

  Future<void> signOut() async {
    await _firebaseAuth.signOut();
  }

  //Entermedia Login with key pasted in
  Future<bool?> emEmailKey(String email) async {
    emUser = null;
    // tempKey = null;
    // final resMap = await postEntermedia(EMFinder + '/services/authentication/sendmagiclink.json', {"to": email}, context);
    final resMap = await oi.postEntermedia(
        oi.app!["mediadb"] +
            '/services/authentication/emailonlysendmagiclinkfinish.json',
        {"to": email},);
    print("Sending email to..." + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }

  // todo; Entermedia send 6 digit code to email.
  Future<bool?> emEmailLoginCode(String email) async {
    emUser = null;
    this.email = email;
    // tempKey = null;
    final resMap = await oi.postEntermedia(
        oi.app!["mediadb"] + '/services/authentication/sendmagiclink.json',
        {"to": email},);
    print("Sending email with login code to..." + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }

  //todo; Create NEW user and send 6 digit temp code to email.
  Future<bool?> emCreateNewUser(
      String email, String firstName, String lastName) async {
    emUser = null;

    final resMap = await oi.postEntermedia(
        oi.app!["mediadb"] + '/services/authentication/sendnewuseremail.json',
        {"email": email, "firstName": firstName, "lastName": lastName},);
    print("Creating new user " + firstName + " with email: " + email);
    if (resMap != null) {
      var loggedin = true;
      return loggedin;
    } else {
      return false;
    }
  }

  //This gets the Entermedia user information called when logging in. - Mando Oct 14th 2020
  Future<EmUser?> firebaseLogin(String email, String password) async {
    final resMap = await oi.postEntermedia(
      oi.app!["mediadb"] + '/services/authentication/firebaselogin.json',
      {"email": email, "password": password},
      customError: "Invalid credentials. Please try again!",
    );
    print("Logging in");
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser!.firebasepassword = results["firebasepassword"];
      return emUser;
    } else {
      return null;
    }
  }

  Future<EmUser?> login(String id, String password) async {
    final resMap = await oi.postEntermedia(
        oi.app!["mediadb"] + '/services/authentication/login.json',
        {"id": id, "password": password},
        customError: "Invalid credentials. Please try again!");
    print("Logging in");
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser = EmUser.fromJson(results);
      print("complete");
      await sharedPref.saveEmUser(emUser!);
      return emUser;
    } else {
      print("login failed");
      return null;
    }
  }

  //todo; Entermedia login with 6 digit code from email. Returns EM User.
  Future<EmUser?> loginCode(String logincode) async {
    await signOut();
    final resMap = await oi.postEntermedia(
        oi.app!["mediadb"] + '/services/authentication/login.json',
        {"email": email, "templogincode": logincode},
        customError: "Invalid credentials. Please try again!");
    print("Logging in with code: " + logincode);
    if (resMap != null) {
      Map<String, dynamic> results = resMap["results"];
      emUser = EmUser.fromJson(results);
      print("complete");
      emUser = await firebaseLogin(email!, emUser!.entermediakey);
      await signIn(email: email!, password: emUser!.firebasepassword!);
      await sharedPref.saveEmUser(emUser!);
      return emUser;
    } else {
      print("login failed");
      return null;
    }
  }

  // todo: logout
  Future<bool?> logout() async {
    emUser = null;
    await sharedPref.resetValues();
    return true;
  }

  Future<bool> isAuthenticated() async {
    emUser ??= await sharedPref.getEmUser();
    return emUser != null;
  }

  Future<String?> signIn(
      {required String email, required String password}) async {
    try {
      await signOut();
      await _firebaseAuth.signInWithEmailAndPassword(
          email: email, password: password);
      //Navigator.push(context, MaterialPageRoute(builder: (context) => WorkspaceSelect())); //something else can do this?  Seems to specific for a lib
      return "Signed In";
    } on FirebaseAuthException catch (e) {
      return e.message;
    } catch (e) {
      print(e);
      return "Unknown error";
    }
  }
}
