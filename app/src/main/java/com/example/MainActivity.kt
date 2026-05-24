package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CloneDefenseScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.*
import com.example.viewmodel.CloneGuardViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: CloneGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge to Edge mandatory configuration as requested under Android Styling rules
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkCarbonBg)
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(TerminalGreen, shape = androidx.compose.foundation.shape.CircleShape)
                                        )
                                        Text(
                                            text = "CLONEGUARD // AI",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, SecurityCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .background(SecurityCyan.copy(alpha = 0.08f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "DEfENSE ACTIVE",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecurityCyan
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = AccentBorder, thickness = 1.dp)
                        }
                    },
                    bottomBar = {
                        Column {
                            HorizontalDivider(color = AccentBorder, thickness = 1.dp)
                            NavigationBar(
                                containerColor = CardSlate,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .testTag("bottom_nav_bar"),
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.DASHBOARD,
                                    onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Shield Health Dashboard") },
                                    label = { Text("Health Dashboard", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF381E72),
                                        selectedTextColor = SecurityCyan,
                                        indicatorColor = SecurityCyan,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_dashboard")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.SCANNER,
                                    onClick = { viewModel.navigateTo(Screen.SCANNER) },
                                    icon = { Icon(Icons.Default.Shield, contentDescription = "Threat audit cores scanner") },
                                    label = { Text("Threat Scanner", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF381E72),
                                        selectedTextColor = SecurityCyan,
                                        indicatorColor = SecurityCyan,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_scanner")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.CLONE_DEFENSE,
                                    onClick = { viewModel.navigateTo(Screen.CLONE_DEFENSE) },
                                    icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = "Voice Clone Protocols & Code word challenges manager") },
                                    label = { Text("Clone Protections", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF381E72),
                                        selectedTextColor = SecurityCyan,
                                        indicatorColor = SecurityCyan,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_clone_defense")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.QUIZ,
                                    onClick = { viewModel.navigateTo(Screen.QUIZ) },
                                    icon = { Icon(Icons.Default.School, contentDescription = "Cyber hygiene simulation drills") },
                                    label = { Text("Cyber Drills", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF381E72),
                                        selectedTextColor = SecurityCyan,
                                        indicatorColor = SecurityCyan,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_quiz")
                                )
                            }
                        }
                    },
                    containerColor = DarkCarbonBg
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        when (currentScreen) {
                            Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                            Screen.SCANNER -> ScannerScreen(viewModel = viewModel)
                            Screen.CLONE_DEFENSE -> CloneDefenseScreen(viewModel = viewModel)
                            Screen.QUIZ -> QuizScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
