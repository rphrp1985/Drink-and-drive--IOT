import pandas as pd

pd = pd.DataFrame({'address':[], 'car_num': [], 'lat': [], 'long': [], 'time': [], 'date': []})
pd.to_csv('data.csv', index=False)