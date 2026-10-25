package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.AttendanceRecord
import com.example.model.EducatorProfile
import com.example.model.GradeEntry
import com.example.model.LessonNote
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreTeacherRepository(
    private val db: FirebaseFirestore
) {
    // Secondary constructor that resolves the custom database ID from strings.xml
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth
    private val TAG = "FirestoreTeacherRepo"

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Teacher must be authenticated with Google before accessing Firestore.")
    }

    suspend fun saveEducatorProfile(profile: EducatorProfile) {
        val uid = requireUserId()
        val data = mapOf(
            "userId" to uid,
            "displayName" to profile.displayName,
            "email" to profile.email,
            "title" to profile.title,
            "institution" to profile.institution,
            "department" to profile.department,
            "staffId" to profile.staffId,
            "bio" to profile.bio,
            "blockchainSignerKey" to profile.blockchainSignerKey,
            "subscriptionTier" to profile.subscriptionTier,
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        try {
            db.collection("users").document(uid).set(data, SetOptions.merge()).await()
            Log.d(TAG, "Educator profile saved to Firestore for $uid")
        } catch (e: Exception) {
            Log.w(TAG, "Error saving educator profile: ${e.message}")
        }
    }

    fun observeEducatorProfile(userId: String): Flow<EducatorProfile?> {
        return db.collection("users").document(userId)
            .snapshots()
            .map { snap ->
                if (snap.exists()) {
                    EducatorProfile(
                        userId = snap.getString("userId") ?: userId,
                        displayName = snap.getString("displayName") ?: "",
                        email = snap.getString("email") ?: "",
                        title = snap.getString("title") ?: "Lead Educator & Blockchain Verifier",
                        institution = snap.getString("institution") ?: "Cambridge International Academy",
                        department = snap.getString("department") ?: "Computer Science & Applied STEM",
                        staffId = snap.getString("staffId") ?: "EDU-2026-9481",
                        bio = snap.getString("bio") ?: "Specializing in cryptography, distributed systems, and AI pedagogy.",
                        blockchainSignerKey = snap.getString("blockchainSignerKey") ?: "0x7F9a38C0...88E1 (Verified Node Signer)",
                        subscriptionTier = snap.getString("subscriptionTier") ?: "FREE_TRIAL",
                        lastSynced = snap.getString("updatedAt") ?: "Just now"
                    )
                } else null
            }
            .catch { e ->
                Log.w(TAG, "Error observing educator profile: ${e.message}")
                emit(null)
            }
    }

    suspend fun saveUserProfile(displayName: String?, email: String?, subscriptionTier: String) {
        val uid = requireUserId()
        val data = mapOf(
            "userId" to uid,
            "displayName" to (displayName ?: "Educator"),
            "email" to (email ?: ""),
            "subscriptionTier" to subscriptionTier,
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        try {
            db.collection("users").document(uid).set(data, SetOptions.merge()).await()
            Log.d(TAG, "User profile synced to Firestore: $uid")
        } catch (e: Exception) {
            Log.w(TAG, "Error saving user profile: ${e.message}")
        }
    }

    suspend fun syncAttendanceRecord(record: AttendanceRecord) {
        val uid = requireUserId()
        val data = mapOf(
            "studentId" to record.studentId,
            "studentName" to record.studentName,
            "classId" to record.classId,
            "date" to record.date,
            "status" to record.status.name,
            "note" to record.note,
            "blockHash" to record.blockHash,
            "isSealed" to record.isSealed,
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        try {
            db.collection("users")
                .document(uid)
                .collection("attendance")
                .document(record.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Attendance record ${record.id} synced to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing attendance record: ${e.message}")
        }
    }

    suspend fun syncGradeEntry(grade: GradeEntry) {
        val uid = requireUserId()
        val data = mapOf(
            "studentId" to grade.studentId,
            "studentName" to grade.studentName,
            "assessmentTitle" to grade.assessmentTitle,
            "assessmentType" to grade.assessmentType.name,
            "score" to grade.score,
            "maxScore" to grade.maxScore,
            "feedback" to grade.feedback,
            "blockHash" to grade.blockHash,
            "isVerifiedOnChain" to grade.isVerifiedOnChain,
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        try {
            db.collection("users")
                .document(uid)
                .collection("grades")
                .document(grade.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Grade entry ${grade.id} synced to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing grade: ${e.message}")
        }
    }

    suspend fun syncLessonNote(note: LessonNote) {
        val uid = requireUserId()
        val data = mapOf(
            "topic" to note.topic,
            "subject" to note.subject,
            "gradeLevel" to note.gradeLevel,
            "curriculum" to note.curriculum,
            "duration" to note.duration,
            "homeworkAssignment" to note.homeworkAssignment,
            "blockchainHash" to note.blockchainHash,
            "createdAt" to note.createdAt
        )
        try {
            db.collection("users")
                .document(uid)
                .collection("lesson_notes")
                .document(note.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Lesson note ${note.id} synced to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing lesson note: ${e.message}")
        }
    }
}
