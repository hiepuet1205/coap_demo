import matplotlib
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib.animation import FuncAnimation
import datetime as dt
import json

fig = plt.figure()
x = []
y = []
ln, = plt.plot(x, y, '-')
formatter = matplotlib.dates.DateFormatter('%H:%M:%S')


def update(frame):
    # Fetch data
    with open('src/main/resources/output.json') as output_file:
        file_contents = output_file.read()

    sensor_data = json.loads(file_contents)

    x.append(dt.datetime.now())
    y.append(sensor_data["data"][0]["value"])

    print(dt.datetime.now().strftime('%H:%M:%S.%f') + ' ' + str(sensor_data["data"][0]["value"]))

    ln.set_data(x, y)
    fig.gca().relim()
    fig.gca().autoscale_view()
    return ln,


animation = FuncAnimation(fig, update, interval=1000)

fig.gca().xaxis.set_major_formatter(formatter)
plt.gcf().autofmt_xdate()
plt.title('Sensor value over time')
plt.ylabel('Value')
plt.xlabel('Time')

plt.show()