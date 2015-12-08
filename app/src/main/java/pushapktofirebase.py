#!/usr/bin/python
from firebase import firebase
import os
import base64
import time

fb = firebase.FirebaseApplication('https://burning-fire-7618.firebaseio.com', None)
#fb.authentication = firebase.Authentication('hggG9AcHbass1X2AcuaRFGRZ8tUvOqBrIdqmDIHo', 'andreg@kth.se', extra={'id' : 123})
#os.chdir('~/TfsScanner/app/build/outputs/apk')
in_file = open('app/build/outputs/apk/app-debug.apk', 'rb') # opening for [r]eading as [b]inary
data = in_file.read() # if you only wanted to read 512 bytes, do .read(512)
in_file.close()
fb.delete('/apk', '')

millis = int(round(time.time() * 1000))
data = {'filename' : 'TfsScanner.apk',  'timestamp' : millis, 'data' : base64.standard_b64encode(data)}
fb.post("/apk", {"apk":data})
