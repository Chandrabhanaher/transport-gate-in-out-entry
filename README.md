# transport gate In-Out entry
Android JSON, Volley libary with PHP web services
```
Scan vehicle no and create qr code gate in time.
Using Blutooth Printer

```
using manifest 
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />  
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />


## dependencies
```
    implementation 'com.google.zxing:core:3.2.1'
    implementation 'com.journeyapps:zxing-android-embedded:3.4.0'
    implementation 'com.google.android.gms:play-services-vision:10.2.4'
    implementation files('libs/itextpdf-5.5.5.jar')    
    implementation 'com.mcxiaoke.volley:library:1.0.19'
     
     
 
## http library
```
      useLibrary 'org.apache.http.legacy'
