import datetime as dt

start_date = "2019-05-05"
end_date = "2019-05-05 8:10:11"

print (dt.datetime.strptime( start_date, "%Y-%m-%d"))
print (dt.datetime.strptime( end_date, "%Y-%m-%d %H:%M:%S"))
print (type(dt.datetime.strptime( end_date, "%Y-%m-%d %H:%M:%S")))
