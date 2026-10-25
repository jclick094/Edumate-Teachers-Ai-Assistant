package com.example.data

import com.example.model.*

object MockData {

    val sampleStudents = listOf(
        Student("s1", "Alex Rivera", "CS-101", "alex.r@school.edu", "class_cs", "AR", 0.96f),
        Student("s2", "Priya Sharma", "CS-102", "priya.s@school.edu", "class_cs", "PS", 0.98f),
        Student("s3", "Marcus Chen", "CS-103", "marcus.c@school.edu", "class_cs", "MC", 0.88f),
        Student("s4", "Zoe Al-Mansoor", "CS-104", "zoe.m@school.edu", "class_cs", "ZA", 0.94f),
        Student("s5", "Liam O'Connor", "CS-105", "liam.o@school.edu", "class_cs", "LO", 0.91f),
        Student("s6", "Sofia Rossi", "CS-106", "sofia.r@school.edu", "class_cs", "SR", 1.0f),
        Student("s7", "Kwame Mensah", "CS-107", "kwame.m@school.edu", "class_cs", "KM", 0.93f),
        Student("s8", "Emma Watson", "CS-108", "emma.w@school.edu", "class_cs", "EW", 0.85f),
        Student("s9", "Dmitri Volkov", "CS-109", "dmitri.v@school.edu", "class_cs", "DV", 0.92f),
        Student("s10", "Aaliyah Patel", "CS-110", "aaliyah.p@school.edu", "class_cs", "AP", 0.97f)
    )

    val sampleCurricula = listOf(
        CurriculumModel(
            id = "cambridge",
            name = "Cambridge Assessment International Education (CAIE)",
            region = "International / UK",
            description = "Rigorous world-renowned syllabus structured for IGCSE, AS and A-Level mastery with strict marking schemes.",
            subjects = listOf("Computer Science (0478)", "Physics (9702)", "Pure Mathematics (9709)", "Chemistry (9701)", "Biology (9700)"),
            units = listOf(
                SyllabusUnit(
                    1,
                    "Data Representation & Number Systems",
                    "Binary, Hexadecimal, Two's Complement, Floating Point, and Character Encoding (ASCII, Unicode).",
                    12,
                    listOf("Convert between number bases effortlessly", "Explain sound & image sampling rates", "Analyze lossy vs lossless compression"),
                    "15% of Paper 1"
                ),
                SyllabusUnit(
                    2,
                    "Communication, Internet & Cryptography",
                    "Network topologies, packet switching, encryption protocols (Symmetric & Asymmetric), and Blockchain hashing.",
                    16,
                    listOf("Explain public/private key infrastructure", "Describe brute-force vs dictionary attacks", "Evaluate distributed ledger integrity"),
                    "20% of Paper 1"
                ),
                SyllabusUnit(
                    3,
                    "Hardware, Logic Gates & Von Neumann Architecture",
                    "CPU Fetch-Decode-Execute cycle, ALU, Registers, Logic circuits, and Truth tables.",
                    14,
                    listOf("Design compound logic circuits using NAND/NOR", "Trace bus architectures and interrupt handlers"),
                    "20% of Paper 1"
                ),
                SyllabusUnit(
                    4,
                    "Algorithms, Pseudocode & Program Design",
                    "Standard algorithms (Bubble Sort, Binary Search), state tables, and Big-O efficiency.",
                    22,
                    listOf("Write validated pseudocode", "Implement recursive traversal functions", "Analyze linear vs binary search bounds"),
                    "45% of Paper 2"
                )
            )
        ),
        CurriculumModel(
            id = "us_ap",
            name = "US Advanced Placement (AP) & Common Core",
            region = "United States / Global",
            description = "College Board accredited rigorous college-level curriculum emphasizing analytical reasoning, FRQ writing, and conceptual mastery.",
            subjects = listOf("AP Computer Science A", "AP Physics C: Mechanics", "AP Calculus BC", "AP Environmental Science"),
            units = listOf(
                SyllabusUnit(
                    1,
                    "Primitive Types & Objects",
                    "Java object orientation, memory references, immutable primitives, and method wrappers.",
                    10,
                    listOf("Instantiate classes and call accessor/mutator methods", "Calculate operator precedence"),
                    "12.5% of Exam"
                ),
                SyllabusUnit(
                    2,
                    "Boolean Expressions & Iterative Structures",
                    "De Morgan's laws, nested for/while loops, and invariant assertions.",
                    15,
                    listOf("Apply short-circuit evaluation rules", "Formulate loop termination proofs"),
                    "20% of Exam"
                ),
                SyllabusUnit(
                    3,
                    "Data Structures: 2D Arrays & ArrayLists",
                    "Dynamic collections, matrix transformations, and algorithmic traversals.",
                    20,
                    listOf("Implement row-major and column-major traversals", "Avoid ConcurrentModificationException"),
                    "25% of Exam"
                ),
                SyllabusUnit(
                    4,
                    "Inheritance, Polymorphism & Recursion",
                    "Superclasses, abstract interfaces, method overriding, dynamic dispatch, and binary search recursion.",
                    25,
                    listOf("Design deep class hierarchies", "Trace recursive call stacks"),
                    "30% of Exam"
                )
            )
        ),
        CurriculumModel(
            id = "ib_dp",
            name = "International Baccalaureate (IB Diploma Programme)",
            region = "Global (150+ countries)",
            description = "Holistic, inquiry-based curriculum centered on the IB Learner Profile, Theory of Knowledge (TOK), and internal assessments.",
            subjects = listOf("Computer Science HL", "Physics HL", "Mathematics: Analysis & Approaches HL"),
            units = listOf(
                SyllabusUnit(
                    1,
                    "System Fundamentals & Human Interaction",
                    "System lifecycle, usability, stakeholders, and ethical societal implications of automated intelligence.",
                    15,
                    listOf("Conduct stakeholder usability audits", "Evaluate moral dilemmas in algorithmic bias"),
                    "Core Topic 1"
                ),
                SyllabusUnit(
                    2,
                    "Abstract Data Structures (HL)",
                    "Linked lists, Stacks, Queues, Binary Search Trees, and custom Hash tables.",
                    30,
                    listOf("Construct dynamic pointer nodes in code", "Prove tree balance metrics and traversals"),
                    "HL Paper 2"
                ),
                SyllabusUnit(
                    3,
                    "Resource Management & Operating Systems",
                    "Virtual memory, paging, CPU scheduling algorithms, and deadlock conditions.",
                    20,
                    listOf("Simulate round-robin and shortest-job-first scheduling", "Formulate Banker's algorithm proofs"),
                    "Core Topic 6"
                )
            )
        ),
        CurriculumModel(
            id = "national_stem",
            name = "National STEM & Digital Technologies Standard",
            region = "National / Modernized",
            description = "Applied practical STEM standards connecting computational mathematics, engineering design, and blockchain verification.",
            subjects = listOf("Applied Cybersecurity & Web3", "Robotics & Microcontrollers", "Data Science & AI Literacy"),
            units = listOf(
                SyllabusUnit(
                    1,
                    "Distributed Consensus & Smart Contracts",
                    "Mechanisms of decentralized truth, blockchain block architecture, and state transitions.",
                    18,
                    listOf("Simulate cryptographic hashing & block verification", "Understand immutable data integrity"),
                    "Capstone Module"
                ),
                SyllabusUnit(
                    2,
                    "Applied Machine Learning & Neural Networks",
                    "Training pipelines, gradient descent, loss functions, and ethical prompt engineering.",
                    24,
                    listOf("Fine-tune model parameters", "Identify hallucinations and verification benchmarks"),
                    "Practical Lab"
                )
            )
        )
    )

    val sampleTextbooks = listOf(
        Textbook(
            id = "tb1",
            title = "Cambridge IGCSE Computer Science Master Edition",
            subject = "Computer Science",
            curriculum = "Cambridge Assessment",
            authors = "David Watson, Helen Williams & EduChain Press",
            edition = "4th International Edition (Updated 2026)",
            coverColor = 0xFF1E3A8A,
            chapters = listOf(
                TextbookChapter(
                    1,
                    "Data Transmission and Network Protocols",
                    "Explores simplex, half-duplex, and full-duplex communication channels, serial vs parallel transmission, packet structure (header, payload, trailer), and checksum calculation.",
                    listOf(
                        "Packet Header contains source/destination IP and sequence number",
                        "Parity checks and Cyclic Redundancy Checks (CRC) detect byte corruption",
                        "Transmission speed vs signal attenuation over twisted pair and fiber optics"
                    ),
                    listOf(
                        "Explain why asymmetric encryption is required during TLS handshake.",
                        "Calculate the parity bit for odd parity given the 7-bit payload: 1011001.",
                        "Diagram packet journey through three router hops."
                    )
                ),
                TextbookChapter(
                    2,
                    "Cryptographic Systems & Blockchain Ledgers",
                    "Introduces cryptographic hash functions (SHA-256), avalanche effect, public key cryptography, digital signatures, and Merkle tree verification in immutable distributed ledgers.",
                    listOf(
                        "Hash functions are deterministic, one-way, and collision-resistant",
                        "Digital signatures ensure authenticity, non-repudiation, and message integrity",
                        "Merkle trees enable O(log n) verification of transactions within sealed blocks"
                    ),
                    listOf(
                        "Why does modifying a single transaction in block #4 invalidate block #5's header?",
                        "Describe how an educator's private key seals student gradebooks.",
                        "Compare SHA-256 with MD5 in terms of collision vulnerability."
                    )
                ),
                TextbookChapter(
                    3,
                    "Automated Systems, Sensors & Actuators",
                    "Covers analog-to-digital converters (ADC), feedback control loops, microprocessors, and smart industrial automation.",
                    listOf(
                        "Sensors measure physical quantities; actuators create mechanical action",
                        "Closed-loop systems incorporate feedback to adjust output toward a setpoint",
                        "Microcontrollers execute control firmware in embedded loops"
                    ),
                    listOf(
                        "Design an automated greenhouse climate regulation loop.",
                        "Differentiate between open-loop and closed-loop control systems with diagrams."
                    )
                )
            )
        ),
        Textbook(
            id = "tb2",
            title = "University Physics with Modern Physics",
            subject = "Physics",
            curriculum = "US AP / Cambridge A-Level",
            authors = "Hugh D. Young, Roger A. Freedman",
            edition = "15th Global Edition",
            coverColor = 0xFF0D9488,
            chapters = listOf(
                TextbookChapter(
                    1,
                    "Newtonian Mechanics & Momentum Conservation",
                    "Comprehensive treatment of vector kinematics, Newton's laws of motion, elastic vs inelastic collisions, and impulse-momentum theorem.",
                    listOf(
                        "Internal forces within an isolated system sum to zero; total momentum is strictly conserved",
                        "Kinetic energy is conserved only in perfectly elastic collisions",
                        "Impulse J = Integral(F dt) = Delta p"
                    ),
                    listOf(
                        "A 0.4 kg soccer ball traveling at 25 m/s is kicked back at 20 m/s. If contact time is 0.05s, find average force.",
                        "Derive the recoil velocity of an artillery cannon using conservation of linear momentum."
                    )
                ),
                TextbookChapter(
                    2,
                    "Electromagnetic Induction and Faraday's Law",
                    "Magnetic flux, Faraday's law of induction, Lenz's law, eddy currents, and AC generator mechanics.",
                    listOf(
                        "Induced electromotive force (EMF) equals the negative rate of change of magnetic flux",
                        "Lenz's law dictates that the induced current creates a field opposing the flux change",
                        "Mutual and self-inductance in solenoid circuits"
                    ),
                    listOf(
                        "State Lenz's law and explain how it upholds the conservation of energy.",
                        "A circular coil of 50 turns and radius 4 cm is in a magnetic field changing at 0.2 T/s. Calculate induced EMF."
                    )
                )
            )
        ),
        Textbook(
            id = "tb3",
            title = "Discrete Mathematics & Cryptographic Foundations",
            subject = "Mathematics",
            curriculum = "Higher Level / College",
            authors = "Kenneth H. Rosen & Academic Guild",
            edition = "8th Edition",
            coverColor = 0xFF7C3AED,
            chapters = listOf(
                TextbookChapter(
                    1,
                    "Modular Arithmetic & Prime Number Theory",
                    "Congruence relations, Euclidean Algorithm for Greatest Common Divisor (GCD), Fermat's Little Theorem, and Chinese Remainder Theorem.",
                    listOf(
                        "a = b (mod m) if and only if m divides (a - b)",
                        "Fermat's Little Theorem: If p is prime and gcd(a, p) = 1, then a^(p-1) = 1 (mod p)",
                        "Extended Euclidean Algorithm yields modular inverses essential for RSA"
                    ),
                    listOf(
                        "Solve the linear congruence 7x = 3 (mod 11).",
                        "Use Fermat's Little Theorem to evaluate 3^102 (mod 101)."
                    )
                )
            )
        )
    )

    val sampleAssessments = listOf(
        Assessment(
            id = "as_1",
            title = "Paper 1 Midterm: Network Protocols & Hashing",
            subject = "Computer Science",
            gradeLevel = "Grade 11 / AS-Level",
            type = AssessmentType.EXAM,
            curriculum = "Cambridge CAIE",
            questions = listOf(
                Question(
                    "q1",
                    "Explain the difference between packet switching and circuit switching. List two advantages of packet switching.",
                    "Structured Essay",
                    emptyList(),
                    "Packet switching breaks data into packets routed independently over dynamic routes, maximizing bandwidth efficiency. Circuit switching dedicates a fixed channel. Advantages: more resilient to node failures and higher line utilization.",
                    6,
                    "3 marks for comparison; 3 marks for 2 valid advantages with explanations."
                ),
                Question(
                    "q2",
                    "Which protocol provides secure cryptographic encapsulation of web traffic?",
                    "Multiple Choice",
                    listOf("HTTP", "FTP", "HTTPS/TLS", "SMTP"),
                    "HTTPS/TLS",
                    2,
                    "2 marks for correct choice."
                ),
                Question(
                    "q3",
                    "Describe how a cryptographic hash function guarantees data integrity in student examination records.",
                    "Short Answer",
                    emptyList(),
                    "A cryptographic hash (like SHA-256) maps examination data to a unique fixed-length digest. Any post-submission modification radically alters the hash digest, triggering an immediate integrity mismatch.",
                    4,
                    "2 marks for hashing mechanics; 2 marks for tamper-evidence property."
                )
            ),
            totalMarks = 12,
            durationMinutes = 60,
            dueDate = "Oct 28, 2026",
            blockchainHash = "0x7a89b3f021c32491a92e",
            isSealedOnChain = true
        ),
        Assessment(
            id = "as_2",
            title = "Quiz 3: Asymmetric Cryptography & Merkle Trees",
            subject = "Computer Science",
            gradeLevel = "Grade 11",
            type = AssessmentType.QUIZ,
            curriculum = "Cambridge CAIE",
            questions = listOf(
                Question(
                    "q1",
                    "State the role of the public key and private key when creating a digital signature.",
                    "Short Answer",
                    emptyList(),
                    "The sender hashes the message and encrypts the hash with their private key to create the signature. Anyone can verify the signature using the sender's public key.",
                    4,
                    "2 marks private key signing; 2 marks public key verification."
                )
            ),
            totalMarks = 4,
            durationMinutes = 20,
            dueDate = "Tomorrow",
            blockchainHash = null,
            isSealedOnChain = false
        )
    )

    val sampleGrades = listOf(
        GradeEntry("g1", "s1", "Alex Rivera", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 11f, 12f, "Outstanding analytical breakdown and clear diagrams.", "Oct 20", "0xBLOCK_003_HASH", true),
        GradeEntry("g2", "s2", "Priya Sharma", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 12f, 12f, "Flawless score! Perfectly articulated cryptographic properties.", "Oct 20", "0xBLOCK_003_HASH", true),
        GradeEntry("g3", "s3", "Marcus Chen", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 8.5f, 12f, "Good effort. Review packet switching routing metrics.", "Oct 20", "0xBLOCK_003_HASH", true),
        GradeEntry("g4", "s4", "Zoe Al-Mansoor", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 10f, 12f, "Thorough explanations of encryption protocols.", "Oct 20", "0xBLOCK_003_HASH", true),
        GradeEntry("g5", "s5", "Liam O'Connor", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 9f, 12f, "Well structured. Mind the distinction between symmetric and asymmetric keys.", "Oct 20", "0xBLOCK_003_HASH", true),
        GradeEntry("g6", "s6", "Sofia Rossi", "as_1", "Midterm: Network Protocols", AssessmentType.EXAM, 12f, 12f, "Exceptional work. Exam-board quality presentation.", "Oct 20", "0xBLOCK_003_HASH", true)
    )

    val sampleScriptsToMark = listOf(
        ScriptSubmission(
            id = "sub_1",
            studentId = "s1",
            studentName = "Alex Rivera",
            assessmentTitle = "Paper 1 Midterm: Question 1",
            questionPrompt = "Explain the difference between packet switching and circuit switching. List two advantages of packet switching.",
            studentAnswer = "Packet switching divides messages into small packets that travel independently through various network nodes before reassembling at the destination. Circuit switching creates a dedicated physical path for the entire duration of the connection (like traditional phone lines). Two advantages of packet switching are: 1) Much higher efficiency because channels are shared when idle; 2) Robust fault tolerance, as packets can automatically take alternative paths if a node fails.",
            maxMarks = 6,
            scoreAwarded = null,
            aiEvaluationFeedback = null,
            status = "PENDING"
        ),
        ScriptSubmission(
            id = "sub_2",
            studentId = "s3",
            studentName = "Marcus Chen",
            assessmentTitle = "Paper 1 Midterm: Question 3",
            questionPrompt = "Describe how a cryptographic hash function guarantees data integrity in student examination records.",
            studentAnswer = "A hash function takes student records and turns them into a fixed code. If someone tries to change the student's grade or answers later on, the hash will change completely and everyone will know it was altered. This makes it impossible to tamper with secretly.",
            maxMarks = 4,
            scoreAwarded = null,
            aiEvaluationFeedback = null,
            status = "PENDING"
        ),
        ScriptSubmission(
            id = "sub_3",
            studentId = "s5",
            studentName = "Liam O'Connor",
            assessmentTitle = "Quiz 3: Question 1",
            questionPrompt = "State the role of the public key and private key when creating a digital signature.",
            studentAnswer = "The signer encrypts the hash of the document with their secret private key so nobody else could have made it. The public key is then shared with everyone so anyone can decrypt it and check if it matches the document hash, proving the teacher really signed it.",
            maxMarks = 4,
            scoreAwarded = 4,
            aiEvaluationFeedback = "Clear and accurate explanation of asymmetric signature generation and verification. Appropriate use of cryptographic terminology.",
            rubricBreakdown = listOf(
                "Private Key role: Full marks (2/2)",
                "Public Key verification: Full marks (2/2)",
                "Clarity & Vocabulary: Proficient"
            ),
            status = "MARKED",
            blockHash = "0x89ab12cd76fe"
        )
    )

    val sampleLessonNotes = listOf(
        LessonNote(
            id = "note_1",
            topic = "Asymmetric Encryption & Digital Signatures",
            subject = "Computer Science",
            gradeLevel = "Grade 11 / A-Level",
            curriculum = "Cambridge CAIE (0478/9618)",
            duration = "50 mins",
            learningObjectives = listOf(
                "Understand the mathematical principles underpinning public-private key pairs",
                "Explain the end-to-end workflow of digital signature generation and verification",
                "Assess the role of cryptographic signatures in preventing academic certificate fraud"
            ),
            keyTerms = listOf("Public Key", "Private Key", "SHA-256 Digest", "Non-repudiation", "Certificate Authority"),
            teachingSteps = listOf(
                LessonStep("Hook & Thought Experiment", 8, "Present a scenario where a student claims an email from the school was forged. Ask class: How can we mathematically prove authorship?", "Gauge student familiarity with passwords vs mathematical signatures."),
                LessonStep("Direct Instruction", 17, "Deconstruct asymmetric key generation. Walk through the one-way trapdoor function concept and demonstrate live CLI hash verification.", "Ensure students do not confuse encryption for privacy with encryption for signature verification."),
                LessonStep("Pair Code / Decryption Lab", 15, "Students use paper-and-pencil mini-RSA or key simulator to sign and exchange verifiable challenge tokens.", "Circulate to check that students use the sender's private key to sign, not public key."),
                LessonStep("Exit Diagnostic Ticket", 10, "Students answer a 3-question formative check on paper or mobile app.", "Timestamp responses into EduChain ledger.")
            ),
            homeworkAssignment = "Read Chapter 2 pages 45-52 in Cambridge CS Textbook. Complete review problems 1 through 5.",
            blockchainHash = "0x98fbc123000aa11",
            createdAt = "Yesterday, 09:30 AM"
        )
    )

    val subscriptionTiers = listOf(
        SubscriptionPlan(
            type = TierType.FREE_TRIAL,
            name = "Educator Free Trial",
            priceMonthly = 0.0,
            priceAnnualMonthly = 0.0,
            badge = "12 Days Left",
            description = "Complete trial access for teachers evaluating AI-assisted lesson planning and blockchain verification.",
            maxClasses = "1 Active Class",
            aiQuotaPerDay = "25 AI Prompts / Day",
            blockchainSeals = "5 Blockchain Seals / Month",
            features = listOf(
                "Full AI Lesson Note & Quiz Generator",
                "Interactive Roll Call Attendance",
                "Automated Script Marking Assist",
                "Access to Cambridge, US AP & IB Syllabi",
                "EduChain Proof-of-Academic-Integrity explorer",
                "14-Day Free Evaluation Period"
            ),
            trialDaysRemaining = 12
        ),
        SubscriptionPlan(
            type = TierType.EDUCATOR_STARTER,
            name = "Educator Starter",
            priceMonthly = 9.99,
            priceAnnualMonthly = 7.99,
            badge = "Essential",
            description = "Ideal for individual subject teachers managing several classes and daily roll call.",
            maxClasses = "Up to 5 Classes",
            aiQuotaPerDay = "100 AI Prompts / Day",
            blockchainSeals = "50 Blockchain Seals / Month",
            features = listOf(
                "Everything in Free Trial",
                "Up to 5 Classes & 200 Students",
                "Export Gradebook to CSV & Excel",
                "Custom Rubric Builder",
                "Standard Email & Helpdesk Support"
            )
        ),
        SubscriptionPlan(
            type = TierType.EDUCATOR_PRO,
            name = "Educator Pro",
            priceMonthly = 19.99,
            priceAnnualMonthly = 15.99,
            badge = "Most Popular",
            description = "The gold standard for dedicated educators seeking unlimited AI power and tamper-proof student records.",
            maxClasses = "Unlimited Classes",
            aiQuotaPerDay = "Unlimited Gemini AI",
            blockchainSeals = "Unlimited On-Chain Seals",
            features = listOf(
                "Everything in Educator Starter",
                "Unlimited Classes & Students",
                "Unlimited Gemini 3.5 Flash AI Generations",
                "Bulk Exam Script Optical & AI Grading",
                "Cryptographic Blockchain Student Diplomas & Seals",
                "All International Digital Textbooks & Syllabi",
                "Priority 24/7 AI Processing"
            )
        ),
        SubscriptionPlan(
            type = TierType.SCHOOL_INSTITUTION,
            name = "School & Institution",
            priceMonthly = 89.99,
            priceAnnualMonthly = 69.99,
            badge = "For Campus",
            description = "Designed for schools, academies, and university departments requiring unified academic integrity ledgers.",
            maxClasses = "Campus-wide",
            aiQuotaPerDay = "Unlimited High-Capacity",
            blockchainSeals = "Private School Consortium Node",
            features = listOf(
                "Everything in Educator Pro",
                "School-wide Teacher & Department Accounts",
                "Dedicated Private EduChain Consensus Node",
                "Student & Parent Grade Verification Portal",
                "Student Information System (SIS) API integration",
                "FERPA & GDPR Compliant Data Encryption",
                "Dedicated Onboarding Specialist & Training"
            )
        )
    )
}
