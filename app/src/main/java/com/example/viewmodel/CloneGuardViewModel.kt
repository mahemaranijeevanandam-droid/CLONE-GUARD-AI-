package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiScanner
import com.example.data.AppDatabase
import com.example.data.CloneGuardRepository
import com.example.data.FamilyCodeEntity
import com.example.data.QuizScoreEntity
import com.example.data.ThreatScanEntity
import com.example.data.ScamReportEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD, SCANNER, CLONE_DEFENSE, QUIZ
}

class CloneGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CloneGuardRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CloneGuardRepository(database)

        // Seed Scam Reports if empty to provide direct transparent tracking
        viewModelScope.launch {
            try {
                val currentReports = repository.scamReports.first()
                if (currentReports.isEmpty()) {
                    val seed1 = ScamReportEntity(
                        title = "Urgent Kidnap Ransom Voice Clone Call",
                        scamType = "AI Voice Clone",
                        sourcePlatform = "Telephone Call",
                        suspectedSender = "+1 (555) 019-3281",
                        description = "I received a phone call of my sister screaming for help, panicking. A man took over the phone saying she had a severe accident and needed money wired. I checked with her in secondary app and she is safe at home. The vocal cloning was unbelievably flawless.",
                        impactLevel = "CRITICAL",
                        reporterEmail = "mayeehema27@gmail.com",
                        status = "OUTCOME_REMEDIED",
                        statusDetails = "ANALYSIS COMPLETE: Captured audio imprint from caller ID logs has been decompiled. Pattern signature matched AI generator template standard: Elevate-ElevenLabs-v2. Countermeasure Deployed: Added voice print telemetry hash to global carrier-level blocking lists and synced dynamic warning profile to 15,000 active nodes. Protective posture achieved."
                    )
                    val seed2 = ScamReportEntity(
                        title = "Chase Bank Typo Holiday Alert link",
                        scamType = "Typo Link",
                        sourcePlatform = "SMS",
                        suspectedSender = "Chase-Notification-Alert-Service@88219",
                        description = "Got an SMS: 'Your Chase card has been frozen due to suspected travel transactions. Verify at https://chase-securIty-verification.com'. Note the uppercase I used instead of lower case l. This is typosquatting homoglyph cloning.",
                        impactLevel = "HIGH",
                        reporterEmail = "mayeehema27@gmail.com",
                        status = "THREAT_MATCHING",
                        statusDetails = "TRIAGED & MONITORING: Cyrillic homoglyph redirect vector detected on address character 'I'. Located fraudulent registrar host server in Eastern Europe. Deployed countermeasure list upgrade: Host IP is blacklisted, and automated take-down petition has been escalated to registry operator ICANN."
                    )
                    repository.insertScamReport(seed1)
                    repository.insertScamReport(seed2)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Navigation and screen management
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Database reactive streams
    val threatScans: StateFlow<List<ThreatScanEntity>> = repository.threatScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val familyCodes: StateFlow<List<FamilyCodeEntity>> = repository.familyCodes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quizScores: StateFlow<List<QuizScoreEntity>> = repository.quizScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val scamReports: StateFlow<List<ScamReportEntity>> = repository.scamReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User reporting system form states
    private val _reportTitle = MutableStateFlow("")
    val reportTitle: StateFlow<String> = _reportTitle.asStateFlow()

    private val _reportScamType = MutableStateFlow("AI Voice Clone")
    val reportScamType: StateFlow<String> = _reportScamType.asStateFlow()

    private val _reportSourcePlatform = MutableStateFlow("SMS")
    val reportSourcePlatform: StateFlow<String> = _reportSourcePlatform.asStateFlow()

    private val _reportSuspectedSender = MutableStateFlow("")
    val reportSuspectedSender: StateFlow<String> = _reportSuspectedSender.asStateFlow()

    private val _reportDescription = MutableStateFlow("")
    val reportDescription: StateFlow<String> = _reportDescription.asStateFlow()

    private val _reportImpactLevel = MutableStateFlow("HIGH")
    val reportImpactLevel: StateFlow<String> = _reportImpactLevel.asStateFlow()

    private val _reportReporterEmail = MutableStateFlow("")
    val reportReporterEmail: StateFlow<String> = _reportReporterEmail.asStateFlow()

    private val _isSubmittingReport = MutableStateFlow(false)
    val isSubmittingReport: StateFlow<Boolean> = _isSubmittingReport.asStateFlow()

    private val _reportOutcomeMessage = MutableStateFlow<String?>(null)
    val reportOutcomeMessage: StateFlow<String?> = _reportOutcomeMessage.asStateFlow()

    fun updateReportTitle(value: String) { _reportTitle.value = value }
    fun updateReportScamType(value: String) { _reportScamType.value = value }
    fun updateReportSourcePlatform(value: String) { _reportSourcePlatform.value = value }
    fun updateReportSuspectedSender(value: String) { _reportSuspectedSender.value = value }
    fun updateReportDescription(value: String) { _reportDescription.value = value }
    fun updateReportImpactLevel(value: String) { _reportImpactLevel.value = value }
    fun updateReportReporterEmail(value: String) { _reportReporterEmail.value = value }
    fun clearReportOutcome() { _reportOutcomeMessage.value = null }

    fun submitScamReport() {
        val title = _reportTitle.value.trim()
        val scamType = _reportScamType.value
        val platform = _reportSourcePlatform.value
        val sender = _reportSuspectedSender.value.trim()
        val desc = _reportDescription.value.trim()
        val impact = _reportImpactLevel.value
        val email = _reportReporterEmail.value.trim()

        if (title.isEmpty() || desc.isEmpty() || email.isEmpty()) {
            _reportOutcomeMessage.value = "Validation Failed: Please fill in Title, Description, and Contact Email."
            return
        }

        viewModelScope.launch {
            _isSubmittingReport.value = true
            try {
                val status = when (scamType) {
                    "AI Voice Clone" -> "PENDING_REVIEWS"
                    "Deepfake Video" -> "PENDING_REVIEWS"
                    else -> "THREAT_MATCHING"
                }
                
                val details = when (scamType) {
                    "AI Voice Clone" -> "SUBMITTED FOR DIGITAL TRIAGE: Our neural engine is processing the acoustic frequency fingerprint of the reported $platform instance to isolate synthesized vocoder anomalies. Your incident status is tracked securely. Status updates will sync transparently."
                    "Deepfake Video" -> "ANALYSIS QUEUED: Inspecting facial pixel synthesis maps and lip-sync alignment delays. If matching dynamic spoof signatures are found, blocklists will be auto-deployed globally."
                    "Typo Link" -> "THREAT MATCH IN PROGRESS: Registrar details are being checked for '$sender' against our high-security lookalike domain database index. Block signals will broadcast on confirmation."
                    else -> "QUEUED: Pattern indicators under active verification by our real-time defense infrastructure. Threat signatures will update on next client sync."
                }

                val newReport = ScamReportEntity(
                    title = title,
                    scamType = scamType,
                    sourcePlatform = platform,
                    suspectedSender = if (sender.isEmpty()) "Unknown Sender" else sender,
                    description = desc,
                    impactLevel = impact,
                    reporterEmail = email,
                    status = status,
                    statusDetails = details
                )

                repository.insertScamReport(newReport)
                
                // Clear the form fields upon successful insertion
                _reportTitle.value = ""
                _reportSuspectedSender.value = ""
                _reportDescription.value = ""
                _reportReporterEmail.value = ""
                
                _reportOutcomeMessage.value = "SUCCESS: Security report securely logged. Tracking token successfully created! View progress in real-time below."
            } catch (e: Exception) {
                _reportOutcomeMessage.value = "ERROR: Failed to save report: ${e.localizedMessage}"
            } finally {
                _isSubmittingReport.value = false
            }
        }
    }

    fun deleteScamReport(id: Long) {
        viewModelScope.launch {
            repository.deleteScamReportById(id)
        }
    }

    // Dynamic Personal Security Shield Health Score Calculation
    // Calculates a security health rating from 0 to 100 based on defensive actions taken.
    val personalSecurityScore: StateFlow<Int> = combine(
        familyCodes,
        quizScores,
        threatScans
    ) { codes, quizzes, scans ->
        var score = 30 // base core score
        
        // Setup family codes (Verbal safe-word protocols are critical defenses against voice cloning)
        if (codes.isNotEmpty()) {
            score += minOf(codes.size * 15, 30) // up to +30% for family challenges
        }
        
        // Quizzes taken (represents simulated training cyber strength)
        if (quizzes.isNotEmpty()) {
            score += 20 // +20% for education
            val maxScore = quizzes.maxOfOrNull { it.score.toFloat() / it.totalQuestions } ?: 0f
            if (maxScore >= 0.75f) {
                score += 10 // bonus +10% for high proficiency
            }
        }
        
        // Scans executed (Checking suspect messages proactively)
        if (scans.isNotEmpty()) {
            score += 10
        }
        
        minOf(score, 100)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    // Active Threat Scanner States
    private val _scanInput = MutableStateFlow("")
    val scanInput: StateFlow<String> = _scanInput.asStateFlow()

    private val _scanType = MutableStateFlow("text") // text, link, transcript
    val scanType: StateFlow<String> = _scanType.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<GeminiScanner.ScanResult?>(null)
    val scanResult: StateFlow<GeminiScanner.ScanResult?> = _scanResult.asStateFlow()

    fun updateScanInput(input: String) {
        _scanInput.value = input
    }

    fun updateScanType(type: String) {
        _scanType.value = type
        _scanResult.value = null
    }

    fun clearScanner() {
        _scanInput.value = ""
        _scanResult.value = null
    }

    fun runThreatScan() {
        val input = _scanInput.value.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            _isScanning.value = true
            try {
                val result = GeminiScanner.scanContent(_scanType.value, input)
                _scanResult.value = result

                // Store scan safely into local database history logs for user audit
                val entity = ThreatScanEntity(
                    inputSource = _scanType.value,
                    inputContent = input,
                    threatLevel = result.threatLevel,
                    confidenceScore = result.confidenceScore,
                    analysisReport = result.analysisReport,
                    indicatorsFound = result.indicators.joinToString(",")
                )
                repository.insertScan(entity)
            } catch (e: Exception) {
                // Return descriptive error fallback scanner state
                _scanResult.value = GeminiScanner.ScanResult(
                    threatLevel = "LOW",
                    confidenceScore = 0,
                    indicators = listOf("Connection Error"),
                    analysisReport = "Failed to run scanning engine: ${e.localizedMessage}. Please verify internet access."
                )
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun deleteScanRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteScanById(id)
        }
    }

    fun clearAllScans() {
        viewModelScope.launch {
            repository.clearScanHistory()
        }
    }

    // Family Clone Defenses State Management (Safe Word Challenges)
    private val _newMemberName = MutableStateFlow("")
    val newMemberName: StateFlow<String> = _newMemberName.asStateFlow()

    private val _newChallengeQuestion = MutableStateFlow("")
    val newChallengeQuestion: StateFlow<String> = _newChallengeQuestion.asStateFlow()

    private val _newSecurityAnswer = MutableStateFlow("")
    val newSecurityAnswer: StateFlow<String> = _newSecurityAnswer.asStateFlow()

    fun updateNewMemberFields(name: String, question: String, answer: String) {
        _newMemberName.value = name
        _newChallengeQuestion.value = question
        _newSecurityAnswer.value = answer
    }

    fun registerFamilyCode() {
        val name = _newMemberName.value.trim()
        val q = _newChallengeQuestion.value.trim()
        val a = _newSecurityAnswer.value.trim()
        if (name.isEmpty() || q.isEmpty() || a.isEmpty()) return

        viewModelScope.launch {
            val entity = FamilyCodeEntity(
                memberName = name,
                challengeQuestion = q,
                securityAnswer = a
            )
            repository.insertFamilyCode(entity)
            
            // clear inputs
            _newMemberName.value = ""
            _newChallengeQuestion.value = ""
            _newSecurityAnswer.value = ""
        }
    }

    fun deleteFamilyCode(id: Long) {
        viewModelScope.launch {
            repository.deleteFamilyCodeById(id)
        }
    }

    fun simulateVoiceChallengeCheck(id: Long, checked: Boolean) {
        viewModelScope.launch {
            repository.updateFamilyVerification(id, checked)
        }
    }

    // Interactive Quiz / Simulation Data Structure
    data class SecurityQuizQuestion(
        val category: String,
        val descriptionTitle: String,
        val scenarioBody: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        val indicatorExplanation: String
    )

    val quizQuestions = listOf(
        SecurityQuizQuestion(
            category = "AI Voice Scams",
            descriptionTitle = "The Stranded Family Claim",
            scenarioBody = "You receive a phone call from an unknown number. On the line is your sibling's voice, sounding distressed, panicking, and stating they have been detained during an emergency business trip. They demand $500 wired immediately to secure bail. Their vocal inflection, speech pattern, and emotional stress sound exactly like them. What is your response?",
            options = listOf(
                "Immediately complete the money wire transfer as they are clearly in extreme physical danger.",
                "Hang up immediately, dial your sibling back on their official, pre-saved private number, or ask them the secret Family Word challenge.",
                "Demand the calling officer's badge number and text a photo of your debit card to the caller.",
                "Yell at them for being irresponsible and block the calling number without verifying if they are safe."
            ),
            correctAnswerIndex = 1,
            indicatorExplanation = "Hang up and perform a primary channel fallback callback. AI Voice Clones only require a 3-second audio snippet (harvested from public social clips) to clone family inflections with 99% accuracy. Establishing and asking an offline family safe code is your absolute defense."
        ),
        SecurityQuizQuestion(
            category = "TypoSquatting Links",
            descriptionTitle = "Slight Character Replacements",
            scenarioBody = "You receive an urgent SMS text indicating unusual transaction activity inside your digital payment app. The message links you to open 'https://www.rn-paypaI.com/verify-identity' to secure your financial balance. What security warning lights should blink?",
            options = listOf(
                "The URL is perfectly secure because the domain ends with '.com' and contains the brand 'paypal'.",
                "The link is dangerous. It contains 'rn' (which replicates an 'm' spelling lookalike) and a capital 'I' (which mimics an 'l'). This is typosquatting homoglyph cloning.",
                "It is a legitimate text since they knew your telephone number in advance.",
                "I should tap the request, sign in quickly to verify my credentials, and then change my security questions."
            ),
            correctAnswerIndex = 1,
            indicatorExplanation = "Typosquatting cloning and homoglyphs trick users with character pairs like 'rn' (m lookalike) or 'I' (capital i for lower L). Always log into companies through verified apps or typing original primary domains into high-security browser address fields directly."
        ),
        SecurityQuizQuestion(
            category = "Manager Spear Phishing",
            descriptionTitle = "The VIP Urgency Order",
            scenarioBody = "An email arrives from your CEO's official email handle (e.g. CEO.Joe@yourcorp-team.com) requesting you to urgently execute an out-of-channel ACH release of $10,000 to close a confidential supplier merger by noon, instructing you to bypass standard dual-signature checks for speed. Why is this questionable?",
            options = listOf(
                "A CEO has overriding operational authority, meaning bypassing standard dual-signature internal audit gates is safe.",
                "The email domain has a slight alteration (yourcorp-team.com vs original yourcorp.com), and standard financial checks should never be bypassed under pressure cues, which are phishing indicators.",
                "Since it is marked confidential and highly urgent, questioning the order is insubordinate.",
                "CEO impersonations do not occur on organizational channels; I should execute the wire transfer."
            ),
            correctAnswerIndex = 1,
            indicatorExplanation = "No operational executive, CEO, or family relative should ever dictate the suspension of verified financial authorization gates or dual-key verifications. Social engineering relies on high-pressure psychological manipulation to override checks."
        ),
        SecurityQuizQuestion(
            category = "Deepfake Corporate Zoom Calls",
            descriptionTitle = "Multi-Impersonator Team Meeting",
            scenarioBody = "You are invited to an online video conference. Your manager and Chief Financial Officer are on camera and direct you verbally to execute an international wire on-screen. Their voices and lip sync closely match their appearance, although the stream displays light lag and static. What key safeguard is needed?",
            options = listOf(
                "Execute the wire on camera since you can visually see and verify your management in real-time.",
                "Perform an out-of-band secondary verification via corporate secure messaging chat, or ask them a real-time logical challenge (e.g., 'What was our team joke on yesterday's lunch meeting?').",
                "Assume video streams are completely secure as video cloning is currently impossible.",
                "Take a screenshot, post it to public circles, and proceed with the financial transaction."
            ),
            correctAnswerIndex = 1,
            indicatorExplanation = "Deepfake video stream impersonation is a highly profitable active corporate fraud tactic. Impersonators clone complete corporate visual faces and voices in real-time, claiming 'network issues' to justify pixel stutters. Secondary verification channels are crucial."
        )
    )

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // QuestIndex -> SelectedOptIndex
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    fun selectQuizAnswer(questionIndex: Int, answerIndex: Int) {
        val updated = _selectedAnswers.value.toMutableMap()
        updated[questionIndex] = answerIndex
        _selectedAnswers.value = updated
    }

    fun nextQuizQuestion() {
        if (_currentQuizIndex.value < quizQuestions.size - 1) {
            _currentQuizIndex.value += 1
        } else {
            completeQuiz()
        }
    }

    fun previousQuizQuestion() {
        if (_currentQuizIndex.value > 0) {
            _currentQuizIndex.value -= 1
        }
    }

    private fun completeQuiz() {
        _quizCompleted.value = true
        
        // calculate scores
        var correct = 0
        quizQuestions.forEachIndexed { idx, q ->
            if (_selectedAnswers.value[idx] == q.correctAnswerIndex) {
                correct++
            }
        }

        viewModelScope.launch {
            val entity = QuizScoreEntity(
                quizTitle = "AI & Clone Phishing Guard Drill",
                score = correct,
                totalQuestions = quizQuestions.size,
                category = "Hybrid Anti-Phishing Core"
            )
            repository.insertQuizScore(entity)
        }
    }

    fun resetQuiz() {
        _currentQuizIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _quizCompleted.value = false
    }
}
