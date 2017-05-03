import { Component } from '@angular/core';
import {NavController, Platform} from 'ionic-angular';
import {Coordinates, Geolocation} from '@ionic-native/geolocation';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  public pos: Coordinates;

  constructor(public navCtrl: NavController, private geolocation: Geolocation, private platform: Platform) {
    this.platform.ready().then(() => this.getGeolocation())
  }

  private getGeolocation() {
    this.geolocation.getCurrentPosition().then((res) => {
      this.pos = res.coords;
    }).catch((error) => {
      console.log('Error getting location', error);
    });
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
