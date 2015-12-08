#!/usr/bin/python
from firebase import firebase
firebase = firebase.FirebaseApplication('https://burning-fire-7618.firebaseio.com', None)
firebase.authentication = firebase.Authentication('hggG9AcHbass1X2AcuaRFGRZ8tUvOqBrIdqmDIHo', 'andreg@kth.se', extra={'id' : 123})
in_file = open(":~/TfsScanner/app/build/outputs/apk/app-debug.apk", "rb") # opening for [r]eading as [b]inary
data = in_file.read() # if you only wanted to read 512 bytes, do .read(512)
in_file.close()

data = {'filename' : 'TfsScanner.apk',  'size' : len(data), 'data' : data}
firebase.post("/", "apk" data)