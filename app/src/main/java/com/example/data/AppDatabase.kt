package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatScanDao {
    @Query("SELECT * FROM threat_scans ORDER BY date DESC")
    fun getAllThreadScans(): Flow<List<ThreatScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ThreatScanEntity): Long

    @Query("DELETE FROM threat_scans WHERE id = :id")
    suspend fun deleteScanById(id: Long)

    @Query("DELETE FROM threat_scans")
    suspend fun clearAllHistory()
}

@Dao
interface FamilyCodeDao {
    @Query("SELECT * FROM family_codes ORDER BY dateSet DESC")
    fun getAllFamilyCodes(): Flow<List<FamilyCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyCode(code: FamilyCodeEntity): Long

    @Query("DELETE FROM family_codes WHERE id = :id")
    suspend fun deleteFamilyCodeById(id: Long)

    @Query("UPDATE family_codes SET isVerified = :verified WHERE id = :id")
    suspend fun updateVerificationStatus(id: Long, verified: Boolean)
}

@Dao
interface QuizScoreDao {
    @Query("SELECT * FROM quiz_scores ORDER BY dateCompleted DESC")
    fun getAllQuizScores(): Flow<List<QuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(score: QuizScoreEntity): Long

    @Query("SELECT SUM(score) FROM quiz_scores")
    suspend fun getTotalCorrectAnswers(): Int?

    @Query("SELECT SUM(totalQuestions) FROM quiz_scores")
    suspend fun getTotalQuestionsAsked(): Int?
}

@Dao
interface ScamReportDao {
    @Query("SELECT * FROM scam_reports ORDER BY dateSubmitted DESC")
    fun getAllScamReports(): Flow<List<ScamReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScamReport(report: ScamReportEntity): Long

    @Query("DELETE FROM scam_reports WHERE id = :id")
    suspend fun deleteScamReportById(id: Long)

    @Query("DELETE FROM scam_reports")
    suspend fun clearAllScamReports()
}

@Database(
    entities = [ThreatScanEntity::class, FamilyCodeEntity::class, QuizScoreEntity::class, ScamReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatScanDao(): ThreatScanDao
    abstract fun familyCodeDao(): FamilyCodeDao
    abstract fun quizScoreDao(): QuizScoreDao
    abstract fun scamReportDao(): ScamReportDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cloneguard_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
