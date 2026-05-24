package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to store phishing and clone threat scans analysed locally or via API.
 */
@Entity(tableName = "threat_scans")
data class ThreatScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val inputSource: String, // email, text message, suspect link, or call transcript
    val inputContent: String,
    val threatLevel: String, // LOW, MEDIUM, HIGH, CRITICAL
    val confidenceScore: Int, // 0 to 100
    val analysisReport: String, // Gemini analysis and detailed security explanation
    val indicatorsFound: String // Comma separated list of phishing/cloning metrics spotted
)

/**
 * Entity to manage secure family voice-verification protocol and verbal password phrases.
 * Establishing offline credentials is the number one defense against AI voice-clone kidnapping or emergency phone scams.
 */
@Entity(tableName = "family_codes")
data class FamilyCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberName: String,
    val challengeQuestion: String,
    val securityAnswer: String, // Stored encrypted or plaintext securely inside local SQLite DB
    val dateSet: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
)

/**
 * Entity to track cyber security educational shield score and completed quiz levels.
 */
@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val dateCompleted: Long = System.currentTimeMillis(),
    val category: String // AI Voice Scams, TypoSquatting Links, Email Phishing, Impersonation
)

/**
 * Entity to manage secure and transparent user reporting of suspected AI-generated scams.
 */
@Entity(tableName = "scam_reports")
data class ScamReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val scamType: String, // AI Voice Clone, Deepfake Video, Typo Link, Email Phishing, Phone Scam, SMS Phishing
    val sourcePlatform: String, // WhatsApp, SMS, Email, Zoom, Telephone Call, Web Browser
    val suspectedSender: String, // Sender address, phone number, or IP
    val description: String,
    val impactLevel: String, // LOW, MEDIUM, HIGH, CRITICAL
    val reporterEmail: String,
    val dateSubmitted: Long = System.currentTimeMillis(),
    val status: String, // PENDING_REVIEWS, THREAT_MATCHING, OUTCOME_REMEDIED, FALSE_ALARMS
    val statusDetails: String // Actionable transparent breakdown and countermeasures outcome details
)

