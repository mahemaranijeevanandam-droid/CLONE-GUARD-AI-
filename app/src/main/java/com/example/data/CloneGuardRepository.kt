package com.example.data

import kotlinx.coroutines.flow.Flow

class CloneGuardRepository(private val database: AppDatabase) {

    val threatScans: Flow<List<ThreatScanEntity>> = database.threatScanDao().getAllThreadScans()
    
    val familyCodes: Flow<List<FamilyCodeEntity>> = database.familyCodeDao().getAllFamilyCodes()
    
    val quizScores: Flow<List<QuizScoreEntity>> = database.quizScoreDao().getAllQuizScores()

    val scamReports: Flow<List<ScamReportEntity>> = database.scamReportDao().getAllScamReports()
    
    suspend fun insertScan(scan: ThreatScanEntity): Long {
        return database.threatScanDao().insertScan(scan)
    }

    suspend fun deleteScanById(id: Long) {
        database.threatScanDao().deleteScanById(id)
    }

    suspend fun clearScanHistory() {
        database.threatScanDao().clearAllHistory()
    }

    suspend fun insertFamilyCode(code: FamilyCodeEntity): Long {
        return database.familyCodeDao().insertFamilyCode(code)
    }

    suspend fun deleteFamilyCodeById(id: Long) {
        database.familyCodeDao().deleteFamilyCodeById(id)
    }

    suspend fun updateFamilyVerification(id: Long, verified: Boolean) {
        database.familyCodeDao().updateVerificationStatus(id, verified)
    }

    suspend fun insertQuizScore(score: QuizScoreEntity): Long {
        return database.quizScoreDao().insertQuizScore(score)
    }

    suspend fun getQuizStats(): Pair<Int, Int> {
        val correct = database.quizScoreDao().getTotalCorrectAnswers() ?: 0
        val total = database.quizScoreDao().getTotalQuestionsAsked() ?: 0
        return Pair(correct, total)
    }

    suspend fun insertScamReport(report: ScamReportEntity): Long {
        return database.scamReportDao().insertScamReport(report)
    }

    suspend fun deleteScamReportById(id: Long) {
        database.scamReportDao().deleteScamReportById(id)
    }

    suspend fun clearScamReports() {
        database.scamReportDao().clearAllScamReports()
    }
}
