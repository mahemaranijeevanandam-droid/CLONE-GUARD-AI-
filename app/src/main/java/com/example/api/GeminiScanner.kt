package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiScanner {
    private const val TAG = "GeminiScanner"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    // OkHttpClient with 60-second timeouts as requested under Gemin API Gotchas in SKILL.md
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class ScanResult(
        val threatLevel: String, // LOW, MEDIUM, HIGH, CRITICAL
        val confidenceScore: Int,
        val indicators: List<String>,
        val analysisReport: String
    )

    suspend fun scanContent(type: String, content: String): ScanResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Empty API key or default placeholder used.")
            return@withContext ScanResult(
                threatLevel = "LOW",
                confidenceScore = 0,
                indicators = listOf("Sandbox Environment", "Missing API Key"),
                analysisReport = "Gemini API Key is not configured in AI Studio Secrets panel. This scan is running in Safe Offline Sandbox Mode. To activate real-time intelligence scanning, please register a valid Gemini API Key in the panel settings.\n\nInput Scanned:\n$content"
            )
        }

        val prompt = """
            You are a specialized cybersecurity defense engine called CloneGuard AI, designed to protect everyday users from AI voice-cloning, deepfakes, text spoofing, and advanced clone phishing attacks.
            
            You have received a suspect element corresponding to a ${type.uppercase()} scan.
            
            INPUT TO SCAN:
            -----------------
            $content
            -----------------
            
            Analyze this content and identify cybersecurity identifiers:
            1. Is it likely synthesized by AI, cloned, or written with typical social engineering cues?
            2. Are there warning indicators of high-pressure demands, suspicious account confirmations, or lookalike links?
            3. If it has voice transcripts, does it contain emergency demands typical of "Kidnapping/Manager Voice Clone Scams" (e.g. asking for immediate untraceable payments)?

            You MUST respond strictly in valid JSON format (with no wrapping markdown backticks, just raw json) containing the following fields:
            - "threatLevel": strictly one of "LOW", "MEDIUM", "HIGH", "CRITICAL".
            - "confidenceScore": integer 0 to 100 of your suspicion.
            - "indicators": an array of short string red flags (e.g. ["Synthetic Urgency", "Relative Impersonation Call", "Suspicious URL Homoglyph", "Aggressive CTA"]).
            - "analysisReport": detailed educational summary explaining why it is or is not flagged, what defensive response steps the user should execute immediately, and a reminder to use verification protocols. Ensure it is written in a friendly, secure, clear tone.
        """.trimIndent()

        try {
            // Build request json
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)
                
                // request structured JSON configuration
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.5)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errCode = response.code
                    val errMsg = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response from Gemini. Code: $errCode, Msg: $errMsg")
                    return@withContext fallbackScan(type, content, "API returned $errCode: $errMsg")
                }

                val responseBodyStr = response.body?.string() ?: ""
                Log.d(TAG, "Raw Response: $responseBodyStr")

                val parsedJson = JSONObject(responseBodyStr)
                val candidates = parsedJson.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val parts = candidates.getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                    if (parts.length() > 0) {
                        val txtResponse = parts.getJSONObject(0).getString("text")
                        return@withContext parseGeminiOutput(txtResponse)
                    }
                }
                return@withContext fallbackScan(type, content, "Unable to extract response candidate parts.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing scanning request", e)
            return@withContext fallbackScan(type, content, "Local error: ${e.localizedMessage}")
        }
    }

    private fun parseGeminiOutput(rawText: String): ScanResult {
        try {
            // clean potential markdown wrappers
            var cleanText = rawText.trim()
            if (cleanText.startsWith("```json")) {
                cleanText = cleanText.substringAfter("```json")
            }
            if (cleanText.startsWith("```")) {
                cleanText = cleanText.substringAfter("```")
            }
            if (cleanText.endsWith("```")) {
                cleanText = cleanText.substringBeforeLast("```")
            }
            cleanText = cleanText.trim()

            val json = JSONObject(cleanText)
            val threatLevel = json.optString("threatLevel", "LOW").uppercase()
            val confidenceScore = json.optInt("confidenceScore", 10)
            
            val indicatorsList = mutableListOf<String>()
            val indicatorsJson = json.optJSONArray("indicators")
            if (indicatorsJson != null) {
                for (i in 0 until indicatorsJson.length()) {
                    indicatorsList.add(indicatorsJson.getString(i))
                }
            } else {
                indicatorsList.add("Syntactic Pattern Match")
            }

            val analysisReport = json.optString("analysisReport", "No report details obtained from analysis module.")

            return ScanResult(
                threatLevel = threatLevel,
                confidenceScore = confidenceScore,
                indicators = indicatorsList,
                analysisReport = analysisReport
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini structured output, falling back to manual: ${e.message}", e)
            return rawTextFallback(rawText)
        }
    }

    private fun rawTextFallback(rawText: String): ScanResult {
        // Simple extraction fallback if model returned plain string
        val isPhish = rawText.contains("phish", ignoreCase = true) || 
                      rawText.contains("urgenc", ignoreCase = true) || 
                      rawText.contains("attack", ignoreCase = true) ||
                      rawText.contains("clon", ignoreCase = true)
        return ScanResult(
            threatLevel = if (isPhish) "HIGH" else "LOW",
            confidenceScore = if (isPhish) 75 else 25,
            indicators = listOf("Linguistic Extraction"),
            analysisReport = rawText
        )
    }

    private fun fallbackScan(type: String, content: String, errorMessage: String): ScanResult {
        Log.d(TAG, "Running offline fallback scanner due to: $errorMessage")
        
        // Comprehensive rule-based engine simulating detection if offline or key is missing
        val contentLower = content.lowercase()
        val indicators = mutableListOf<String>()
        var level = "LOW"
        var score = 10
        val report = StringBuilder()
        
        report.append("🔒 [OFFLINE SECURITY DETECTOR] Run complete.\n\n")

        if (type == "link" || type == "url") {
            val urlPattern = "https?://[^\\s/$.?#].[^\\s]*"
            if (contentLower.contains(".xyz") || contentLower.contains(".info") || contentLower.contains(".cc") || contentLower.contains(".top") || contentLower.contains(".cf")) {
                indicators.add("Suspicious Low-Cost TLD")
                level = "HIGH"
                score = 80
            }
            if (contentLower.contains("login") || contentLower.contains("signin") || contentLower.contains("verify") || contentLower.contains("bank") || contentLower.contains("account")) {
                indicators.add("Lookalike Banking/Authentication Path")
                if (level == "LOW") level = "MEDIUM"
                score = maxOf(score, 65)
            }
            if (contentLower.contains("paypal") && !contentLower.contains("paypal.com")) {
                indicators.add("Domain Homoglyph / Spoofing Attempt")
                level = "CRITICAL"
                score = 95
            }
            if (indicators.isEmpty()) {
                report.append("Result: This link does not trigger immediate heuristic phishing alerts, but always inspect the domain carefully in your address bar before typing passwords.\n\n")
            } else {
                report.append("Warning: Multiple heuristic alerts triggered for input URL.\n\n")
            }
        } else {
            // Text or Call Transcript scans
            if (contentLower.contains("grandpa") || contentLower.contains("grandma") || contentLower.contains("mom") || contentLower.contains("dad") || contentLower.contains("kidnap") || contentLower.contains("accident") || contentLower.contains("jail") || contentLower.contains("bail")) {
                indicators.add("Simulated Family Clone Scenario")
                level = "HIGH"
                score = 85
                report.append("⚠️ CRITICAL ALERT: The transcript contains family emergency triggers (bail, jail, accident). This is the absolute signature of AI Voice Clone scam calls. Scammers capture a 3-second audio clip of your family member, clone their voice, and call you claiming they are in an urgent emergency demanding cash/giftcards.\n\n")
            }
            if (contentLower.contains("wired") || contentLower.contains("gift card") || contentLower.contains("crypto") || contentLower.contains("bitcoin") || contentLower.contains("zelle") || contentLower.contains("transfer immediately")) {
                indicators.add("Untraceable Urgent Payment Request")
                if (level == "LOW") level = "MEDIUM"
                score = maxOf(score, 75)
            }
            if (contentLower.contains("immediate action") || contentLower.contains("urgently") || contentLower.contains("warn") || contentLower.contains("block your account") || contentLower.contains("suspended")) {
                indicators.add("High-Pressure Coercion Tactics")
                if (level == "LOW") level = "MEDIUM"
                score = maxOf(score, 60)
            }
        }

        if (indicators.isEmpty()) {
            level = "LOW"
            score = 15
            indicators.add("Standard Pattern match")
            report.append("Scan completed with clear status. No obvious AI cloning scripts or phishing lookalikes were triggered via basic local heuristic scanning.\n\nRemember: Always confirm sender address when sensitive data is requested.")
        } else {
            if (score >= 80) {
                level = "CRITICAL"
            }
            report.append("Defense Protocols Suggested:\n")
            report.append("1. **Do Not Act**: Do not wire money, buy gift cards, or open suspicious links.\n")
            report.append("2. **Callback Security**: Hang up immediately and call the family member or company back using their pre-saved official phone number — NOT the number calling you.\n")
            report.append("3. **Challenge Phrase**: Ask the caller for your Family Security Code Word or secret challenge answer (Configure this in the protection screen!).")
        }

        report.append("\n\n*(Analysis provided by CloneGuard Offline Intelligence Core; register an API Key to enable deeper Gemini Cognitive Threat Analysis)*")

        return ScanResult(
            threatLevel = level,
            confidenceScore = score,
            indicators = indicators,
            analysisReport = report.toString()
        )
    }
}
