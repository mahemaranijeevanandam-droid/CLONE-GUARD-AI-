package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FamilyCodeEntity
import com.example.ui.theme.*
import com.example.viewmodel.CloneGuardViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CloneDefenseScreen(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    val familyCodes by viewModel.familyCodes.collectAsState()

    // Form states
    val memberName by viewModel.newMemberName.collectAsState()
    val challengeQuestion by viewModel.newChallengeQuestion.collectAsState()
    val securityAnswer by viewModel.newSecurityAnswer.collectAsState()

    // Diagnosis list states for the Voice Call Diagnostic Checklist
    var anomalyStudioQuiet by remember { mutableStateOf(false) }
    var anomalyMonotoneInput by remember { mutableStateOf(false) }
    var anomalyResponseLatency by remember { mutableStateOf(false) }
    var anomalyRefusesCallback by remember { mutableStateOf(false) }
    var anomalyRequestGiftcard by remember { mutableStateOf(false) }

    val anomaliesCheckedClass = listOf(
        anomalyStudioQuiet,
        anomalyMonotoneInput,
        anomalyResponseLatency,
        anomalyRefusesCallback,
        anomalyRequestGiftcard
    )
    val checkCount = anomaliesCheckedClass.count { it }

    // Simulation Call Screen dialog
    var activeSimulateCode by remember { mutableStateOf<FamilyCodeEntity?>(null) }
    var simulateInputAnswer by remember { mutableStateOf("") }
    var simulationMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // High level warning card (educational context)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "OFFLINE VERBAL CODE PROTOCOLS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Because artificial intelligence models can replicate any human voice inflection from a 3-second recording, digital voice authenticity has broken down. \n\n" +
                               "The single most powerful real-world defense is to agree in advance on offline challenge questions and safe-codes inside your family circle. If someone calls claiming an emergency, request their safe-code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Checklist Checklist diagnosis
        item {
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
                    text = "VOICE CALL CHARACTER ANOMALY CHECKLIST",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SecurityCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "If you are on an unverified call, check the auditory and behavioral traits you are actively noticing:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CheckItemBox(
                        checked = anomalyStudioQuiet,
                        onCheckedChange = { anomalyStudioQuiet = it },
                        text = "Excessive quietness in caller backdrop (zero background acoustics or street echoes)."
                    )
                    CheckItemBox(
                        checked = anomalyMonotoneInput,
                        onCheckedChange = { anomalyMonotoneInput = it },
                        text = "Flat emotional transitions (voice doesn't spike naturally even when claiming terror or panic)."
                    )
                    CheckItemBox(
                        checked = anomalyResponseLatency,
                        onCheckedChange = { anomalyResponseLatency = it },
                        text = "Micro-pauses of 1-3 seconds before responding to direct custom questions (indicates AI generator latency)."
                    )
                    CheckItemBox(
                        checked = anomalyRefusesCallback,
                        onCheckedChange = { anomalyRefusesCallback = it },
                        text = "Caller aggressively refuses hangup and callback to their official saved private number."
                    )
                    CheckItemBox(
                        checked = anomalyRequestGiftcard,
                        onCheckedChange = { anomalyRequestGiftcard = it },
                        text = "Demands untraceable, high-priority payment options (e.g. gift cards, wire, crypto, Zelle)."
                    )
                }

                if (checkCount > 0) {
                    val flagColor = if (checkCount >= 3) CriticalRed else WarningOrange
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(flagColor.copy(alpha = 0.1f))
                            .border(1.dp, flagColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (checkCount >= 3) "❌ HIGH PROBABILITY OF AI CLONE SCAM" else "⚠️ UNVERIFIED THREAT MARKERS SPOTTED",
                                fontWeight = FontWeight.Bold,
                                color = flagColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (checkCount >= 3) {
                                    "This call bears multiple, clear characteristics of automated voice cloning and social engineering. HANG UP IMMEDIATELY. Call the relative back using their pre-saved personal contact entry."
                                } else {
                                    "Suspicious metrics detected. Initiate verbal safety codeword challenge to verify identity before coordinating financial actions."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Safe Codeword Manager
        item {
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
                    text = "REGISTER SECURE VERBAL CHALLENGE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SecurityCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Configure Expected Challenge-Answer targets for family units. These are saved completely locally and privately on your physical device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { viewModel.updateNewMemberFields(it, challengeQuestion, securityAnswer) },
                        label = { Text("Family Member Name (e.g. Mom, Son)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("family_name_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder
                        )
                    )

                    OutlinedTextField(
                        value = challengeQuestion,
                        onValueChange = { viewModel.updateNewMemberFields(memberName, it, securityAnswer) },
                        label = { Text("Challenge Question only they know (e.g. First street name?)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("family_question_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder
                        )
                    )

                    OutlinedTextField(
                        value = securityAnswer,
                        onValueChange = { viewModel.updateNewMemberFields(memberName, challengeQuestion, it) },
                        label = { Text("Required Answer Match Word (Case insensitive)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("family_answer_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder
                        )
                    )
                }

                Button(
                    onClick = { viewModel.registerFamilyCode() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("register_family_button"),
                    enabled = memberName.isNotEmpty() && challengeQuestion.isNotEmpty() && securityAnswer.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerminalGreen,
                        contentColor = Color(0xFF1C1B1F),
                        disabledContainerColor = CardSlate,
                        disabledContentColor = TextMuted.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register Protocol Challenge", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // List of Active Codes
        if (familyCodes.isNotEmpty()) {
            item {
                Text(
                    text = "ACTIVE VERBAL CHALLENGE CODES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(familyCodes, key = { it.id }) { code ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AccentBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(TerminalGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = TerminalGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = code.memberName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Protocol: Verbal Challenge Configured",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activeSimulateCode = code
                                        simulateInputAnswer = ""
                                        simulationMessage = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SecurityCyan.copy(alpha = 0.2f), contentColor = SecurityCyan),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Test Sim", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                IconButton(
                                    onClick = { viewModel.deleteFamilyCode(code.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Code",
                                        tint = CriticalRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Divider(color = AccentBorder, thickness = 1.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "🔒 EXPECTED QUESTION:",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted
                            )
                            Text(
                                text = code.challengeQuestion,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "🔑 EXPECTED ANSWER CODE:",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted
                            )
                            Text(
                                text = code.securityAnswer.uppercase(),
                                fontSize = 13.sp,
                                color = TerminalGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }

    // Call Challenge Simulation Dialog
    activeSimulateCode?.let { code ->
        AlertDialog(
            onDismissRequest = { activeSimulateCode = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = SecurityCyan)
                    Text(
                        text = "SIMULATING PHONE SCAM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Incoming urgent call claims to be: '${code.memberName}'. The audio sounds flawless and requests bank transfers. You execute the security protocol.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCarbonBg)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "You ask verbally: '${code.challengeQuestion}'",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SecurityCyan
                            )
                        }
                    }

                    OutlinedTextField(
                        value = simulateInputAnswer,
                        onValueChange = { simulateInputAnswer = it },
                        label = { Text("Caller responses with Answer Word:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("simulate_caller_answer_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SecurityCyan,
                            unfocusedBorderColor = AccentBorder
                        )
                    )

                    if (simulationMessage.isNotEmpty()) {
                        Text(
                            text = simulationMessage,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (simulationMessage.contains("SUCCESS")) TerminalGreen else CriticalRed,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val match = simulateInputAnswer.trim().equals(code.securityAnswer.trim(), ignoreCase = true)
                            if (match) {
                                simulationMessage = "✅ PROTOCOL SUCCESS: Caller returned target safe-word correctly! Voice authentication certified."
                                viewModel.simulateVoiceChallengeCheck(code.id, true)
                            } else {
                                simulationMessage = "🚨 PROTOCOL FAILED: Caller returned incorrect answer! Highly likely a synthesized AI voice clone fraudster trying to bypass your gates. Hang up immediately."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecurityCyan)
                    ) {
                        Text("Verify Answer", color = Color.White)
                    }
                    TextButton(onClick = { activeSimulateCode = null }) {
                        Text("Cancel Call", color = TextMuted)
                    }
                }
            },
            containerColor = CardSlate
        )
    }
}

@Composable
fun CheckItemBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = SecurityCyan,
                uncheckedColor = AccentBorder
            ),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (checked) Color.White else TextMuted,
            lineHeight = 15.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
