import {Component, ViewChild} from '@angular/core';
import {Nav, Platform} from 'ionic-angular';
import {StatusBar} from '@ionic-native/status-bar';
import {SplashScreen} from '@ionic-native/splash-screen';

import {HomePage} from '../pages/home/home';
import {ListPage} from '../pages/list/list';
import {Firebase} from "@ionic-native/firebase";

@Component({
    templateUrl: 'app.html'
})
export class MyApp {
    @ViewChild(Nav) nav: Nav;

    rootPage: any = HomePage;

    pages: Array<{ title: string, component: any }>;

    constructor(public platform: Platform, public statusBar: StatusBar, public splashScreen: SplashScreen, private firebase: Firebase) {
        this.initializeApp();

        // used for an example of ngFor and navigation
        this.pages = [
            {title: 'Home', component: HomePage},
            {title: 'List', component: ListPage}
        ];

    }

    initializeApp() {
        this.platform.ready().then(() => {
            // Okay, so the platform is ready and our plugins are available.
            // Here you can do any higher level native things you might need.
            this.statusBar.styleDefault();
            this.splashScreen.hide();

            if (this.platform.is("cordova")) {
                this.firebase.getToken()
                    .then(token => console.log(`The token is ${token}`)) // save the token server-side and use it to push notifications to this device
                    .catch(error => console.error('Error getting token', error));

                this.firebase.onTokenRefresh()
                    .subscribe((token: string) => console.log(`Got a new token ${token}`));
            }
            else {
                console.log("I know we are in the web");

                const msg = (<any>window).firebase.messaging();

                msg.useServiceWorker((<any>window).firebaseSWRegistration);

                msg.requestPermission()
                    .then(function () {
                        console.log('Notification permission granted.');
                        msg.getToken()
                            .then(function (currentToken) {
                                if (currentToken) {
                                    console.log("Current Token is:", currentToken);
                                    // sendTokenToServer(currentToken);
                                    // updateUIForPushEnabled(currentToken);
                                    msg.onMessage(function (payload) {
                                        console.log("Message received. ", payload);
                                    });
                                } else {
                                    // Show permission request.
                                    console.log('No Instance ID token available. Request permission to generate one.');
                                    // Show permission UI.
                                    // updateUIForPushPermissionRequired();
                                    // setTokenSentToServer(false);
                                }
                            })
                            .catch(function (err) {
                                console.log('An error occurred while retrieving token. ', err);
                                // showToken('Error retrieving Instance ID token. ', err);
                                // setTokenSentToServer(false);
                            });
                    })
                    .catch(function (err) {
                        console.log('Unable to get permission to notify.', err);
                    });
            }
        });
    }

    openPage(page) {
        // Reset the content nav to have just this page
        // we wouldn't want the back button to show in this scenario
        this.nav.setRoot(page.component);
    }
}
