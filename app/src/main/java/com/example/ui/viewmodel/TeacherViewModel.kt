package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.ai.ScriptEvaluationResult
import com.example.blockchain.VerificationResult
import com.example.data.TeacherRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val repository: TeacherRepository = TeacherRepository(),
    private val firestoreRepository: com.example.data.FirestoreTeacherRepository? = null
) : ViewModel() {

    private val _educatorProfile = MutableStateFlow(EducatorProfile())
    val educatorProfile: StateFlow<EducatorProfile> = _educatorProfile.asStateFlow()

    fun initUserSession(user: com.google.firebase.auth.FirebaseUser?) {
        if (user != null) {
            val initial = _educatorProfile.value.copy(
                userId = user.uid,
                displayName = user.displayName ?: "Professor / Educator",
                email = user.email ?: "",
                subscriptionTier = activeTier.value.name
            )
            _educatorProfile.value = initial

            if (firestoreRepository != null) {
                viewModelScope.launch {
                    firestoreRepository.saveEducatorProfile(initial)
                    firestoreRepository.observeEducatorProfile(user.uid).collect { updated ->
                        if (updated != null) {
                            _educatorProfile.value = updated
                        }
                    }
                }
            }
        }
    }

    fun updateEducatorProfile(
        title: String,
        institution: String,
        department: String,
        staffId: String,
        bio: String
    ) {
        val current = _educatorProfile.value
        val updated = current.copy(
            title = title,
            institution = institution,
            department = department,
            staffId = staffId,
            bio = bio,
            lastSynced = "Just now"
        )
        _educatorProfile.value = updated
        _statusMessage.value = "Educator Profile updated and synced to Firestore"

        if (firestoreRepository != null && current.userId.isNotBlank()) {
            viewModelScope.launch {
                firestoreRepository.saveEducatorProfile(updated)
            }
        }
    }

    val currentClass: StateFlow<String> = repository.currentClass
    val availableClasses: List<String> = repository.availableClasses
    val students: StateFlow<List<Student>> = repository.students
    val attendance: StateFlow<List<AttendanceRecord>> = repository.attendance
    val grades: StateFlow<List<GradeEntry>> = repository.grades
    val assessments: StateFlow<List<Assessment>> = repository.assessments
    val scripts: StateFlow<List<ScriptSubmission>> = repository.scripts
    val lessonNotes: StateFlow<List<LessonNote>> = repository.lessonNotes
    val curricula: StateFlow<List<CurriculumModel>> = repository.curricula
    val textbooks: StateFlow<List<Textbook>> = repository.textbooks
    val blockchain: StateFlow<List<BlockchainBlock>> = repository.blockchain
    val activeTier: StateFlow<TierType> = repository.activeTier
    val isAnnualBilling: StateFlow<Boolean> = repository.isAnnualBilling
    val aiQuotaUsed: StateFlow<Int> = repository.aiQuotaUsed

    // Active bottom navigation tab
    // 0: AI Studio, 1: Attendance, 2: Gradebook, 3: Curriculum & Books, 4: Blockchain & Subscriptions
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Sub-tab in AI Studio (0: Notes Generator, 1: Assessment Creator, 2: Script Grader, 3: AI Co-pilot)
    private val _aiSubTab = MutableStateFlow(0)
    val aiSubTab: StateFlow<Int> = _aiSubTab.asStateFlow()

    // Loading indicators
    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // AI Generation preview states
    private val _previewNote = MutableStateFlow<LessonNote?>(null)
    val previewNote: StateFlow<LessonNote?> = _previewNote.asStateFlow()

    private val _previewAssessment = MutableStateFlow<Assessment?>(null)
    val previewAssessment: StateFlow<Assessment?> = _previewAssessment.asStateFlow()

    private val _scriptEvaluationResult = MutableStateFlow<ScriptEvaluationResult?>(null)
    val scriptEvaluationResult: StateFlow<ScriptEvaluationResult?> = _scriptEvaluationResult.asStateFlow()

    private val _activeScriptToGrade = MutableStateFlow<ScriptSubmission?>(null)
    val activeScriptToGrade: StateFlow<ScriptSubmission?> = _activeScriptToGrade.asStateFlow()

    // AI Co-pilot chat
    private val _copilotMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello Professor! I am your EduChain AI Teaching Assistant. How can I help you refine your lesson plans, mark scripts, or design assessments today?" to false
        )
    )
    val copilotMessages: StateFlow<List<Pair<String, Boolean>>> = _copilotMessages.asStateFlow()

    // Verification modal state
    private val _verificationResult = MutableStateFlow<VerificationResult?>(null)
    val verificationResult: StateFlow<VerificationResult?> = _verificationResult.asStateFlow()

    // Feature 1: Live Voice Conversation with gemini-3.8-live
    private val _isLiveVoiceConnected = MutableStateFlow(false)
    val isLiveVoiceConnected: StateFlow<Boolean> = _isLiveVoiceConnected.asStateFlow()

    private val _liveVoiceMessages = MutableStateFlow<List<com.example.ai.LiveVoiceMessage>>(
        listOf(
            com.example.ai.LiveVoiceMessage(
                sender = "gemini-3.8-live",
                text = "Welcome to EduChain Live Voice. I am powered by gemini-3.8-live for real-time pedagogical dialogue. Tap Speak to converse with me directly.",
                timestamp = "Just now"
            )
        )
    )
    val liveVoiceMessages: StateFlow<List<com.example.ai.LiveVoiceMessage>> = _liveVoiceMessages.asStateFlow()

    // Feature 2: Google Search Grounding with gemini-3.8-flash (with googleSearch tool)
    private val _groundedSearchResult = MutableStateFlow<com.example.ai.GroundedSearchResult?>(null)
    val groundedSearchResult: StateFlow<com.example.ai.GroundedSearchResult?> = _groundedSearchResult.asStateFlow()

    // Feature 3: Google Maps Grounding with gemini-3.8-flash (with googleMaps tool)
    private val _mapsGroundingResult = MutableStateFlow<com.example.ai.MapsGroundingResult?>(null)
    val mapsGroundingResult: StateFlow<com.example.ai.MapsGroundingResult?> = _mapsGroundingResult.asStateFlow()

    // Feature 4: Audio Transcription with gemini-3.5-transcribe
    private val _transcribedText = MutableStateFlow<String?>(null)
    val transcribedText: StateFlow<String?> = _transcribedText.asStateFlow()

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectAiSubTab(index: Int) {
        _aiSubTab.value = index
    }

    fun switchClass(newClass: String) {
        repository.setClass(newClass)
        _statusMessage.value = "Switched to $newClass"
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun updateAttendance(studentId: String, status: AttendanceStatus) {
        repository.updateAttendanceStatus(studentId, status)
    }

    fun markAllPresent() {
        repository.markAllAttendancePresent()
        _statusMessage.value = "All students marked Present"
    }

    fun sealAttendance() {
        val block = repository.sealAttendanceOnBlockchain()
        _statusMessage.value = "Attendance Sealed on EduChain! Block #${block.blockNumber} (Hash: ${block.hash.take(12)}...)"
        if (firestoreRepository != null) {
            viewModelScope.launch {
                attendance.value.forEach { rec ->
                    firestoreRepository.syncAttendanceRecord(rec)
                }
            }
        }
    }

    fun sealGradebook() {
        val block = repository.sealGradesOnBlockchain()
        _statusMessage.value = "Gradebook Certified on EduChain! Block #${block.blockNumber} (Hash: ${block.hash.take(12)}...)"
        if (firestoreRepository != null) {
            viewModelScope.launch {
                grades.value.forEach { g ->
                    firestoreRepository.syncGradeEntry(g)
                }
            }
        }
    }

    fun addManualGrade(
        studentId: String,
        studentName: String,
        title: String,
        type: AssessmentType,
        score: Float,
        maxScore: Float,
        feedback: String
    ) {
        repository.addGrade(studentId, studentName, title, type, score, maxScore, feedback)
        _statusMessage.value = "Added grade for $studentName"
        if (firestoreRepository != null) {
            viewModelScope.launch {
                val latest = grades.value.firstOrNull()
                if (latest != null) {
                    firestoreRepository.syncGradeEntry(latest)
                }
            }
        }
    }

    fun generateLessonNote(
        topic: String,
        subject: String,
        gradeLevel: String,
        curriculum: String
    ) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val note = GeminiAiService.generateLessonNote(topic, subject, gradeLevel, curriculum)
                _previewNote.value = note
                _statusMessage.value = "AI Lesson Plan Generated!"
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun savePreviewNote() {
        val note = _previewNote.value ?: return
        repository.addLessonNote(note)
        _previewNote.value = null
        _statusMessage.value = "Lesson Note saved to repository"
        if (firestoreRepository != null) {
            viewModelScope.launch {
                firestoreRepository.syncLessonNote(note)
            }
        }
    }

    fun discardPreviewNote() {
        _previewNote.value = null
    }

    fun generateAssessment(
        title: String,
        subject: String,
        gradeLevel: String,
        type: AssessmentType,
        numQuestions: Int,
        curriculum: String
    ) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val assessment = GeminiAiService.generateAssessment(title, subject, gradeLevel, type, numQuestions, curriculum)
                _previewAssessment.value = assessment
                _statusMessage.value = "AI ${type.name} with questions created!"
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun savePreviewAssessment() {
        val assessment = _previewAssessment.value ?: return
        repository.addAssessment(assessment)
        _previewAssessment.value = null
        _statusMessage.value = "Assessment saved to curriculum library"
    }

    fun discardPreviewAssessment() {
        _previewAssessment.value = null
    }

    fun openScriptGrading(script: ScriptSubmission) {
        _activeScriptToGrade.value = script
        _scriptEvaluationResult.value = null
    }

    fun closeScriptGrading() {
        _activeScriptToGrade.value = null
        _scriptEvaluationResult.value = null
    }

    fun runAiScriptEvaluation(script: ScriptSubmission) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val result = GeminiAiService.evaluateScript(
                    questionPrompt = script.questionPrompt,
                    studentAnswer = script.studentAnswer,
                    maxMarks = script.maxMarks,
                    markingGuide = "Strict Cambridge / AP standard rubric. Award marks for precision, vocabulary, and deduction."
                )
                _scriptEvaluationResult.value = result
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun confirmScriptGrading(
        submissionId: String,
        score: Int,
        feedback: String,
        rubric: List<String>
    ) {
        val block = repository.markScript(submissionId, score, feedback, rubric)
        _activeScriptToGrade.value = null
        _scriptEvaluationResult.value = null
        _statusMessage.value = "Script Graded & Sealed into Block #${block.blockNumber}!"
    }

    fun sendCopilotMessage(query: String) {
        if (query.isBlank()) return
        val updated = _copilotMessages.value + (query to true)
        _copilotMessages.value = updated

        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val response = GeminiAiService.askAiCopilot(query, currentClass.value)
                _copilotMessages.value = _copilotMessages.value + (response to false)
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun verifyTamperStatus(payload: String, blockHash: String) {
        val result = repository.verifyItemIntegrity(payload, blockHash)
        _verificationResult.value = result
    }

    fun dismissVerificationModal() {
        _verificationResult.value = null
    }

    fun changeSubscriptionTier(tier: TierType) {
        repository.setSubscriptionTier(tier)
        _statusMessage.value = "Subscribed to ${tier.name.replace("_", " ")} plan!"
    }

    fun toggleBillingCycle() {
        repository.toggleAnnualBilling()
    }

    // Feature 1: Live Voice Conversation with gemini-3.8-live
    fun toggleLiveVoice() {
        _isLiveVoiceConnected.value = !_isLiveVoiceConnected.value
        _statusMessage.value = if (_isLiveVoiceConnected.value) "Connected to gemini-3.8-live Live API" else "Live Voice Disconnected"
    }

    fun sendLiveVoiceUtterance(spokenText: String) {
        if (spokenText.isBlank()) return
        val userMsg = com.example.ai.LiveVoiceMessage(
            sender = "teacher",
            text = spokenText,
            timestamp = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        )
        _liveVoiceMessages.value = _liveVoiceMessages.value + userMsg

        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val liveReply = GeminiAiService.converseWithLiveApi(spokenText, _liveVoiceMessages.value)
                _liveVoiceMessages.value = _liveVoiceMessages.value + liveReply
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    // Feature 2: Google Search Grounding with gemini-3.8-flash (with googleSearch tool)
    fun searchCurriculumWithGoogle(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val result = GeminiAiService.searchCurriculumWithGoogle(query)
                _groundedSearchResult.value = result
                _statusMessage.value = "Retrieved Google Search grounded curriculum data"
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun clearGroundedSearchResult() {
        _groundedSearchResult.value = null
    }

    // Feature 3: Google Maps Grounding with gemini-3.8-flash (with googleMaps tool)
    fun searchVenuesWithMaps(query: String, location: String = "London / Cambridge Region") {
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                repository.incrementAiUsage()
                val result = GeminiAiService.searchAcademicVenuesWithMaps(query, location)
                _mapsGroundingResult.value = result
                _statusMessage.value = "Retrieved Google Maps grounded educational venues"
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun clearMapsGroundingResult() {
        _mapsGroundingResult.value = null
    }

    // Feature 4: Audio Transcription with gemini-3.5-transcribe
    fun transcribeAudioInput(audioBase64: String = "DUMMY_AUDIO_BASE64", mimeType: String = "audio/mp4") {
        viewModelScope.launch {
            _isTranscribing.value = true
            try {
                repository.incrementAiUsage()
                val text = GeminiAiService.transcribeAudio(audioBase64, mimeType)
                _transcribedText.value = text
                _statusMessage.value = "Voice transcription completed via gemini-3.5-transcribe"
            } finally {
                _isTranscribing.value = false
            }
        }
    }

    fun clearTranscribedText() {
        _transcribedText.value = null
    }
}
