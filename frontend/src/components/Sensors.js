import SensorCard from "./SensorCard";
import { useEffect, useState } from "react";

const Sensors = () => {
  const [sensors, setSensors] = useState([]);
  const [delay, setDelay] = useState(0);
  const [cpu, setCpu] = useState(0);
  const [ram, setRam] = useState(0);

  const [numSensors, setNumSensors] = useState(0);
  const [numAddSensors, setNumAddSensors] = useState(0);

  const handleNumSensorsChange = (event) => {
    setNumAddSensors(event.target.value);
  };
  // This function is executed when an event (such as a button click) occurs
  const handleConfirmClick = () => {
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        number: numAddSensors,
      }),
    };

    fetch("http://localhost:8080/api/addSensor", requestOptions)
      .then((data) => console.log(data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    const eventSource = new EventSource("http://localhost:8080/api/observe");
    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);

      const newSensors = [];

      for (var i = 0; i < data.result; i++) {
        const sensor = data.data[i];

        const existingSensor = sensors.filter((el) => el.id == data.data[i].id);

        const newData = {
          value: sensor.value,
          time: sensor.lastUpdate,
        };

        const newSensorData = {
          id: sensor.id,
          lastValue: sensor.value,
          isRunning: sensor.isRunning,
        };

        if (existingSensor.length > 0) {
          existingSensor[0].data.push(newData);
          newSensorData.data = existingSensor[0].data;
        } else {
          newSensorData.data = [newData];
        }

        newSensors.push(newSensorData);
      }

      setSensors(newSensors);

      setNumSensors(data.result);
    };
    return () => {
      eventSource.close();
    };
  }, []);

  useEffect(() => {
    const eventSource = new EventSource(
      "http://localhost:8080/api/performance"
    );
    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);

      setDelay(data.delay);
      setCpu(data.cpu);
      setRam(data.ram);
    };
    return () => {
      eventSource.close();
    };
  }, []);

  return (
    <>
      <h1>Welcome to Sensor Control Panel</h1>
      <div className="container">
        <h2>Sensor Count: {numSensors}</h2>
        <h2>Delay: {delay} ms</h2>
        {/* <h2>CPU: {cpu} %</h2>
                <h2>Ram: {delay} B</h2> */}
        <div className="sensor-count">
          <label htmlFor="numAddSensors">Add Sensors:</label>
          <input
            type="number"
            id="numAddSensors"
            name="numAddSensors"
            value={numAddSensors}
            onChange={handleNumSensorsChange}
          />
          <button className="button" onClick={handleConfirmClick}>
            Confirm
          </button>
        </div>
        {sensors.map((sensor) => (
          <SensorCard sensor={sensor} key={sensor.id} />
        ))}
      </div>
    </>
  );
};

export default Sensors;
