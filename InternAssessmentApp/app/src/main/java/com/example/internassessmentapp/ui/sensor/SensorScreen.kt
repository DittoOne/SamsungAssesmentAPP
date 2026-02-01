package com.example.internassessmentapp.ui.sensor

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internassessmentapp.viewmodel.SensorViewModel
import kotlin.math.*
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(
    onBack: () -> Unit,
    viewModel: SensorViewModel = viewModel()
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    )

    // Start listening when screen opens
    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Multi-Sensor Dashboard", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${viewModel.activeSensorCount} sensors active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Accelerometer Section
                SensorCard(
                    title = "Accelerometer",
                    icon = Icons.Default.Vibration,
                    isAvailable = viewModel.isAccelerometerAvailable.value,
                    color = Color(0xFF6366F1)
                ) {
                    AccelerometerVisualization(
                        x = viewModel.accelerometerX.floatValue,
                        y = viewModel.accelerometerY.floatValue,
                        z = viewModel.accelerometerZ.floatValue
                    )
                    SensorDataRow("X-axis", viewModel.accelerometerX.floatValue, "m/s²")
                    SensorDataRow("Y-axis", viewModel.accelerometerY.floatValue, "m/s²")
                    SensorDataRow("Z-axis", viewModel.accelerometerZ.floatValue, "m/s²")
                    SensorDataRow(
                        "Magnitude",
                        sqrt(
                            viewModel.accelerometerX.floatValue * viewModel.accelerometerX.floatValue +
                                    viewModel.accelerometerY.floatValue * viewModel.accelerometerY.floatValue +
                                    viewModel.accelerometerZ.floatValue * viewModel.accelerometerZ.floatValue
                        ),
                        "m/s²"
                    )
                }

                // Gyroscope Section
                SensorCard(
                    title = "Gyroscope",
                    icon = Icons.AutoMirrored.Filled.RotateRight,
                    isAvailable = viewModel.isGyroscopeAvailable.value,
                    color = Color(0xFFEC4899)
                ) {
                    GyroscopeVisualization(
                        x = viewModel.gyroscopeX.floatValue,
                        y = viewModel.gyroscopeY.floatValue,
                        z = viewModel.gyroscopeZ.floatValue
                    )
                    SensorDataRow("X-rotation", viewModel.gyroscopeX.floatValue, "rad/s")
                    SensorDataRow("Y-rotation", viewModel.gyroscopeY.floatValue, "rad/s")
                    SensorDataRow("Z-rotation", viewModel.gyroscopeZ.floatValue, "rad/s")
                }

                // Magnetic Field Section
                SensorCard(
                    title = "Magnetic Field",
                    icon = Icons.Default.Explore,
                    isAvailable = viewModel.isMagneticFieldAvailable.value,
                    color = Color(0xFF10B981)
                ) {
                    CompassVisualization(
                        x = viewModel.magneticX.floatValue,
                        y = viewModel.magneticY.floatValue,
                        z = viewModel.magneticZ.floatValue
                    )
                    SensorDataRow("X-field", viewModel.magneticX.floatValue, "μT")
                    SensorDataRow("Y-field", viewModel.magneticY.floatValue, "μT")
                    SensorDataRow("Z-field", viewModel.magneticZ.floatValue, "μT")
                    SensorDataRow(
                        "Total field",
                        sqrt(
                            viewModel.magneticX.floatValue * viewModel.magneticX.floatValue +
                                    viewModel.magneticY.floatValue * viewModel.magneticY.floatValue +
                                    viewModel.magneticZ.floatValue * viewModel.magneticZ.floatValue
                        ),
                        "μT"
                    )
                }

                // Light Sensor Section
                SensorCard(
                    title = "Light Sensor",
                    icon = Icons.Default.LightMode,
                    isAvailable = viewModel.isLightAvailable.value,
                    color = Color(0xFFF59E0B)
                ) {
                    LightVisualization(lightLevel = viewModel.lightLevel.floatValue)
                    SensorDataRow("Illuminance", viewModel.lightLevel.floatValue, "lux")
                }

                // Proximity Sensor Section
                SensorCard(
                    title = "Proximity Sensor",
                    icon = Icons.Default.Sensors,
                    isAvailable = viewModel.isProximityAvailable.value,
                    color = Color(0xFF8B5CF6)
                ) {
                    ProximityVisualization(distance = viewModel.proximityDistance.floatValue)
                    SensorDataRow("Distance", viewModel.proximityDistance.floatValue, "cm")
                }

                // Pressure Sensor Section
                SensorCard(
                    title = "Pressure Sensor",
                    icon = Icons.Default.CloudQueue,
                    isAvailable = viewModel.isPressureAvailable.value,
                    color = Color(0xFF06B6D4)
                ) {
                    PressureVisualization(pressure = viewModel.pressure.floatValue)
                    SensorDataRow("Atmospheric Pressure", viewModel.pressure.floatValue, "hPa")
                    SensorDataRow(
                        "Altitude (approx)",
                        44330 * (1 - (viewModel.pressure.floatValue / 1013.25).pow(0.1903)).toFloat(),
                        "m"
                    )
                }

                // Temperature Sensor Section
                SensorCard(
                    title = "Temperature Sensor",
                    icon = Icons.Default.Thermostat,
                    isAvailable = viewModel.isTemperatureAvailable.value,
                    color = Color(0xFFEF4444)
                ) {
                    TemperatureVisualization(temperature = viewModel.temperature.floatValue)
                    SensorDataRow("Temperature", viewModel.temperature.floatValue, "°C")
                    SensorDataRow(
                        "Fahrenheit",
                        viewModel.temperature.floatValue * 9 / 5 + 32,
                        "°F"
                    )
                }

                // Humidity Sensor Section
                SensorCard(
                    title = "Humidity Sensor",
                    icon = Icons.Default.WaterDrop,
                    isAvailable = viewModel.isHumidityAvailable.value,
                    color = Color(0xFF3B82F6)
                ) {
                    HumidityVisualization(humidity = viewModel.humidity.floatValue)
                    SensorDataRow("Relative Humidity", viewModel.humidity.floatValue, "%")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    icon: ImageVector,
    isAvailable: Boolean,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusBadge(isAvailable)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isAvailable) {
                content()
            } else {
                Text(
                    text = "Sensor not available on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StatusBadge(isAvailable: Boolean) {
    Surface(
        color = if (isAvailable) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isAvailable) Color(0xFF10B981) else Color(0xFFEF4444))
            )
            Text(
                text = if (isAvailable) "Active" else "Unavailable",
                style = MaterialTheme.typography.labelSmall,
                color = if (isAvailable) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        }
    }
}

@Composable
fun SensorDataRow(label: String, value: Float, unit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "%.2f %s".format(value, unit),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Visualization Components
@Composable
fun AccelerometerVisualization(x: Float, y: Float, z: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 8.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = size.width.coerceAtMost(size.height) / 3

        // Draw circle
        drawCircle(
            color = Color(0xFF6366F1).copy(alpha = 0.1f),
            radius = maxRadius,
            center = Offset(centerX, centerY)
        )

        // Draw position indicator
        val scale = 10f
        val indicatorX = centerX + (x / scale) * maxRadius
        val indicatorY = centerY + (y / scale) * maxRadius

        drawCircle(
            color = Color(0xFF6366F1),
            radius = 12f,
            center = Offset(indicatorX, indicatorY)
        )

        // Draw line from center
        drawLine(
            color = Color(0xFF6366F1).copy(alpha = 0.5f),
            start = Offset(centerX, centerY),
            end = Offset(indicatorX, indicatorY),
            strokeWidth = 2f
        )
    }
}

@Composable
fun GyroscopeVisualization(x: Float, y: Float, z: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "gyro")

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp)
    ) {
        val barWidth = size.width / 5
        val values = listOf(
            Pair("X", abs(x)),
            Pair("Y", abs(y)),
            Pair("Z", abs(z))
        )

        values.forEachIndexed { index, (label, value) ->
            val barHeight = (value / 5f).coerceIn(0f, 1f) * size.height * 0.8f
            val x = barWidth * (index + 0.5f)

            drawLine(
                color = Color(0xFFEC4899),
                start = Offset(x, size.height),
                end = Offset(x, size.height - barHeight),
                strokeWidth = barWidth * 0.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CompassVisualization(x: Float, y: Float, z: Float) {
    val angle = remember(x, y) {
        Math.toDegrees(kotlin.math.atan2(y.toDouble(), x.toDouble())).toFloat()
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 8.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.width.coerceAtMost(size.height) / 3

        // Draw compass circle
        drawCircle(
            color = Color(0xFF10B981).copy(alpha = 0.2f),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )

        // Draw north indicator
        val angleRad = Math.toRadians(angle.toDouble())
        val endX = centerX + (radius * kotlin.math.cos(angleRad)).toFloat()
        val endY = centerY + (radius * kotlin.math.sin(angleRad)).toFloat()

        drawLine(
            color = Color(0xFF10B981),
            start = Offset(centerX, centerY),
            end = Offset(endX, endY),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color(0xFF10B981),
            radius = 8f,
            center = Offset(endX, endY)
        )
    }
}

@Composable
fun LightVisualization(lightLevel: Float) {
    val brightness = (lightLevel / 10000f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF59E0B).copy(alpha = brightness)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.LightMode,
            contentDescription = "Light",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ProximityVisualization(distance: Float) {
    val proximity = (1f - (distance / 10f).coerceIn(0f, 1f))

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp)
    ) {
        val circleRadius = size.width.coerceAtMost(size.height) / 4
        val alpha = proximity.coerceIn(0.1f, 0.8f)

        drawCircle(
            color = Color(0xFF8B5CF6).copy(alpha = alpha),
            radius = circleRadius,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

@Composable
fun PressureVisualization(pressure: Float) {
    val normalPressure = 1013.25f
    val deviation = ((pressure - normalPressure) / normalPressure).coerceIn(-0.1f, 0.1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp)
    ) {
        val centerY = size.height / 2
        val barHeight = abs(deviation) * size.height * 5

        drawLine(
            color = if (deviation > 0) Color(0xFF06B6D4) else Color(0xFFEF4444),
            start = Offset(size.width / 2, centerY),
            end = Offset(size.width / 2, centerY - (deviation * size.height * 5)),
            strokeWidth = size.width * 0.1f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TemperatureVisualization(temperature: Float) {
    val normalizedTemp = ((temperature - 0f) / 50f).coerceIn(0f, 1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp)
    ) {
        val barHeight = normalizedTemp * size.height * 0.9f

        drawLine(
            color = Color(0xFFEF4444),
            start = Offset(size.width / 2, size.height),
            end = Offset(size.width / 2, size.height - barHeight),
            strokeWidth = size.width * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun HumidityVisualization(humidity: Float) {
    val normalizedHumidity = (humidity / 100f).coerceIn(0f, 1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp)
    ) {
        val waveHeight = size.height * normalizedHumidity
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height - waveHeight)

            val segments = 20
            for (i in 0..segments) {
                val x = (size.width / segments) * i
                val y = size.height - waveHeight +
                        kotlin.math.sin(i * 0.5) * 10f
                lineTo(x,y.toFloat())
            }

            lineTo(size.width, size.height)
            close()
        }

        drawPath(
            path = path,
            color = Color(0xFF3B82F6).copy(alpha = 0.6f)
        )
    }
}