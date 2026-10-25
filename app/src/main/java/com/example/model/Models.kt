package com.example.model

data class EducatorProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val title: String = "Lead Educator & Blockchain Verifier",
    val institution: String = "Cambridge International Academy",
    val department: String = "Computer Science & Applied STEM",
    val staffId: String = "EDU-2026-9481",
    val bio: String = "Specializing in cryptography, distributed systems, and AI pedagogy.",
    val blockchainSignerKey: String = "0x7F9a38C0...88E1 (Verified Node Signer)",
    val subscriptionTier: String = "FREE_TRIAL",
    val lastSynced: String = "Just now"
)

enum class AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    EXCUSED
}

enum class AssessmentType {
    ASSIGNMENT,
    QUIZ,
    TEST,
    EXAM
}

data class Student(
    val id: String,
    val name: String,
    val rollNumber: String,
    val email: String,
    val classId: String,
    val avatarInitials: String,
    val attendanceRate: Float = 0.95f
)

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val studentName: String,
    val classId: String,
    val date: String,
    val status: AttendanceStatus,
    val note: String = "",
    val blockHash: String? = null,
    val isSealed: Boolean = false
)

data class Question(
    val id: String,
    val questionText: String,
    val type: String, // "Multiple Choice", "Short Answer", "Structured Essay", "Problem Solving"
    val options: List<String> = emptyList(),
    val correctOrModelAnswer: String,
    val marks: Int,
    val markingRubric: String = ""
)

data class Assessment(
    val id: String,
    val title: String,
    val subject: String,
    val gradeLevel: String,
    val type: AssessmentType,
    val curriculum: String,
    val questions: List<Question>,
    val totalMarks: Int,
    val durationMinutes: Int,
    val dueDate: String,
    val blockchainHash: String? = null,
    val isSealedOnChain: Boolean = false
)

data class GradeEntry(
    val id: String,
    val studentId: String,
    val studentName: String,
    val assessmentId: String,
    val assessmentTitle: String,
    val assessmentType: AssessmentType,
    val score: Float,
    val maxScore: Float,
    val feedback: String,
    val markedDate: String,
    val blockHash: String? = null,
    val isVerifiedOnChain: Boolean = false
) {
    val percentage: Float
        get() = if (maxScore > 0) (score / maxScore) * 100f else 0f

    val letterGrade: String
        get() = when {
            percentage >= 90f -> "A+"
            percentage >= 80f -> "A"
            percentage >= 70f -> "B"
            percentage >= 60f -> "C"
            percentage >= 50f -> "D"
            else -> "F"
        }
}

data class ScriptSubmission(
    val id: String,
    val studentId: String,
    val studentName: String,
    val assessmentTitle: String,
    val questionPrompt: String,
    val studentAnswer: String,
    val maxMarks: Int,
    val scoreAwarded: Int? = null,
    val aiEvaluationFeedback: String? = null,
    val rubricBreakdown: List<String> = emptyList(),
    val status: String = "PENDING", // PENDING, MARKED, VERIFIED
    val blockHash: String? = null
)

data class LessonNote(
    val id: String,
    val topic: String,
    val subject: String,
    val gradeLevel: String,
    val curriculum: String,
    val duration: String,
    val learningObjectives: List<String>,
    val keyTerms: List<String>,
    val teachingSteps: List<LessonStep>,
    val homeworkAssignment: String,
    val blockchainHash: String? = null,
    val createdAt: String
)

data class LessonStep(
    val stage: String, // "Hook & Warmup", "Direct Instruction", "Guided Practice", "Independent Activity", "Wrap-up & Assessment"
    val durationMin: Int,
    val description: String,
    val teacherNotes: String
)

data class SyllabusUnit(
    val unitNumber: Int,
    val title: String,
    val description: String,
    val estimatedHours: Int,
    val keyLearningOutcomes: List<String>,
    val examWeightage: String
)

data class CurriculumModel(
    val id: String,
    val name: String,
    val region: String,
    val description: String,
    val subjects: List<String>,
    val units: List<SyllabusUnit>
)

data class Textbook(
    val id: String,
    val title: String,
    val subject: String,
    val curriculum: String,
    val authors: String,
    val edition: String,
    val coverColor: Long,
    val chapters: List<TextbookChapter>
)

data class TextbookChapter(
    val chapterNumber: Int,
    val title: String,
    val summary: String,
    val coreTheories: List<String>,
    val sampleExercises: List<String>
)

data class BlockchainTx(
    val txId: String,
    val type: String, // ATTENDANCE_SEAL, GRADE_CERTIFICATE, EXAM_MARKING, INTEGRITY_STAMP
    val entitySummary: String,
    val payloadHash: String,
    val timestamp: Long,
    val teacherSigner: String = "Prof. J. Click (TeacherID #94)"
)

data class BlockchainBlock(
    val blockNumber: Long,
    val timestamp: Long,
    val previousHash: String,
    val merkleRoot: String,
    val hash: String,
    val nonce: Long,
    val transactions: List<BlockchainTx>,
    val validator: String = "EduChain Node #04 (Proof-of-Authority)"
)

enum class TierType {
    FREE_TRIAL,
    EDUCATOR_STARTER,
    EDUCATOR_PRO,
    SCHOOL_INSTITUTION,
    ENTERPRISE
}

data class SubscriptionPlan(
    val type: TierType,
    val name: String,
    val priceMonthly: Double,
    val priceAnnualMonthly: Double,
    val badge: String?,
    val description: String,
    val maxClasses: String,
    val aiQuotaPerDay: String,
    val blockchainSeals: String,
    val features: List<String>,
    val trialDaysRemaining: Int? = null
)
