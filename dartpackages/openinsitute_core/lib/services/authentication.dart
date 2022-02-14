import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

//firebase log in code - Mando
class AuthenticationService {
  AuthenticationService._();
  static final AuthenticationService instance = AuthenticationService._();
  final FirebaseAuth _firebaseAuth = FirebaseAuth.instance;

  Stream<User?> get authStateChanges => _firebaseAuth.authStateChanges();

  Future<void> signOut() async {
    await _firebaseAuth.signOut();
  }

  Future<String?> signIn({required String email, required String password}) async {
    try {
      await signOut();
      await _firebaseAuth.signInWithEmailAndPassword(email: email, password: password);
      //Navigator.push(context, MaterialPageRoute(builder: (context) => WorkspaceSelect())); //something else can do this?  Seems to specific for a lib
      return "Signed In";
    } on FirebaseAuthException catch (e) {
      return e.message;
    } catch (e) {
      print(e);
    }
  }
}
