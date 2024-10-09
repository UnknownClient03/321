USE BlueBookDB;

DROP USER user1;
DROP LOGIN user1;

DROP TABLE PractitionerGuardianID;
DROP TABLE PractitionerAccountDetails;
DROP TABLE Practitioner;
DROP TABLE Appointments;
DROP TABLE ChildCheckSignage;
DROP TABLE ChildCheckProtectiveFactors;
DROP TABLE ChildCheckAssessmentVariables;
DROP TABLE ChildCheckAssessment;
DROP TABLE ChildCheckQuestion;
DROP TABLE ChildCheck;
DROP TABLE FourMonthImmunisation;
DROP TABLE ImmunisationRecord;
DROP TABLE Hearingscreen;
DROP TABLE hearingPreScreening;
DROP TABLE newBornHearing;
DROP TABLE NBTable;
DROP TABLE NewbornExamination;
DROP TABLE BirthDetails;
DROP TABLE IllnessInjuries;
DROP TABLE ProgressNotes;
DROP TABLE HealthChecks;
DROP TABLE FamilyDentalHistory;
DROP TABLE familyHealthHistory;
DROP TABLE parent;
DROP TABLE Child;
DROP TABLE usefulContact;
DROP TABLE GuardianLanguage;
DROP TABLE Address;
DROP TABLE GuardianAccountDetails;
DROP TABLE Guardian;

DROP TRIGGER createPractitioner;
DROP TRIGGER removePractitioner;
