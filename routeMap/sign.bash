#!/bin/bash

/usr/local/jdk1.7.0_25/bin/jarsigner -storepass android -keystore ~/.android/debug.keystore -digestalg SHA1 TraceEditor.apk playstore
read
~/Android/Sdk/build-tools/22.0.1/zipalign -v 4 TraceEditor.apk TraceEditor_signed.apk
read
/usr/local/jdk1.8.0_65/bin/jarsigner -verify -verbose -certs TraceEditor_signed.apk
