package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
fun QuizScreen(
    viewModel: CloneGuardViewModel,
    modifier: Modifier = Modifier
) {
    val currentQuestionIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()

    val questions = viewModel.quizQuestions
    val currentQuestion = questions[currentQuestionIndex]
    val selectedAnswerIndex = selectedAnswers[currentQuestionIndex]
    val hasAnswered = selectedAnswerIndex != null

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (!quizCompleted) {
            // Header Progress Map
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CYBER DRILLS & SCENARIOS (${currentQuestionIndex + 1}/${questions.size})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SecurityCyan,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        
                        Text(
                            text = currentQuestion.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = WarningOrange,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Simple Linear Progress Bar
                    val percentage = (currentQuestionIndex + 1).toFloat() / questions.size
                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = SecurityCyan,
                        trackColor = AccentBorder
                    )
                }
            }

            // Scenario Question Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(CardSlate)
                    .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Adjust,
                        contentDescription = null,
                        tint = SecurityCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = currentQuestion.descriptionTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Inner monospace terminal-like box for the scenario text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCarbonBg)
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentQuestion.scenarioBody,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        lineHeight = 20.sp
                    )
                }
            }

            // Options selection list
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentQuestion.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = selectedAnswerIndex == optIndex
                    val isCorrect = optIndex == currentQuestion.correctAnswerIndex
                    
                    val borderOutlineColor = when {
                        !hasAnswered -> if (isSelected) SecurityCyan else AccentBorder
                        isSelected && isCorrect -> TerminalGreen
                        isSelected && !isCorrect -> CriticalRed
                        isCorrect -> TerminalGreen // Highlight correct answer after input
                        else -> AccentBorder
                    }

                    val containerColor = when {
                        !hasAnswered -> if (isSelected) SecurityCyan.copy(alpha = 0.08f) else CardSlate
                        isSelected && isCorrect -> TerminalGreen.copy(alpha = 0.1f.coerceAtLeast(0.08f))
                        isSelected && !isCorrect -> CriticalRed.copy(alpha = 0.08f)
                        isCorrect -> TerminalGreen.copy(alpha = 0.05f)
                        else -> CardSlate
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderOutlineColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !hasAnswered) {
                                viewModel.selectQuizAnswer(currentQuestionIndex, optIndex)
                            }
                            .testTag("quiz_option_$optIndex")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SecurityCyan else DarkCarbonBg)
                                    .border(1.dp, if (isSelected) SecurityCyan else AccentBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = if (hasAnswered && !isCorrect) Icons.Default.Close else Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (hasAnswered && isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = TerminalGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected || (hasAnswered && isCorrect)) Color.White else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Explanation Section Revealed
            if (hasAnswered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) TerminalGreen.copy(alpha = 0.08f) else WarningOrange.copy(alpha = 0.08f))
                        .border(1.dp, if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) TerminalGreen.copy(alpha = 0.3f) else WarningOrange.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) Icons.Default.VerifiedUser else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) TerminalGreen else WarningOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) "CYBER INTEGRITY PASS" else "SAFE POSTURE LESSON",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) TerminalGreen else WarningOrange,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = currentQuestion.indicatorExplanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Direction Actions Nav Button
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousQuizQuestion() },
                    enabled = currentQuestionIndex > 0,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecurityCyan)
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = { viewModel.nextQuizQuestion() },
                    enabled = hasAnswered,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecurityCyan,
                        contentColor = Color(0xFF381E72),
                        disabledContainerColor = CardSlate,
                        disabledContentColor = TextMuted.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.testTag("next_quiz_button")
                ) {
                    Text(
                        text = if (currentQuestionIndex == questions.size - 1) "Complete Evaluation" else "Next Scenario",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Quiz Results screen
            Column(
                modifier = Modifier
                    .fillPaddingAndWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var correctCount = 0
                questions.forEachIndexed { idx, q ->
                    if (selectedAnswers[idx] == q.correctAnswerIndex) correctCount++
                }

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(SecurityCyan.copy(alpha = 0.15f))
                        .border(2.dp, SecurityCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Success Medal",
                        tint = SecurityCyan,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Text(
                    text = "HARDENED POSTURE REGISTERED",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Success score ratio: $correctCount / ${questions.size} scenario matches finalized.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(CardSlate)
                        .border(1.dp, AccentBorder, RoundedCornerShape(32.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "DEFENSIVE GRADE: ${if (correctCount == questions.size) "HIGH INTEGRITY (100% SECURE)" else "AWARE GUARD"}",
                            fontWeight = FontWeight.Bold,
                            color = if (correctCount == questions.size) TerminalGreen else WarningOrange,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (correctCount == questions.size) {
                                "Excellent! You displayed flawless proficiency in recognizing typosquatted homoglyph cloning, AI synthetic vocal coercion scripts, and CEO impersonations. Keep sharing safe passwords within private verbal channels."
                            } else {
                                "Good attempt. Practice these security check scenarios periodically. Impersonation fraud operates primarily on speed and emotional confusion. Slowing down caller scripts and using verification bypass loops blocks hackers."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.resetQuiz() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecurityCyan,
                        contentColor = Color(0xFF381E72)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Execute Scenario Re-audit", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun Modifier.fillPaddingAndWidth() = this.fillMaxWidth()
