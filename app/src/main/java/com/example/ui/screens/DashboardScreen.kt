package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FamilyCodeEntity
import com.example.data.QuizScoreEntity
import com.example.data.ThreatScanEntity
import com.example.data.ScamReportEntity
import com.example.ui.theme.*
import com.example.viewmodel.CloneGuardViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Shield Core", "Global Threat Intel", "Scam Safe Report")

    Column(modifier = modifier.fillMaxSize()) {
        // TabRow selector for active interface matching guidelines
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkCarbonBg,
            contentColor = SecurityCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    modifier = Modifier.testTag("dashboard_tab_$index")
                ) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (activeTab == index) SecurityCyan else TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = AccentBorder, thickness = 1.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                0 -> ShieldCoreTab(viewModel = viewModel)
                1 -> GlobalThreatIntelTab()
                2 -> ScamReportingTab(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShieldCoreTab(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    val securityScore by viewModel.personalSecurityScore.collectAsState()
    val threatScans by viewModel.threatScans.collectAsState()
    val familyCodes by viewModel.familyCodes.collectAsState()
    val quizScores by viewModel.quizScores.collectAsState()

    val shieldColor by animateColorAsState(
        targetValue = when {
            securityScore >= 80 -> TerminalGreen
            securityScore >= 50 -> WarningOrange
            else -> CriticalRed
        },
        animationSpec = tween(1000), label = "shield_color"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = securityScore / 100f,
        animationSpec = tween(1000), label = "dashboard_progress"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Core Display: Security Health Gauge Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(CardSlate)
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "CORE DEFENSIVE INTEGRITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecurityCyan,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Circular Gauge
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(140.dp)) {
                            // Background Ring
                            drawArc(
                                color = AccentBorder.copy(alpha = 0.5f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Animated Indicator Ring
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(shieldColor.copy(alpha = 0.7f), shieldColor)
                                ),
                                startAngle = 135f,
                                sweepAngle = 270f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$securityScore%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    securityScore >= 80 -> "HIGH INTEGRITY"
                                    securityScore >= 50 -> "SECURE GUARD"
                                    else -> "HIGH EXPOSURE"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = shieldColor,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Your exposure level decreases as you establish voice confirmation protocol codes, complete training drills, and audit suspicious links/emails.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Quick Indicators Row
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                StatBox(
                    title = "Scans Audit",
                    value = "${threatScans.size}",
                    icon = Icons.Default.Shield,
                    color = SecurityCyan,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "Voice Codes",
                    value = "${familyCodes.size}",
                    icon = Icons.Default.RecordVoiceOver,
                    color = TerminalGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "Drills Run",
                    value = "${quizScores.size}",
                    icon = Icons.Default.MenuBook,
                    color = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Smart Recommendations card (UX Trigger)
        item {
            RecommendationsSection(
                viewModel = viewModel,
                codes = familyCodes,
                quizzes = quizScores
            )
        }

        // Emerging Clone Scams Broadcast Ticker (Defensive Alert Hub)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(CardSlate)
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Threat Intelligence Warnings",
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "EMERGING SCAM ALERTS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ScamAlertRow(
                    code = "ALERT-VC26",
                    title = "Grandparent Voice-Cloning Loops",
                    desc = "Scammers extract 5 seconds of social media voice notes to clone children voices calling elderly with high-pressure panic requests.",
                    severity = "CRITICAL",
                    color = PurpleCritical
                )
                Divider(color = AccentBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                ScamAlertRow(
                    code = "ALERT-TYPO",
                    title = "Banking Homoglyph Redirects",
                    desc = "Fake verification prompts circulating via SMS containing letters from Cyrillic alpha maps to duplicate legitimate domains.",
                    severity = "HIGH",
                    color = CriticalRed
                )
                Divider(color = AccentBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                ScamAlertRow(
                    code = "ALERT-CEO",
                    title = "AI Video Meeting Impersonation",
                    desc = "Syndicates join corporate video feeds using real-time generative avatar tools to direct multi-million accounting transfers.",
                    severity = "MEDIUM",
                    color = WarningOrange
                )
            }
        }

        // Scan Audit Logs Title
        if (threatScans.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HISTORICAL THREAT AUDITS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.bodySmall,
                        color = CriticalRed,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { viewModel.clearAllScans() }
                            .padding(4.dp)
                    )
                }
            }

            items(threatScans.take(5), key = { scan -> scan.id }) { scan ->
                AuditHistoryRow(scan = scan, onDelete = { scanId -> viewModel.deleteScanRecord(scanId) })
            }
        }
    }
}

@Composable
fun GlobalThreatIntelTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Section: Network Health / Countermeasure Effectiveness
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "COUNTERMEASURE SYSTEM STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGreen,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "99.4%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Active Threat Prevention Rate",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(TerminalGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GppGood,
                                contentDescription = "Security Integrity Status",
                                tint = TerminalGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    HorizontalDivider(color = AccentBorder, thickness = 1.dp)
                    
                    // Specific Performance Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PerformanceMetricCard(
                            label = "Blocked Hacks",
                            value = "1,842",
                            icon = Icons.Default.OfflineBolt,
                            color = SecurityCyan,
                            modifier = Modifier.weight(1f)
                        )
                        PerformanceMetricCard(
                            label = "Verified Nodes",
                            value = "42,912",
                            icon = Icons.Default.CellTower,
                            color = WarningOrange,
                            modifier = Modifier.weight(1f)
                        )
                        PerformanceMetricCard(
                            label = "Mitigation Rate",
                            value = "< 14s",
                            icon = Icons.Default.Timer,
                            color = PurpleCritical,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Threat Spectrum Analysis by Type
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Threat Spectrum Analysis",
                            tint = SecurityCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "THREAT SPECTRUM ANALYSIS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    Text(
                        text = "Real-time visual distribution of AI-synthesized cloning and message channel threats intercepted in the past week.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ThreatCompositionRow(
                            category = "Linguistic AI Voice Cloning",
                            percentage = 36,
                            volume = "249 vectors isolated",
                            color = PurpleCritical
                        )
                        ThreatCompositionRow(
                            category = "Deepfake Video Impersonation",
                            percentage = 18,
                            volume = "124 active sessions cut",
                            color = CriticalRed
                        )
                        ThreatCompositionRow(
                            category = "TypoSquatting / Homoglyphs",
                            percentage = 28,
                            volume = "192 domains sinkholed",
                            color = WarningOrange
                        )
                        ThreatCompositionRow(
                            category = "CEO Spear Phishing Audits",
                            percentage = 11,
                            volume = "76 company campaigns flagged",
                            color = SecurityCyan
                        )
                        ThreatCompositionRow(
                            category = "Generic Spam Phishing Scams",
                            percentage = 7,
                            volume = "48 gateway loops locked",
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Threat Origin and Potential Impact Division
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Origin Map Tracker",
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "ORIGINS & POTENTIAL IMPACT",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left Column: Suspected Origin Sectors
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "SUSPECTED ORIGIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarningOrange,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                            OriginMetricRow("Eastern Europe Hosts", "42%", TerminalGreen)
                            OriginMetricRow("Southeast Asia Hubs", "29%", SecurityCyan)
                            OriginMetricRow("Cloud VPS Servers", "19%", WarningOrange)
                            OriginMetricRow("Obfuscated Botnets", "10%", CriticalRed)
                        }
                        
                        // Right Column: Potential Impact
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "POTENTIAL IMPACT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CriticalRed,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                            OriginMetricRow("Severe Financial Fraud", "34%", CriticalRed)
                            OriginMetricRow("Company Account Leaks", "26%", WarningOrange)
                            OriginMetricRow("Identity Profiling Bots", "21%", SecurityCyan)
                            OriginMetricRow("Suspicious Link Hacking", "19%", TextMuted)
                        }
                    }
                }
            }
        }

        // Actionable Insights section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Actionable Insights",
                            tint = TerminalGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "ACTIONABLE INSIGHTS & PROTOCOLS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    Text(
                        text = "Follow these guidelines strictly to block social engineering cloning vectors immediately:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        InsightItem(
                            number = "1",
                            title = "Engage Familial Secret Codes",
                            desc = "Establish custom security questions in the 'Clone Protections' tab. If you receive a distress call about family in trouble, ask the verbal codename answer immediately."
                        )
                        InsightItem(
                            number = "2",
                            title = "Inspect Link Character Map Overlay",
                            desc = "Typosquatting pages use unicode letters to mimic famous domains. Read message URLs carefully, identifying Cyrillic homoglyph character substitutions."
                        )
                        InsightItem(
                            number = "3",
                            title = "Execute Out-Of-Band Verification",
                            desc = "If an urgent message demanding funds arrives from family or executives, hang up immediately and place a callback verify over their pre-saved official coordinates."
                        )
                        InsightItem(
                            number = "4",
                            title = "Sandbox Your Audio Permissions",
                            desc = "AI vocoder cloning requires only 5 seconds of sample speech. Protect your conversations by denying web microphone permissions to unverified third parties."
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScamReportingTab(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.scamReports.collectAsState()
    val isSubmitting by viewModel.isSubmittingReport.collectAsState()
    val outcomeMessage by viewModel.reportOutcomeMessage.collectAsState()

    val reportTitle by viewModel.reportTitle.collectAsState()
    val reportScamType by viewModel.reportScamType.collectAsState()
    val reportSourcePlatform by viewModel.reportSourcePlatform.collectAsState()
    val reportSuspectedSender by viewModel.reportSuspectedSender.collectAsState()
    val reportDescription by viewModel.reportDescription.collectAsState()
    val reportImpactLevel by viewModel.reportImpactLevel.collectAsState()
    val reportReporterEmail by viewModel.reportReporterEmail.collectAsState()

    val scamTypes = listOf("AI Voice Clone", "Deepfake Video", "Typo Link", "Email Phishing", "Phone Scam", "SMS Phishing")
    val platforms = listOf("Telephone Call", "WhatsApp", "SMS", "Zoom Video", "Email", "Web Page")
    val impacts = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Welcome and Intro
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "SECURE & TRANSPARENT AUDITS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SecurityCyan,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Submit Threat Incident Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Submit suspected AI-generated phone calls, fake links, or phishing emails. Tracks your reports transparently to review active mitigation countermeasures deployed in response.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Incident Submission Form Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "NEW TRANSMISSION DOSSIER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Input Title
                    OutlinedTextField(
                        value = reportTitle,
                        onValueChange = { viewModel.updateReportTitle(it) },
                        label = { Text("Incident Summary / Threat Target", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder,
                            focusedLabelColor = SecurityCyan,
                            unfocusedLabelColor = TextMuted,
                            focusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Scam Category Group Selector
                    Column {
                        Text(
                            text = "Scam Category",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            scamTypes.forEach { type ->
                                val selected = reportScamType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selected) SecurityCyan else DarkCarbonBg)
                                        .border(1.dp, if (selected) SecurityCyan else AccentBorder, RoundedCornerShape(16.dp))
                                        .clickable { viewModel.updateReportScamType(type) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("scam_type_$type")
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color(0xFF381E72) else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Medium Platform selector
                    Column {
                        Text(
                            text = "Source Medium / Platform",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            platforms.forEach { plat ->
                                val selected = reportSourcePlatform == plat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selected) SecurityCyan else DarkCarbonBg)
                                        .border(1.dp, if (selected) SecurityCyan else AccentBorder, RoundedCornerShape(16.dp))
                                        .clickable { viewModel.updateReportSourcePlatform(plat) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("platform_$plat")
                                ) {
                                    Text(
                                        text = plat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color(0xFF381E72) else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Suspected Sender input
                    OutlinedTextField(
                        value = reportSuspectedSender,
                        onValueChange = { viewModel.updateReportSuspectedSender(it) },
                        label = { Text("Originating Address / Phone / Source Identity", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_sender_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder,
                            focusedLabelColor = SecurityCyan,
                            unfocusedLabelColor = TextMuted,
                            focusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        placeholder = { Text("e.g. +1 (555) 014-9988, client-verify@sec-chase.com", fontSize = 11.sp, color = TextMuted.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Impact selector
                    Column {
                        Text(
                            text = "Incident Exposure Threat Level",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            impacts.forEach { impactName ->
                                val selected = reportImpactLevel == impactName
                                val segmentColor = when (impactName) {
                                    "LOW" -> TerminalGreen
                                    "MEDIUM" -> WarningOrange
                                    "HIGH" -> CriticalRed
                                    "CRITICAL" -> PurpleCritical
                                    else -> TextMuted
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) segmentColor.copy(alpha = 0.25f) else DarkCarbonBg)
                                        .border(2.dp, if (selected) segmentColor else AccentBorder, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.updateReportImpactLevel(impactName) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = impactName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (selected) Color.White else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Description transcription area
                    OutlinedTextField(
                        value = reportDescription,
                        onValueChange = { viewModel.updateReportDescription(it) },
                        label = { Text("Scam Content / Call Dialogue / Warning Flags Spotted", fontSize = 12.sp) },
                        placeholder = { Text("Paste suspicious message, link headers, deepfake meeting delays, or phone conversational speech styles...", fontSize = 11.sp, color = TextMuted.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("report_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder,
                            focusedLabelColor = SecurityCyan,
                            unfocusedLabelColor = TextMuted,
                            focusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f)
                        ),
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Contact Email
                    OutlinedTextField(
                        value = reportReporterEmail,
                        onValueChange = { viewModel.updateReportReporterEmail(it) },
                        label = { Text("Contact Email (For direct outcomes notification)", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder,
                            focusedLabelColor = SecurityCyan,
                            unfocusedLabelColor = TextMuted,
                            focusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkCarbonBg.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (outcomeMessage != null) {
                        val isSuccess = outcomeMessage?.contains("SUCCESS") == true
                        val blockColor = if (isSuccess) TerminalGreen else CriticalRed
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(blockColor.copy(alpha = 0.12f))
                                .border(1.dp, blockColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isSuccess) "TRANSMISSION SUCCESSFUL" else "VALIDATION EXCEPTION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = blockColor,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = outcomeMessage ?: "",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                                Text(
                                    text = "Dismiss",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SecurityCyan,
                                    modifier = Modifier
                                        .clickable { viewModel.clearReportOutcome() }
                                        .align(Alignment.End)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Button Submit
                    Button(
                        onClick = { viewModel.submitScamReport() },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecurityCyan,
                            contentColor = Color(0xFF381E72),
                            disabledContainerColor = DarkCarbonBg,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_report_button")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color(0xFF381E72),
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Transparent Incident Audit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Submissions tracker list header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LODGED TRACKING CONSOLE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${reports.size} Dossiers",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecurityCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (reports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = TextMuted.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Text("No incident profiles filed locally.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        } else {
            items(reports, key = { report -> report.id }) { report ->
                ReportTrackingRow(report = report, onDelete = { reportId -> viewModel.deleteScamReport(reportId) })
            }
        }
    }
}

// RESTORED CORE HELPERS FROM THE ORIGINAL IMPLEMENTATION FOR THE FIRST TAB (ShieldCore)
@Composable
fun StatBox(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(24.dp))
            .border(1.dp, AccentBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecommendationsSection(
    viewModel: CloneGuardViewModel,
    codes: List<FamilyCodeEntity>,
    quizzes: List<QuizScoreEntity>
) {
    val needsFamilyCode = codes.isEmpty()
    val needsQuiz = quizzes.isEmpty()

    if (!needsFamilyCode && !needsQuiz) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(TerminalGreen.copy(alpha = 0.08f))
                .border(1.dp, TerminalGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TerminalGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "All protections active",
                        tint = TerminalGreen
                    )
                }
                Column {
                    Text(
                        text = "SECURE PROTOCOLS ONLINE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Excellent defense posture! Family codename verifications and cyber simulation files are active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(CardSlate)
            .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "CRITICAL INSTRUCTIONS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = WarningOrange,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        if (needsFamilyCode) {
            RecommendationItem(
                text = "Unconfigured Voice-Clone Defense Code",
                actionLabel = "Setup Family Code",
                icon = Icons.Default.RecordVoiceOver,
                desc = "Register private voice challenge answers. This offline protection blocks cloned phone demands completely.",
                onAction = { viewModel.navigateTo(Screen.CLONE_DEFENSE) },
                testTag = "setup_family_code"
            )
        }

        if (needsFamilyCode && needsQuiz) {
            Divider(color = AccentBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        if (needsQuiz) {
            RecommendationItem(
                text = "Pending Phishing Drills",
                actionLabel = "Complete Training",
                icon = Icons.Default.School,
                desc = "Execute gamified scenarios to identify CEO-spoofing emails, typosquatted links, and video meeting deepfakes.",
                onAction = { viewModel.navigateTo(Screen.QUIZ) },
                testTag = "complete_quiz_training"
            )
        }
    }
}

@Composable
fun RecommendationItem(
    text: String,
    actionLabel: String,
    icon: ImageVector,
    desc: String,
    onAction: () -> Unit,
    testTag: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SecurityCyan,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = SecurityCyan),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .testTag(testTag)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ScamAlertRow(
    code: String,
    title: String,
    desc: String,
    severity: String,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = severity,
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            lineHeight = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Broadcasting code: $code (Real-time Intel)",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted.copy(alpha = 0.5f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AuditHistoryRow(
    scan: ThreatScanEntity,
    onDelete: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val dateString = formatter.format(Date(scan.date))

    val statusColor = when (scan.threatLevel) {
        "LOW" -> TerminalGreen
        "MEDIUM" -> WarningOrange
        "HIGH" -> CriticalRed
        "CRITICAL" -> PurpleCritical
        else -> TextMuted
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentBorder, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when (scan.inputSource) {
                            "link" -> Icons.Default.Link
                            "transcript" -> Icons.Default.PhoneCallback
                            else -> Icons.Default.Email
                        },
                        contentDescription = scan.inputSource,
                        tint = SecurityCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = scan.inputContent,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${scan.threatLevel} (${scan.confidenceScore}%)",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(
                        onClick = { onDelete(scan.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete record",
                            tint = CriticalRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = AccentBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Linguistic Indicators Flagged: ${scan.indicatorsFound}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = WarningOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scan.analysisReport,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Audited: $dateString",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// Global Threat Intel Tab Subcomponents
@Composable
fun PerformanceMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCarbonBg.copy(alpha = 0.5f)),
        modifier = modifier.border(1.dp, AccentBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = label, fontSize = 9.sp, color = TextMuted, lineHeight = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ThreatCompositionRow(
    category: String,
    percentage: Int,
    volume: String,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "$percentage%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
        }
        
        // Custom Linear composition progress indicators
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(AccentBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        
        Text(
            text = volume,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
fun OriginMetricRow(
    title: String,
    percent: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Text(text = title, fontSize = 10.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(text = percent, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun InsightItem(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(SecurityCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecurityCyan)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextMuted, lineHeight = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// User reports tracking element row
@Composable
fun ReportTrackingRow(
    report: ScamReportEntity,
    onDelete: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val timeStr = formatter.format(Date(report.dateSubmitted))

    val statusColor = when (report.status) {
        "PENDING_REVIEWS" -> CriticalRed
        "THREAT_MATCHING" -> WarningOrange
        "OUTCOME_REMEDIED" -> TerminalGreen
        "FALSE_ALARMS" -> SecurityCyan
        else -> TextMuted
    }

    val statusLabel = when (report.status) {
        "PENDING_REVIEWS" -> "PENDING ACTION"
        "THREAT_MATCHING" -> "INVESTIGATING"
        "OUTCOME_REMEDIED" -> "ACTIVE DEFENSE"
        "FALSE_ALARMS" -> "VERIFIED SAFE"
        else -> "QUEUED"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        Text(
                            text = report.scamType,
                            style = MaterialTheme.typography.labelSmall,
                            color = SecurityCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = report.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Src: ${report.sourcePlatform}", fontSize = 10.sp, color = TextMuted)
                        Text(text = "•", fontSize = 10.sp, color = TextMuted)
                        Text(text = "From: ${report.suspectedSender}", fontSize = 10.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    }
                }

                IconButton(
                    onClick = { onDelete(report.id) },
                    modifier = Modifier.size(24.dp).testTag("delete_report_${report.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete report ticket",
                        tint = CriticalRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent Progress steppers in the tracking item
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val step1Active = true
                val step2Active = report.status == "THREAT_MATCHING" || report.status == "OUTCOME_REMEDIED" || report.status == "FALSE_ALARMS"
                val step3Active = report.status == "OUTCOME_REMEDIED" || report.status == "FALSE_ALARMS"

                ProgressIndicatorSegment(label = "LOGGED", active = step1Active, activeColor = SecurityCyan, modifier = Modifier.weight(1f))
                ProgressIndicatorSegment(label = "ANALYSING", active = step2Active, activeColor = WarningOrange, modifier = Modifier.weight(1f))
                ProgressIndicatorSegment(
                    label = if (report.status == "FALSE_ALARMS") "RESOLVED SAFE" else "COUNTERED",
                    active = step3Active,
                    activeColor = statusColor,
                    modifier = Modifier.weight(1f)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AccentBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TRANSMITTED SCAM DESCRIPTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCarbonBg.copy(alpha = 0.5f))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                            Text(
                                text = "AI COUNTERMEASURE LOGS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = report.statusDetails,
                            fontSize = 11.sp,
                            color = Color.White,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Filed: $timeStr", fontSize = 10.sp, color = TextMuted)
                    Text(text = "Impact: ${report.impactLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap block to examine diagnostic analyst log logs • Click to track details...",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorSegment(
    label: String,
    active: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(if (active) activeColor else AccentBorder)
        )
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) activeColor else TextMuted,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
