import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from random import randrange
import datetime as dt
import json

fig = plt.figure(figsize=(6, 3))
x = []
y = []
ln, = plt.plot(x, y, '-')


def update(frame):
    # Fetch data
    with open('src/main/resources/output.json') as output_file:
        file_contents = output_file.read()

    sensor_data = json.loads(file_contents)
    print(sensor_data["data"][0]["value"])

    x.append(dt.datetime.now())
    y.append(sensor_data["data"][0]["value"])

    ln.set_data(x, y)
    fig.gca().relim()
    fig.gca().autoscale_view()
    return ln


animation = FuncAnimation(fig, update, interval=1000)

plt.title('Sensor value over time')
plt.ylabel('Value')
plt.xlabel('Time')

plt.show()