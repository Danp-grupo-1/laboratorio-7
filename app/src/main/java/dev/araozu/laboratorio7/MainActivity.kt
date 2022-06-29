package dev.araozu.laboratorio7

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.araozu.laboratorio7.ui.theme.Laboratorio7Theme

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: CustomSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = CustomSensorManager(getSystemService(SENSOR_SERVICE) as SensorManager)

        setContent {
            Laboratorio7Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(sensorManager)
                }
            }
        }
    }

    override fun onDestroy() {
        sensorManager.unregister()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(manager: CustomSensorManager) {
    Scaffold(
        topBar = { TopBar() }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            AccelerometerCard(manager)
            MagnetometerCard(manager)
            OrientationCard(manager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerometerCard(manager: CustomSensorManager) {
    var accX by remember { mutableStateOf(0f) }
    var accY by remember { mutableStateOf(0f) }
    var accZ by remember { mutableStateOf(0f) }

    manager.registerAccelerometerCallback { x, y, z ->
        accX = x
        accY = y
        accZ = z
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column {
                Text("Acelerómetro")
                Text("x: $accX")
                Text("y: $accY")
                Text("z: $accZ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagnetometerCard(manager: CustomSensorManager) {
    var magX by remember { mutableStateOf(0f) }
    var magY by remember { mutableStateOf(0f) }
    var magZ by remember { mutableStateOf(0f) }

    manager.registerMagnetometerCallback { x, y, z ->
        magX = x
        magY = y
        magZ = z
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column {
                Text("Magnetómetro")
                Text("x: $magX")
                Text("y: $magY")
                Text("z: $magZ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientationCard(manager: CustomSensorManager) {
    var orientation by remember { mutableStateOf("") }
    manager.registerOrientationCallback { s -> orientation = s }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Text(text ="Orientación: $orientation")
        }
    }
}

@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Laboratorio 7") },
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    )
}
