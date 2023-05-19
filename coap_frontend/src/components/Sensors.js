import SensorCard from './SensorCard'
import { useEffect, useState } from 'react'

const Sensors = () => {
    const [sensors, setSensors] = useState([])

    const [numSensors, setNumSensors] = useState(0)
    const [numAddSensors, setNumAddSensors] = useState(0)

    const handleNumSensorsChange = (event) => {
        setNumAddSensors(event.target.value)
    }

    const handleConfirmClick = () => {
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                number: numAddSensors,
            }),
        }

        fetch('http://localhost:8080/api/addSensor', requestOptions)
            .then((data) => console.log(data))
            .catch((err) => console.error(err))
    }

    useEffect(() => {
        const eventSource = new EventSource('http://localhost:8080/api/observe')
        eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data)

            const newSensors = []

            for (var i = 0; i < data.result; i++) {
                const sensor = data.data[i]

                const existingSensor = sensors.filter(
                    (el) => el.id == data.data[i].id
                )

                const newData = {
                    value: sensor.value,
                    time: sensor.lastUpdate,
                }

                const newSensorData = {
                    id: sensor.id,
                    lastValue: sensor.value,
                    isRunning: sensor.isRunning,
                }

                if (existingSensor.length > 0) {
                    existingSensor[0].data.push(newData)
                    newSensorData.data = existingSensor[0].data
                } else {
                    newSensorData.data = [newData]
                }

                newSensors.push(newSensorData)
            }

            setSensors(newSensors)

            setNumSensors(data.result)
        }
        return () => {
            eventSource.close()
        }
    }, [])

    return (
        <>
            <h1>Welcome to Sensor Control Panel</h1>
            <div className="container">
                <h2>Sensor Count: {numSensors}</h2>
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
    )
}

export default Sensors

// [
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.128",
//         "id": "Sensor006",
//         "value": -2.6481835124872593
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.125",
//         "id": "Sensor004",
//         "value": 2.6309952973121806
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.126",
//         "id": "Sensor0010",
//         "value": 33.83873553054168
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.128",
//         "id": "Sensor002",
//         "value": 30.42385485990907
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.124",
//         "id": "Sensor008",
//         "value": 12.768898448197287
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.124",
//         "id": "Sensor001",
//         "value": -0.783284037103023
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.130",
//         "id": "Sensor005",
//         "value": 15.276791823586414
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.128",
//         "id": "Sensor003",
//         "value": 21.18015966495717
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.129",
//         "id": "Sensor007",
//         "value": 27.45240004887934
//     },
//     {
//         "isRunning": true,
//         "lastUpdate": "2023-05-07 17:43:41.128",
//         "id": "Sensor009",
//         "value": -7.572861392188226
//     }
// ]
