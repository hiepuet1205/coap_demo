import { useEffect, useState } from 'react'
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
} from 'recharts'
import moment from 'moment'

const SensorCard = (props) => {
    const sensor = props.sensor
    const [data, setData] = useState([])
    const [showChart, setShowChart] = useState(false)

    useEffect(() => {
        setData((prevData) => [...prevData, sensor.data[0]])
        // console.log(data)
    }, [sensor])

    const handleToggle = () => {
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: sensor.id,
                isRunning: !sensor.isRunning,
                value: sensor.lastValue,
            }),
        }
// Get log output to debug
        fetch('http://localhost:8080/api/toggleSensor', requestOptions)
            .then((data) => console.log(data))
            .catch((err) => console.error(err))
    }

    const handleToggleChart = () => {
        setShowChart((prev) => !prev)
    }

    return (
        <div className="card">
            <h2>{sensor.id}</h2>
            <p>
                Value:{' '}
                <span id="sensor1-value">{sensor.lastValue.toFixed(2)}</span>
            </p>
            {showChart && (
                <LineChart width={400} height={300} data={data}>
                    <XAxis
                        dataKey="time"
                        tickCount={3}
                        tickFormatter={(time) =>
                            moment(time).format('mm:ss.SSS')
                        }
                    />
                    <YAxis />
                    <CartesianGrid strokeDasharray="3 3" />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="value" stroke="#8884d8" />
                </LineChart>
            )}

            <button
                className={`button ${showChart ? 'off' : 'on'}`}
                id="sensor1-button"
                onClick={handleToggleChart}
            >
                {showChart ? 'Chart Off' : 'Chart On'}
            </button>
            <button
                className={`button ${sensor.isRunning ? 'off' : ''}`}
                id="sensor1-button"
                onClick={handleToggle}
            >
                {sensor.isRunning ? 'Turn Off' : 'Turn On'}
            </button>
        </div>
    )
}

export default SensorCard
