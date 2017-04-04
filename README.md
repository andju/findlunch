### Konfiguration/ReadMe FindLunch
### Stand: 31.03.2017
### Maximilian Haag
### Mail: mhaag@hm.edu
------------------------------------
# Beschreibung:
Bestehendes FindLunch Projekt, erweitert um Google Firebase Cloud Messaging (FCM), sowie Amazon SNS Cloud Messaging (SNS/ADM). Integrierte Klassen zur Ausführung von Messungen für beide Push-Messaging Provider.

------------------------------------
## 1. Webserver-Applikation (webapp)
Basiert auf folgenden Technologien:
- Spring Boot
- Spring Data JPA Hibernate
- Spring MVC
- Thymeleaf Template Engine
- Bootstrap
- Amazon Java SDK 1.11.65

Benötigt konfigurierte Datenbank (MariaDB etc.) und Webserver.
Einstellungen in der Spring Suite in "application.properties" (Pfad: src/main/resources).

### Sonstiges:
Domain:
- Voreingestellte Domain: findlunch.de

SSL:
- Benötigt signiertes SSL Zertifikat (Projekt beinhaltet SSL.com Zertifikat, gültig bis 08.05.2017).
- Temporärer Betrieb ohne SSL mit entsprechenden Änderungen möglich.

Port:
- Voreingestellter Port: 22001.

Messung/Laufender Betrieb:
- Messung getrennt von laufendem Betrieb.
- Zur Ausführung einer Messung muss der Scheduled Task in "PushNotificationScheduledTask" (dort beschrieben) deaktiviert werden.
- "PushMeasureBase" im Package "measurement" anpassen und gewünschte Messung aktivieren.
- Entsprechende merkierte Parameter in "PushMeasureBase" festlegen.
- Benötigt registrierte bzw. eingetragene PushNotification in Datenbank mit Titel "testpush".
- PushNotification benötigt Token von entsprechendem, zu messenden Service.
- Mehrere "testpush" in Datenbank von verschiedenen Usern für skalierte Messung mit mehreren Geräten.
- Messungen werden in "/MeasureLog.txt" geloggt.

Push-Messaging Service Credentials:
- Credentials für Messung/Live-Betrieb müssen im Ordner: /src/main/ressources angelegt sein (vorkonfiguriert).

Firebase + Amazon SNS: LiveOpCredentials.conf und MeasureCredentials.conf
```sh
FCM_ID (Serverschlüssel)
AWS_Client_ID
AWS_Client_Secret
AWS_APP_Name
AWS_Endpoint_Userdata
```

Nur Amazon IAM: AwsCredentials.properties
```sh
Amazon Access Key:
accessKey:xxxxxxxxxx
Amazon Secret Key:
secretKey:xxxxxxxxxx

```

### Import in Spring Tool Suite:
1. File -> Import -> General -> Existing Projects into Workspace
2. Projektordner auswählen
3. Finish

------------------------------------
## 2. Datenbank (database)
- Datenbankschema aus /database muss in MariaDB erstellt werden.
- Datenbankverbindung in Webserver-Applikation per "application.properties" konfigurieren (src/main/resources).

------------------------------------
## 3. Mobile-Applikation/Android App (FindLunchApp)
Basiert auf folgenden Technologien:
- Spring for Android
- Jackson
- Google Play services Maps
- Google Maps Android API utility library
- Google FCM (Google Firebase Messaging)
- Amazon SNS (Amazon ADM/SNS Messaging)

### Sonstiges:
Messung/Laufender Betrieb:
- Zur Ausführung einer Messung müssen die entsprechenden Parameter in "MeasureProcessing" im Package "measure" angepasst werden.
- Für den laufenden Betrieb muss die Messung dort deaktiviert sein.

Push-Messaging Service Credentials:
- Firebase (direkt von Firebase Portal):
```sh
/app/google-services.json

```
- Amazon (aus Amazon Dev Portal): 
```sh
/app/src/assets/api_key.txt
```

### Import in Android Studio:
1. File -> Open
2. Mobile_Applikation auswählen -> OK

------------------------------------
Um Fehler zu vermeiden, bitte vor dem ersten Start einmal "Clean Project" ausführen.

### Repository:
https://github.com/maxhaag/FindLunch

### Folders:
- /database
- /webapp
- /FindLunchApp
