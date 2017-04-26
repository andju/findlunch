import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  constructor(public navCtrl: NavController) {

  }

  sendToWeb() {
    // Add Serverkey and Webtoken here
    var webKey = 'SERVERKEY-API';
    var toWeb = 'WEB-TOKEN';

    let notification = {
      'title': 'Bestellung XY eingegangen',
      'body': 'Kunde: Maximilian Mustermann',
      'icon': '/assets/icon/favicon.ico',
      'click_action': 'http://localhost:8100'
    };

    fetch('https://fcm.googleapis.com/fcm/send', {
      'method': 'POST',
      'headers': {
        'Authorization': 'key=' + webKey,
        'Content-Type': 'application/json'
      },
      'body': JSON.stringify({
        'notification': notification,
        'to': toWeb
      })
    }).then(function(response) {
      console.log(response);
    }).catch(function(error) {
      console.error(error);
    })

  }

  sendToAndroid() {
    // Add Serverkey and Android Token
    var androidKey = 'SERVERKEY-API';
    var toAndroid = 'ANDROID-TOKEN';

    let notification = {
      'title': 'Bestätigung der Bestellung XY',
      'body': 'Abholbereit in ca. 20 min',
      'icon': 'firebase-logo.png',
      'click_action': 'http://localhost:8100'
    };

    fetch('https://fcm.googleapis.com/fcm/send', {
      'method': 'POST',
      'headers': {
        'Authorization': 'key=' + androidKey,
        'Content-Type': 'application/json'
      },
      'body': JSON.stringify({
        'notification': notification,
        'to': toAndroid
      })
    }).then(function(response) {
      console.log(response);
    }).catch(function(error) {
      console.error(error);
    })

  }

}
