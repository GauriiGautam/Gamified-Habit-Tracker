DROP DATABASE IF EXISTS HabitTracker;
CREATE DATABASE HabitTracker;
USE HabitTracker;

CREATE TABLE CATEGORY (
    CategoryID   INT PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL,
    Description  VARCHAR(255),
    IconName     VARCHAR(100)
);

CREATE TABLE LEVEL (
    LevelID      INT PRIMARY KEY,
    LevelNumber  INT NOT NULL,
    LevelName    VARCHAR(100),
    XPRequired   INT NOT NULL
);

CREATE TABLE USER (
    UserID           INT PRIMARY KEY,
    Username         VARCHAR(50)  NOT NULL,
    Email            VARCHAR(255) NOT NULL,
    Password         VARCHAR(255) NOT NULL,
    FullName         VARCHAR(200),
    Bio              VARCHAR(500),
    CurrentLevel     INT,
    TotalXP          INT DEFAULT 0,
    RegistrationDate DATE,
    LastLoginDate    DATE,
    IsActive         INT DEFAULT 1,
    FOREIGN KEY (CurrentLevel) REFERENCES LEVEL(LevelID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE BADGE (
    BadgeID       INT PRIMARY KEY,
    BadgeName     VARCHAR(150) NOT NULL,
    Description   VARCHAR(255),
    BadgeType     VARCHAR(50),
    BadgeCategory VARCHAR(100),
    CriteriaValue INT,
    XPReward      INT DEFAULT 0,
    RarityLevel   VARCHAR(50),
    IsActive      INT DEFAULT 1
);

CREATE TABLE REWARD (
    RewardID    INT PRIMARY KEY,
    RewardName  VARCHAR(150) NOT NULL,
    Description VARCHAR(255),
    RewardType  VARCHAR(50),
    UnlockLevel INT,
    IsActive    INT DEFAULT 1,
    FOREIGN KEY (UnlockLevel) REFERENCES LEVEL(LevelID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE USER_BADGE (
    UserBadgeID  INT PRIMARY KEY,
    UserID       INT NOT NULL,
    BadgeID      INT NOT NULL,
    EarnedDate   DATE,
    DisplayOrder INT DEFAULT 0,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (BadgeID) REFERENCES BADGE(BadgeID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE USER_REWARD (
    UserRewardID INT PRIMARY KEY,
    UserID       INT NOT NULL,
    RewardID     INT NOT NULL,
    EarnedDate   DATE,
    IsEquipped   INT DEFAULT 0,
    FOREIGN KEY (UserID)   REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (RewardID) REFERENCES REWARD(RewardID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HABIT (
    HabitID          INT PRIMARY KEY,
    UserID           INT NOT NULL,
    CategoryID       INT NOT NULL,
    HabitName        VARCHAR(200) NOT NULL,
    Description      VARCHAR(500),
    Frequency        VARCHAR(50),
    TargetCount      INT DEFAULT 1,
    DifficultyLevel  INT DEFAULT 1,
    XPValue          INT DEFAULT 10,
    CurrentStreak    INT DEFAULT 0,
    LongestStreak    INT DEFAULT 0,
    TotalCompletions INT DEFAULT 0,
    IsActive         VARCHAR(20) DEFAULT 'Active',
    CreatedDate      DATE,
    ReminderTime     TIME,
    FOREIGN KEY (UserID)     REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES CATEGORY(CategoryID) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO CATEGORY VALUES
(1, 'Health',       'Physical health habits',    'heart'),
(2, 'Productivity', 'Work and focus habits',     'briefcase'),
(3, 'Learning',     'Educational habits',        'book'),
(4, 'Fitness',      'Exercise and movement',     'dumbbell'),
(5, 'Mindfulness',  'Mental wellbeing practices','brain');

INSERT INTO LEVEL VALUES
(1, 1, 'Beginner',  0),
(2, 2, 'Explorer',  100),
(3, 3, 'Achiever',  250),
(4, 4, 'Champion',  500),
(5, 5, 'Legend',    1000);

INSERT INTO USER VALUES
(1, 'john_doe',   'john@example.com', 'pwd1', 'John Doe',   'Building better habits', 3, 1650, '2024-01-10', '2024-11-19', 1),
(2, 'jane_smith', 'jane@example.com', 'pwd2', 'Jane Smith', 'Fitness enthusiast',     4, 4200, '2024-02-15', '2024-11-19', 1),
(3, 'ravi_k',     'ravi@example.com', 'pwd3', 'Ravi Kumar', 'Learning consistency',   2,  820, '2024-03-20', '2024-11-18', 1);

INSERT INTO BADGE VALUES
(1, '7-Day Warrior',   'Complete a habit 7 days in a row',          'Streak',     'Consistency', 7,   50,  'Common',    1),
(2, '30-Day Champion', 'Maintain a 30-day streak',                  'Streak',     'Consistency', 30,  200, 'Rare',      1),
(3, 'Century Club',    'Log 100 completions for any habit',         'Completion', 'Milestone',   100, 300, 'Epic',      1),
(4, 'Perfect Week',    'Complete all habits for a full week',       'Milestone',  'Performance', 7,   100, 'Rare',      1),
(5, 'AI Oracle',       'Followed AI prediction correctly 10 times', 'AI_Powered', 'AI',          10,  150, 'Legendary', 1);

INSERT INTO REWARD VALUES
(1, 'Dark Theme',        'Unlock dark mode UI',              'Customization', 2, 1),
(2, 'Custom Categories', 'Create your own habit categories', 'FeatureUnlock', 3, 1),
(3, 'PDF Export',        'Export reports as PDF',            'FeatureUnlock', 4, 1),
(4, 'Avatar Pack',       'Unlock premium avatar collection', 'VirtualItem',   3, 1);

INSERT INTO USER_BADGE VALUES
(1, 1, 1, '2024-11-15', 1),
(2, 1, 4, '2024-11-18', 2),
(3, 2, 1, '2024-10-05', 1),
(4, 2, 2, '2024-11-01', 2),
(5, 2, 3, '2024-11-20', 3);

INSERT INTO USER_REWARD VALUES
(1, 1, 1, '2024-11-10', 1),
(2, 1, 2, '2024-11-16', 0),
(3, 2, 1, '2024-10-15', 0),
(4, 2, 2, '2024-11-02', 1),
(5, 2, 3, '2024-11-21', 0);

INSERT INTO HABIT VALUES
(1, 1, 4, 'Morning Run',     '30-minute morning jog',         'daily', 1, 2, 20, 12, 18,  45,  'Active', '2024-01-15', '06:30:00'),
(2, 1, 5, 'Meditation',      '10 minutes of mindfulness',     'daily', 1, 1, 10,  5, 12,  38,  'Active', '2024-01-15', '07:00:00'),
(3, 2, 2, 'Deep Work Block', '2 hours distraction-free work', 'daily', 1, 3, 30, 22, 30,  85,  'Active', '2024-02-20', '09:00:00'),
(4, 2, 3, 'Read 20 Pages',   'Read non-fiction books daily',  'daily', 1, 1, 10,  8, 22, 102,  'Active', '2024-02-20', '21:00:00'),
(5, 3, 1, 'Drink 2L Water',  'Stay hydrated throughout day',  'daily', 1, 1, 10,  3,  7,  22,  'Active', '2024-03-25', '08:00:00');

CREATE TABLE GOAL (
    GoalID            INT PRIMARY KEY,
    HabitID           INT NOT NULL,
    UserID            INT NOT NULL,
    GoalType          VARCHAR(50),
    TargetCompletions INT DEFAULT 1,
    StartDate         DATE,
    EndDate           DATE,
    Status            VARCHAR(20) DEFAULT 'InProgress',
    CompletedCount    INT DEFAULT 0,
    CreatedDate       DATE,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HABIT_LOG (
    LogID          INT PRIMARY KEY,
    HabitID        INT NOT NULL,
    UserID         INT NOT NULL,
    CompletionDate DATE,
    CompletionTime TIME,
    Notes          VARCHAR(300),
    XPAwarded      INT DEFAULT 0,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MOOD_ENTRY (
    MoodID    INT PRIMARY KEY,
    UserID    INT NOT NULL,
    MoodDate  DATE,
    MoodScore INT,
    MoodType  VARCHAR(50),
    Notes     VARCHAR(300),
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE STREAK_INSURANCE (
    InsuranceID  INT PRIMARY KEY,
    UserID       INT NOT NULL,
    HabitID      INT NOT NULL,
    TotalTokens  INT DEFAULT 0,
    UsedTokens   INT DEFAULT 0,
    LastUsedDate DATE,
    ExpiryDate   DATE,
    EarnedSource VARCHAR(100),
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE NOTIFICATION (
    NotificationID   INT PRIMARY KEY,
    UserID           INT NOT NULL,
    HabitID          INT,
    NotificationType VARCHAR(50),
    Message          VARCHAR(300),
    ScheduledTime    TIME,
    SentDate         DATE,
    IsRead           INT DEFAULT 0,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE LEADERBOARD (
    LeaderboardID   INT PRIMARY KEY,
    UserID          INT NOT NULL,
    PeriodType      VARCHAR(20),
    PeriodStart     DATE,
    PeriodEnd       DATE,
    TotalXP         INT DEFAULT 0,
    RankPosition    INT,
    HabitsCompleted INT DEFAULT 0,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HABIT_SHARING (
    ShareID        INT PRIMARY KEY,
    HabitID        INT NOT NULL,
    SharedByUserID INT NOT NULL,
    SharedToUserID INT NOT NULL,
    SharedDate     DATE,
    Message        VARCHAR(300),
    IsAccepted     INT DEFAULT 0,
    FOREIGN KEY (HabitID)        REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SharedByUserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SharedToUserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE USER_SESSION (
    SessionID  INT PRIMARY KEY,
    UserID     INT NOT NULL,
    LoginTime  DATETIME,
    LogoutTime DATETIME,
    IPAddress  VARCHAR(50),
    DeviceType VARCHAR(50),
    IsActive   INT DEFAULT 1,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE CHALLENGE (
    ChallengeID   INT PRIMARY KEY,
    ChallengeName VARCHAR(200) NOT NULL,
    Description   VARCHAR(400),
    CategoryID    INT,
    StartDate     DATE,
    EndDate       DATE,
    TargetCount   INT DEFAULT 1,
    XPReward      INT DEFAULT 50,
    IsActive      INT DEFAULT 1,
    FOREIGN KEY (CategoryID) REFERENCES CATEGORY(CategoryID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE USER_CHALLENGE (
    UserChallengeID INT PRIMARY KEY,
    UserID          INT NOT NULL,
    ChallengeID     INT NOT NULL,
    JoinedDate      DATE,
    CurrentProgress INT DEFAULT 0,
    Status          VARCHAR(20) DEFAULT 'Active',
    CompletedDate   DATE,
    XPEarned        INT DEFAULT 0,
    FOREIGN KEY (UserID)      REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ChallengeID) REFERENCES CHALLENGE(ChallengeID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ADMIN_LOG (
    LogID       INT PRIMARY KEY,
    ActionType  VARCHAR(100),
    TargetTable VARCHAR(100),
    TargetID    INT,
    OldValue    VARCHAR(500),
    NewValue    VARCHAR(500),
    ActionDate  DATETIME,
    Remarks     VARCHAR(300)
);

CREATE TABLE HABIT_DNA (
    DNAID                  INT PRIMARY KEY,
    UserID                 INT NOT NULL,
    PreferredTimeOfDay     VARCHAR(20),
    ConsistencyScore       INT DEFAULT 0,
    HabitStrengthScore     INT DEFAULT 0,
    TotalHabitsTracked     INT DEFAULT 0,
    LastCalculated         DATE,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HABIT_PREDICTION (
    PredictionID        INT PRIMARY KEY,
    HabitID             INT NOT NULL,
    UserID              INT NOT NULL,
    PredictionDate      DATE,
    SuccessProbability  INT DEFAULT 0,
    ActualOutcome       VARCHAR(20),
    ConfidenceScore     INT DEFAULT 0,
    ModelVersion        VARCHAR(20),
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE BEHAVIORAL_ANOMALY (
    AnomalyID            INT PRIMARY KEY,
    UserID               INT NOT NULL,
    DetectionDate        DATE,
    AnomalyScore         INT DEFAULT 0,
    AnomalyType          VARCHAR(50),
    SeverityLevel        VARCHAR(20),
    InterventionTriggered INT DEFAULT 0,
    InterventionType     VARCHAR(50),
    ResolutionDate       DATE,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ADAPTIVE_DIFFICULTY_LOG (
    LogID              INT PRIMARY KEY,
    HabitID            INT NOT NULL,
    UserID             INT NOT NULL,
    PreviousDifficulty INT,
    NewDifficulty      INT,
    AdjustmentReason   VARCHAR(300),
    AdjustmentDate     DATE,
    UserAccepted       INT DEFAULT 0,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE FRIEND (
    FriendID       INT PRIMARY KEY,
    UserID         INT NOT NULL,
    FriendUserID   INT NOT NULL,
    Status         VARCHAR(20) DEFAULT 'Pending',
    ConnectedDate  DATE,
    FOREIGN KEY (UserID)       REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (FriendUserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE XP_TRANSACTION (
    TransactionID  INT PRIMARY KEY,
    UserID         INT NOT NULL,
    HabitID        INT,
    XPAmount       INT DEFAULT 0,
    Source         VARCHAR(100),
    TransactionDate DATE,
    FOREIGN KEY (UserID)  REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (HabitID) REFERENCES HABIT(HabitID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE REPORT (
    ReportID     INT PRIMARY KEY,
    UserID       INT NOT NULL,
    ReportType   VARCHAR(50),
    PeriodStart  DATE,
    PeriodEnd    DATE,
    TotalXP      INT DEFAULT 0,
    HabitsLogged INT DEFAULT 0,
    GoalsMet     INT DEFAULT 0,
    GeneratedOn  DATE,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE FEEDBACK (
    FeedbackID   INT PRIMARY KEY,
    UserID       INT NOT NULL,
    FeedbackType VARCHAR(50),
    Message      VARCHAR(500),
    Rating       INT,
    SubmittedOn  DATE,
    IsReviewed   INT DEFAULT 0,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE SUBSCRIPTION (
    SubscriptionID INT PRIMARY KEY,
    UserID         INT NOT NULL,
    PlanType       VARCHAR(50),
    StartDate      DATE,
    EndDate        DATE,
    IsActive       INT DEFAULT 1,
    AmountPaid     DECIMAL(8,2) DEFAULT 0.00,
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MODIFICATION_REQUEST (
    RequestID     INT PRIMARY KEY,
    UserID        INT NOT NULL,
    RequestDate   DATE,
    RequestStatus VARCHAR(50) DEFAULT 'Pending',
    Description   VARCHAR(500),
    FOREIGN KEY (UserID) REFERENCES USER(UserID) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO GOAL VALUES
(1, 1, 1, 'monthly', 20, '2024-11-01', '2024-11-30', 'InProgress', 12, '2024-11-01'),
(2, 2, 1, 'weekly',   5, '2024-11-11', '2024-11-17', 'Completed',   5, '2024-11-11'),
(3, 3, 2, 'monthly', 25, '2024-11-01', '2024-11-30', 'InProgress', 22, '2024-11-01'),
(4, 4, 2, 'weekly',   7, '2024-11-11', '2024-11-17', 'Completed',   7, '2024-11-11'),
(5, 5, 3, 'monthly', 15, '2024-11-01', '2024-11-30', 'InProgress',  3, '2024-11-01');

INSERT INTO HABIT_LOG VALUES
(1, 1, 1, '2024-11-19', '06:35:00', 'Felt great today',    20),
(2, 2, 1, '2024-11-19', '07:05:00', 'Very calm session',   10),
(3, 1, 1, '2024-11-18', '06:40:00', NULL,                  20),
(4, 3, 2, '2024-11-19', '09:15:00', 'Deep focus achieved', 30),
(5, 4, 2, '2024-11-19', '21:10:00', 'Finished chapter 3',  10),
(6, 5, 3, '2024-11-18', '09:00:00', NULL,                  10);

INSERT INTO MOOD_ENTRY VALUES
(1, 1, '2024-11-19', 4, 'Happy',    'Productive morning run'),
(2, 1, '2024-11-18', 3, 'Neutral',  NULL),
(3, 2, '2024-11-19', 5, 'Happy',    'Crushed my deep work session'),
(4, 2, '2024-11-18', 4, 'Happy',    'Good reading session'),
(5, 3, '2024-11-18', 2, 'Stressed', 'Forgot to drink water');

INSERT INTO STREAK_INSURANCE VALUES
(1, 1, 1, 2, 0, NULL,         '2025-02-15', '30-day streak reward'),
(2, 1, 2, 1, 0, NULL,         '2025-02-15', 'Perfect week reward'),
(3, 2, 3, 3, 1, '2024-11-10', '2025-02-20', '30-day streak reward'),
(4, 2, 4, 2, 0, NULL,         '2025-02-20', 'Level milestone'),
(5, 3, 5, 1, 0, NULL,         '2025-03-25', 'Weekly goal bonus');

INSERT INTO NOTIFICATION VALUES
(1, 1, 1,    'Reminder',    'Time for your morning run!',             '06:25:00', '2024-11-19', 1),
(2, 1, NULL, 'BadgeUnlock', 'You earned the Perfect Week badge!',     NULL,       '2024-11-18', 1),
(3, 2, 3,    'Reminder',    'Your deep work block starts in 5 min.',  '08:55:00', '2024-11-19', 1),
(4, 2, NULL, 'LevelUp',     'Congrats! You reached Level 4 Champion.',NULL,       '2024-11-01', 1),
(5, 3, 5,    'StreakWarn',  'Log your water habit to keep streak!',   '07:50:00', '2024-11-18', 0);

INSERT INTO LEADERBOARD VALUES
(1, 2, 'weekly',  '2024-11-11', '2024-11-17', 420, 1, 35),
(2, 1, 'weekly',  '2024-11-11', '2024-11-17', 310, 2, 22),
(3, 3, 'weekly',  '2024-11-11', '2024-11-17', 120, 3, 10),
(4, 2, 'monthly', '2024-11-01', '2024-11-30', 980, 1, 85),
(5, 1, 'monthly', '2024-11-01', '2024-11-30', 740, 2, 60);

INSERT INTO HABIT_SHARING VALUES
(1, 1, 1, 2, '2024-11-10', 'Try morning runs, changed my life!', 1),
(2, 3, 2, 1, '2024-11-12', 'Deep work is worth it, join me!',    1),
(3, 4, 2, 3, '2024-11-14', 'Read 20 pages daily with me.',       0),
(4, 2, 1, 3, '2024-11-15', 'Meditation helps a lot.',            0),
(5, 5, 3, 1, '2024-11-16', 'Staying hydrated together!',         1);

INSERT INTO USER_SESSION VALUES
(1, 1, '2024-11-19 06:00:00', '2024-11-19 08:00:00', '192.168.1.10', 'mobile',  0),
(2, 2, '2024-11-19 08:45:00', NULL,                  '192.168.1.22', 'desktop', 1),
(3, 3, '2024-11-18 07:30:00', '2024-11-18 08:15:00', '192.168.1.35', 'mobile',  0),
(4, 1, '2024-11-18 20:00:00', '2024-11-18 21:00:00', '192.168.1.10', 'desktop', 0),
(5, 2, '2024-11-17 09:00:00', '2024-11-17 11:30:00', '192.168.1.22', 'mobile',  0);

INSERT INTO CHALLENGE VALUES
(1, '30-Day Fitness Blitz',   'Complete a fitness habit every day for 30 days', 4, '2024-11-01', '2024-11-30', 30, 300, 1),
(2, '7-Day Mindfulness Week', 'Meditate daily for one full week',               5, '2024-11-11', '2024-11-17',  7, 100, 1),
(3, '2-Week Reading Sprint',  'Read 20 pages every day for 14 days',            3, '2024-11-04', '2024-11-17', 14, 150, 1),
(4, 'Hydration Hero',         'Drink 2L of water daily for 10 days',            1, '2024-11-10', '2024-11-19', 10,  80, 1),
(5, 'Deep Work November',     'Log a deep work block every weekday in Nov',     2, '2024-11-01', '2024-11-29', 20, 250, 1);

INSERT INTO USER_CHALLENGE VALUES
(1, 1, 1, '2024-11-01', 18, 'Active',    NULL,         0),
(2, 2, 1, '2024-11-01', 30, 'Completed', '2024-11-30', 300),
(3, 1, 2, '2024-11-11',  7, 'Completed', '2024-11-17', 100),
(4, 2, 3, '2024-11-04', 14, 'Completed', '2024-11-17', 150),
(5, 3, 4, '2024-11-10',  3, 'Active',    NULL,           0);

INSERT INTO ADMIN_LOG VALUES
(1, 'UPDATE', 'BADGE',    1, 'XPReward=40', 'XPReward=50', '2024-11-01 10:00:00', 'Increased XP for 7-Day Warrior'),
(2, 'INSERT', 'CATEGORY', 5, NULL,          'Mindfulness', '2024-11-02 11:30:00', 'Added Mindfulness category'),
(3, 'UPDATE', 'USER',     3, 'IsActive=0',  'IsActive=1',  '2024-11-05 09:15:00', 'Reactivated suspended account'),
(4, 'DELETE', 'HABIT',    0, 'HabitID=9',   NULL,          '2024-11-10 14:00:00', 'Removed flagged habit'),
(5, 'UPDATE', 'REWARD',   3, 'IsActive=0',  'IsActive=1',  '2024-11-15 16:45:00', 'Re-enabled PDF Export reward');

INSERT INTO HABIT_DNA VALUES
(1, 1, 'Morning', 82, 75, 2, '2024-11-17'),
(2, 2, 'Morning', 91, 88, 2, '2024-11-17'),
(3, 3, 'Morning', 45, 40, 1, '2024-11-17');

INSERT INTO HABIT_PREDICTION VALUES
(1, 1, 1, '2024-11-19', 87, 'Completed', 90, 'v1.0'),
(2, 2, 1, '2024-11-19', 72, 'Completed', 80, 'v1.0'),
(3, 3, 2, '2024-11-19', 95, 'Completed', 92, 'v1.0'),
(4, 4, 2, '2024-11-19', 88, 'Completed', 85, 'v1.0'),
(5, 5, 3, '2024-11-18', 41, 'Missed',    60, 'v1.0');

INSERT INTO BEHAVIORAL_ANOMALY VALUES
(1, 3, '2024-11-18', 72, 'Sudden_Drop', 'High', 1, 'Goal_Adjustment', NULL),
(2, 1, '2024-11-05', 35, 'Pattern_Change', 'Low', 0, NULL, '2024-11-08'),
(3, 2, '2024-10-20', 20, 'Erratic_Behavior', 'Low', 0, NULL, '2024-10-23');

INSERT INTO ADAPTIVE_DIFFICULTY_LOG VALUES
(1, 1, 1, 2, 3, 'Streak above 10 days, increasing difficulty', '2024-11-15', 1),
(2, 3, 2, 3, 2, 'Success rate dropped below 60%',              '2024-11-10', 1),
(3, 5, 3, 1, 1, 'Consistent completion, maintaining level',    '2024-11-12', 0);

INSERT INTO FRIEND VALUES
(1, 1, 2, 'Accepted', '2024-11-10'),
(2, 2, 1, 'Accepted', '2024-11-10'),
(3, 1, 3, 'Pending',  NULL),
(4, 2, 3, 'Accepted', '2024-11-14'),
(5, 3, 2, 'Accepted', '2024-11-14');

INSERT INTO XP_TRANSACTION VALUES
(1, 1, 1,    20, 'Habit completion',  '2024-11-19'),
(2, 1, NULL, 50, 'Badge earned',      '2024-11-18'),
(3, 2, 3,    30, 'Habit completion',  '2024-11-19'),
(4, 2, NULL,100, 'Challenge complete','2024-11-17'),
(5, 3, 5,    10, 'Habit completion',  '2024-11-18');

INSERT INTO REPORT VALUES
(1, 1, 'weekly',  '2024-11-11', '2024-11-17', 310, 17, 2, '2024-11-18'),
(2, 2, 'weekly',  '2024-11-11', '2024-11-17', 420, 35, 3, '2024-11-18'),
(3, 3, 'monthly', '2024-11-01', '2024-11-30',  80,  8, 0, '2024-11-18'),
(4, 1, 'monthly', '2024-11-01', '2024-11-30', 740, 60, 2, '2024-11-18'),
(5, 2, 'monthly', '2024-11-01', '2024-11-30', 980, 85, 4, '2024-11-18');

INSERT INTO FEEDBACK VALUES
(1, 1, 'Bug',        'Streak counter resets incorrectly sometimes', 3, '2024-11-15', 1),
(2, 2, 'Suggestion', 'Add dark mode please',                        5, '2024-11-16', 1),
(3, 3, 'General',    'Love the app, very motivating!',              5, '2024-11-17', 0),
(4, 1, 'Suggestion', 'Weekly report PDF would be helpful',          4, '2024-11-18', 0),
(5, 2, 'Bug',        'Notification time is off by one hour',        2, '2024-11-19', 0);

INSERT INTO SUBSCRIPTION VALUES
(1, 1, 'Free',    '2024-01-10', NULL,         1,    0.00),
(2, 2, 'Premium', '2024-02-15', '2025-02-15', 1,  999.00),
(3, 3, 'Free',    '2024-03-20', NULL,         1,    0.00);

INSERT INTO MODIFICATION_REQUEST VALUES
(1, 1, '2024-11-16', 'Pending',  'Request to change username to john_d'),
(2, 2, '2024-11-17', 'Approved', 'Request to export all habit data as CSV'),
(3, 3, '2024-11-18', 'Pending',  'Request to delete account and all data');
