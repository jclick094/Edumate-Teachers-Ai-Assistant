package com.example.data

import com.example.ai.GeminiAiService
import com.example.ai.ScriptEvaluationResult
import com.example.blockchain.EduChain
import com.example.blockchain.VerificationResult
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class TeacherRepository {

    private val _currentClass = MutableStateFlow("Grade 11 - Advanced Computer Science")
    val currentClass: StateFlow<String> = _currentClass.asStateFlow()

    val availableClasses = listOf(
        "Grade 11 - Advanced Computer Science",
        "Grade 10 - Cambridge Physics (0478)",
        "Grade 9 - Discrete Mathematics & Logic",
        "Grade 12 - Senior AP Calculus"
    )

    private val _students = MutableStateFlow<List<Student>>(MockData.sampleStudents)
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(
        MockData.sampleStudents.mapIndexed { idx, s ->
            AttendanceRecord(
                id = "att_$idx",
                studentId = s.id,
                studentName = s.name,
                classId = "class_cs",
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                status = when (idx) {
                    2 -> AttendanceStatus.LATE
                    4 -> AttendanceStatus.EXCUSED
                    7 -> AttendanceStatus.ABSENT
                    else -> AttendanceStatus.PRESENT
                },
                note = if (idx == 2) "Arrived 10 mins late due to transit" else "",
                blockHash = null,
                isSealed = false
            )
        }
    )
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance.asStateFlow()

    private val _grades = MutableStateFlow<List<GradeEntry>>(MockData.sampleGrades)
    val grades: StateFlow<List<GradeEntry>> = _grades.asStateFlow()

    private val _assessments = MutableStateFlow<List<Assessment>>(MockData.sampleAssessments)
    val assessments: StateFlow<List<Assessment>> = _assessments.asStateFlow()

    private val _scripts = MutableStateFlow<List<ScriptSubmission>>(MockData.sampleScriptsToMark)
    val scripts: StateFlow<List<ScriptSubmission>> = _scripts.asStateFlow()

    private val _lessonNotes = MutableStateFlow<List<LessonNote>>(MockData.sampleLessonNotes)
    val lessonNotes: StateFlow<List<LessonNote>> = _lessonNotes.asStateFlow()

    private val _curricula = MutableStateFlow<List<CurriculumModel>>(MockData.sampleCurricula)
    val curricula: StateFlow<List<CurriculumModel>> = _curricula.asStateFlow()

    private val _textbooks = MutableStateFlow<List<Textbook>>(MockData.sampleTextbooks)
    val textbooks: StateFlow<List<Textbook>> = _textbooks.asStateFlow()

    // Blockchain Ledger state
    private val genesisBlock = EduChain.createGenesisBlock()
    private val _blockchain = MutableStateFlow<List<BlockchainBlock>>(listOf(genesisBlock))
    val blockchain: StateFlow<List<BlockchainBlock>> = _blockchain.asStateFlow()

    // Subscriptions
    private val _activeTier = MutableStateFlow(TierType.FREE_TRIAL)
    val activeTier: StateFlow<TierType> = _activeTier.asStateFlow()

    private val _isAnnualBilling = MutableStateFlow(false)
    val isAnnualBilling: StateFlow<Boolean> = _isAnnualBilling.asStateFlow()

    private val _aiQuotaUsed = MutableStateFlow(7)
    val aiQuotaUsed: StateFlow<Int> = _aiQuotaUsed.asStateFlow()

    fun setClass(newClass: String) {
        _currentClass.value = newClass
    }

    fun updateAttendanceStatus(studentId: String, status: AttendanceStatus) {
        _attendance.value = _attendance.value.map {
            if (it.studentId == studentId) it.copy(status = status, isSealed = false, blockHash = null)
            else it
        }
    }

    fun markAllAttendancePresent() {
        _attendance.value = _attendance.value.map {
            it.copy(status = AttendanceStatus.PRESENT, isSealed = false, blockHash = null)
        }
    }

    fun sealAttendanceOnBlockchain(): BlockchainBlock {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val records = _attendance.value
        val summary = "Attendance Roll Call for ${_currentClass.value} ($currentDate) - ${records.count { it.status == AttendanceStatus.PRESENT }} Present, ${records.count { it.status == AttendanceStatus.ABSENT }} Absent"

        val payload = records.joinToString(";") { "${it.studentId}:${it.status}:${it.date}" }
        val payloadHash = EduChain.sha256(payload)

        val tx = BlockchainTx(
            txId = "0xATT_" + UUID.randomUUID().toString().substring(0, 8),
            type = "ATTENDANCE_ROLL_SEAL",
            entitySummary = summary,
            payloadHash = payloadHash,
            timestamp = System.currentTimeMillis()
        )

        val prevBlock = _blockchain.value.last()
        val newBlock = EduChain.mineBlock(
            blockNumber = prevBlock.blockNumber + 1,
            previousHash = prevBlock.hash,
            transactions = listOf(tx)
        )

        _blockchain.value = _blockchain.value + newBlock
        _attendance.value = _attendance.value.map { it.copy(isSealed = true, blockHash = newBlock.hash) }
        return newBlock
    }

    fun sealGradesOnBlockchain(): BlockchainBlock {
        val records = _grades.value
        val summary = "Academic Gradebook Audit Seal for ${_currentClass.value} - ${records.size} Student Assessment Marks"
        val payload = records.joinToString(";") { "${it.studentId}:${it.assessmentId}:${it.score}/${it.maxScore}" }
        val payloadHash = EduChain.sha256(payload)

        val tx = BlockchainTx(
            txId = "0xGRD_" + UUID.randomUUID().toString().substring(0, 8),
            type = "GRADEBOOK_AUDIT_SEAL",
            entitySummary = summary,
            payloadHash = payloadHash,
            timestamp = System.currentTimeMillis()
        )

        val prevBlock = _blockchain.value.last()
        val newBlock = EduChain.mineBlock(
            blockNumber = prevBlock.blockNumber + 1,
            previousHash = prevBlock.hash,
            transactions = listOf(tx)
        )

        _blockchain.value = _blockchain.value + newBlock
        _grades.value = _grades.value.map { it.copy(isVerifiedOnChain = true, blockHash = newBlock.hash) }
        return newBlock
    }

    fun addGrade(
        studentId: String,
        studentName: String,
        assessmentTitle: String,
        type: AssessmentType,
        score: Float,
        maxScore: Float,
        feedback: String
    ) {
        val entry = GradeEntry(
            id = "g_" + UUID.randomUUID().toString().substring(0, 6),
            studentId = studentId,
            studentName = studentName,
            assessmentId = "as_manual",
            assessmentTitle = assessmentTitle,
            assessmentType = type,
            score = score,
            maxScore = maxScore,
            feedback = feedback,
            markedDate = "Today",
            blockHash = null,
            isVerifiedOnChain = false
        )
        _grades.value = listOf(entry) + _grades.value
    }

    fun addAssessment(assessment: Assessment) {
        _assessments.value = listOf(assessment) + _assessments.value
    }

    fun addLessonNote(note: LessonNote) {
        _lessonNotes.value = listOf(note) + _lessonNotes.value
    }

    fun markScript(
        submissionId: String,
        score: Int,
        feedback: String,
        rubric: List<String>
    ): BlockchainBlock {
        val sub = _scripts.value.find { it.id == submissionId }
            ?: throw IllegalArgumentException("Submission not found")

        val payload = "${sub.studentId}:${sub.assessmentTitle}:$score/${sub.maxMarks}:$feedback"
        val payloadHash = EduChain.sha256(payload)

        val tx = BlockchainTx(
            txId = "0xSCR_" + UUID.randomUUID().toString().substring(0, 8),
            type = "EXAM_SCRIPT_MARKING",
            entitySummary = "Graded Script: ${sub.studentName} for '${sub.assessmentTitle}' - Score: $score/${sub.maxMarks}",
            payloadHash = payloadHash,
            timestamp = System.currentTimeMillis()
        )

        val prevBlock = _blockchain.value.last()
        val newBlock = EduChain.mineBlock(
            blockNumber = prevBlock.blockNumber + 1,
            previousHash = prevBlock.hash,
            transactions = listOf(tx)
        )

        _blockchain.value = _blockchain.value + newBlock

        _scripts.value = _scripts.value.map {
            if (it.id == submissionId) {
                it.copy(
                    scoreAwarded = score,
                    aiEvaluationFeedback = feedback,
                    rubricBreakdown = rubric,
                    status = "MARKED",
                    blockHash = newBlock.hash
                )
            } else it
        }

        // Also add to Gradebook!
        addGrade(
            studentId = sub.studentId,
            studentName = sub.studentName,
            assessmentTitle = sub.assessmentTitle,
            type = AssessmentType.EXAM,
            score = score.toFloat(),
            maxScore = sub.maxMarks.toFloat(),
            feedback = feedback
        )

        return newBlock
    }

    fun verifyItemIntegrity(payload: String, blockHash: String): VerificationResult {
        return EduChain.verifyIntegrity(payload, blockHash, _blockchain.value)
    }

    fun setSubscriptionTier(tier: TierType) {
        _activeTier.value = tier
    }

    fun toggleAnnualBilling() {
        _isAnnualBilling.value = !_isAnnualBilling.value
    }

    fun incrementAiUsage() {
        _aiQuotaUsed.value += 1
    }
}
