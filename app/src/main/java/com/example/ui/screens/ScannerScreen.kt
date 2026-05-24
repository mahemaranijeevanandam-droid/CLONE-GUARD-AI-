package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CloneGuardViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScannerScreen(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    val scanInput by viewModel.scanInput.collectAsState()
    val scanType by viewModel.scanType.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Explanatory Top Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecurityCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Troubleshoot,
                        contentDescription = null,
                        tint = SecurityCyan
                    )
                }
                Column {
                    Text(
                        text = "COGNITIVE CORES AUDIT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Audit suspects using AI cognitive analysis. Analyzes character modifications (homoglyphs), structural urgency, and voice-transcript clone patterns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Segmented Scan Mode Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val modes = listOf(
                Triple("text", "Email / SMS", Icons.Default.Email),
                Triple("link", "Web Link", Icons.Default.Link),
                Triple("transcript", "Voice Call", Icons.Default.PhoneCallback)
            )
            modes.forEach { (mode, label, icon) ->
                val selected = scanType == mode
                Button(
                    onClick = { viewModel.updateScanType(mode) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) SecurityCyan else CardSlate,
                        contentColor = if (selected) Color(0xFF381E72) else TextMuted
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (selected) SecurityCyan else AccentBorder),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("scan_tab_$mode")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Interactive Input Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(CardSlate)
                .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (scanType) {
                    "link" -> "TARGET SUSPECT URL"
                    "transcript" -> "SUSPECT CONVERSATION TRANSCRIPT"
                    else -> "SUSPECT TEXT BODY"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SecurityCyan,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = scanInput,
                onValueChange = { viewModel.updateScanInput(it) },
                placeholder = {
                    Text(
                        text = when (scanType) {
                            "link" -> "e.g. www.chase-secure-rn.xyz/login"
                            "transcript" -> "e.g. 'Hey Dad, I lost my wallet and got into an accident. Call this number now and wire some funds or buy gift cards...'"
                            else -> "e.g. 'URGENT: Your account was suspended due to unverified activities. Tap this lookalike link within 1 hour...'"
                        },
                        fontSize = 13.sp,
                        color = TextMuted.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (scanType == "link") 68.dp else 140.dp)
                    .testTag("scan_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SecurityCyan,
                    unfocusedBorderColor = AccentBorder,
                    focusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (scanInput.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { viewModel.clearScanner() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CriticalRed),
                        border = BorderStroke(1.dp, CriticalRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("clear_scan_button")
                    ) {
                        Text("Clear", fontWeight = FontWeight.Bold)
                    }
                }
                
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.runThreatScan()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("run_scan_button"),
                    enabled = scanInput.trim().isNotEmpty() && !isScanning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecurityCyan,
                        contentColor = Color(0xFF381E72),
                        disabledContainerColor = CardSlate,
                        disabledContentColor = TextMuted.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = Color(0xFF381E72),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Content...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Execute Security Audit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Scan Results Section
        AnimatedVisibility(
            visible = scanResult != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            scanResult?.let { result ->
                val resultColor = when (result.threatLevel) {
                    "LOW" -> TerminalGreen
                    "MEDIUM" -> WarningOrange
                    "HIGH" -> CriticalRed
                    "CRITICAL" -> PurpleCritical
                    else -> TextMuted
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(CardSlate)
                        .border(2.dp, resultColor.copy(alpha = 0.7f), RoundedCornerShape(32.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (result.threatLevel) {
                                    "LOW" -> Icons.Default.VerifiedUser
                                    "MEDIUM" -> Icons.Default.SentimentNeutral
                                    "HIGH" -> Icons.Default.ReportProblem
                                    "CRITICAL" -> Icons.Default.GppBad
                                    else -> Icons.Default.Help
                                },
                                contentDescription = null,
                                tint = resultColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "THREAT LEVEL: ${result.threatLevel}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = resultColor
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(resultColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${result.confidenceScore}% CONFIDENCE",
                                style = MaterialTheme.typography.bodySmall,
                                color = resultColor,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Divider(color = AccentBorder, thickness = 1.dp)

                    // Indicators Pills list
                    Text(
                        text = "AUDIT ATTRIBUTES FLAGGED:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        result.indicators.forEach { indicator ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(DarkCarbonBg)
                                    .border(1.dp, resultColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = indicator,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Divider(color = AccentBorder, thickness = 1.dp)

                    // Explanation & Security Guidelines Report
                    Text(
                        text = "SECURITY THREAT REPORT & COGNITIVE ANALYSIS:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = result.analysisReport,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCarbonBg)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SecurityCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Transparency: This report is constructed dynamically using cognitive threat heuristic mapping. CloneGuard maintains fully transparent audit rules. We do not store or transmit private text externally once audit is finalized.",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
