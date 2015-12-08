#!/usr/bin/python
from firebase import firebase
import os
fb = firebase.FirebaseApplication('https://burning-fire-7618.firebaseio.com', 'hggG9AcHbass1X2AcuaRFGRZ8tUvOqBrIdqmDIHo')
#fb.authentication = firebase.Authentication('hggG9AcHbass1X2AcuaRFGRZ8tUvOqBrIdqmDIHo', 'andreg@kth.se', extra={'id' : 123})
#os.chdir('~/TfsScanner/app/build/outputs/apk')
in_file = open('app-debug.apk', 'rb') # opening for [r]eading as [b]inary
data = in_file.read() # if you only wanted to read 512 bytes, do .read(512)
in_file.close()

data = {'filename' : 'TfsScanner.apk',  'size' : len(data), 'data' : data}
fb.post("/", {"apk":'test'}, {'print':'silent'}, {'X_FANCY_HEADER':'VERY_FANCY'})
