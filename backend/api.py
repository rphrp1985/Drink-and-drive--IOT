import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
# python -m flask --app speechapi run --host=0.0.0.0
import os
import io
from flask import Flask, flash, request, redirect, url_for
from flask_restful import Resource, Api
from werkzeug.utils import secure_filename
import speech_recognition as sr
import subprocess
import os
import requests
import random
import pandas as pd
# from pydub import AudioSegment
from pydub import AudioSegment
from datetime import datetime
from datetime import date
import requests
# import pandas as pd 
import numpy as np
from sklearn.ensemble import RandomForestRegressor


now = datetime.now()

# from flask_socketio import SocketIO, send, emit

UPLOAD_FOLDER = '/files'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif', 'ogg', 'mp3', 'wav'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

api = Api(app)

cred = credentials.Certificate('key.json')

app1 = firebase_admin.initialize_app(cred)

db = firestore.client()

data_frame = pd.read_csv('data.csv')



class FileHandler(Resource):

    def informEmegencynumber(self,num,x):
        URL='https://api.callmebot.com/whatsapp.php'
        msg = "*Car number "+x['car_num']+"*       hey, I am drunk and driving. Please help me. I am at   "+x['address']
        maplink = "        https://www.google.com/maps/search/?api=1&query="+str(x['long'])+","+str(x['lat'])
        msg = msg + " " + maplink
        PARAMS = {'phone':num, 'text':msg,'apikey':7196447}
  
# sending get request and saving the response as response object
        r = requests.get(url = URL, params = PARAMS)
        print("sent msg to emergency number")
        # data =sdata)
  
# extracting data in js

    def getCarLocation(self, car_num):
        users_ref = db.collection(u'location').document(car_num)
        dict= users_ref.get().to_dict()
        print(dict)
        x= {}
        x['address']= dict['address']
        x['car_num']= dict['car_num']
        x['lat']= dict['latitude']
        x['long']= dict['longitude']
        current_time = now.strftime("%H:%M:%S")
        x['time']= current_time
        today = date.today()
        x['date']= today
        # print(x)
        data_frame.loc[len(data_frame.index)] = [dict['address'], dict['car_num'], dict['latitude'],dict['longitude'],current_time,today] 
        print(data_frame.head())
        data_frame.to_csv('data.csv', index=False)
        self.informEmegencynumber(dict['eme_num'],x)



        

    def get(self):
        print('inside get')
        x=request.args.get("car_num")
        self.getCarLocation(x)

        print(x)
        return "Drink and Drive Reported Successfully"


class Predict(Resource):
     def get(self):
        address= request.args.get("address")
        day = request.args.get("day")

        print('inside get of predict')
        df= pd.read_csv('data.csv')
        df['hour'] = pd.to_datetime(df['time']).dt.hour
        df['weekday'] = pd.to_datetime(df['date']).dt.weekday
        x=df[['lat','long','weekday']]
        y= df['hour']
        regressor = RandomForestRegressor(n_estimators=100, random_state=0)
        regressor.fit(x, y)
        
        adress= df['address']
        lat= df['lat']
        long= df['long']
        weekday= df['weekday']  

        a1=[]
        lat1=[]
        long1=[]
        weekday1=[]
        i =0
        while i<len(adress):
            if adress[i] == '40, Vidyasagar St, Machuabazar, Kolkata, West Bengal 700009, India':
                a1.append(adress[i])
                lat1.append(lat[i])
                long1.append(long[i])
                weekday1.append(day)
                break
            i=i+1

        df2= pd.DataFrame({'lat':lat1,'long':long1,'weekday':weekday1})
        result= regressor.predict(df2)
        abs = int(result[0])
        print("predicted time is  "+ str(abs) +" - "+ str((abs+1)))
        return  str(abs) +" - "+ str(abs+1)



    

        



         

   



api.add_resource(FileHandler, "/")
api.add_resource(Predict, "/predict")



app.run()



