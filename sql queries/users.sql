USE BlueBookDB;

CREATE LOGIN user1 WITH PASSWORD = 'strongPwd123';
CREATE USER user1 FOR LOGIN user1;

GRANT INSERT, SELECT, UPDATE ON Guardian TO user1;
GRANT INSERT, SELECT, UPDATE ON GuardianAccountDetails TO user1;
GRANT INSERT, SELECT, UPDATE, DELETE ON Child TO user1;
GRANT INSERT, SELECT, DELETE ON GuardianLanguage TO user1;
GRANT INSERT, SELECT, UPDATE  ON Address TO user1;
GRANT INSERT, SELECT, UPDATE ON Parent TO user1;
GRANT INSERT, SELECT ON familyHealthHistory TO user1;
GRANT INSERT, SELECT ON FamilyDentalHistory TO user1;
GRANT INSERT, SELECT ON ProgressNotes TO user1;
GRANT INSERT, SELECT ON IllnessInjuries TO user1;
GRANT INSERT, SELECT, UPDATE ON UsefulContact TO user1;
GRANT INSERT, SELECT ON ImmunisationRecord to user1;
GRANT INSERT, SELECT, UPDATE ON FourMonthImmunisation to user1;
GRANT INSERT, SELECT, UPDATE, DELETE ON Appointments to user1;
GRANT INSERT, SELECT, UPDATE ON BirthDetails TO user1;
GRANT INSERT, SELECT, UPDATE ON NewbornExamination TO user1;
GRANT INSERT, SELECT, UPDATE ON NBTable TO user1;
GRANT INSERT, SELECT, UPDATE ON HearingPreScreening TO user1;
GRANT INSERT, SELECT, UPDATE, DELETE ON Hearingscreen TO user1;
GRANT INSERT, SELECT, UPDATE ON NewBornHearing TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheckAssessment TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheckAssessmentVariables TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheck TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheckSignage TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheckQuestion TO user1;
GRANT SELECT ON Practitioner TO user1;
GRANT SELECT ON PractitionerAccountDetails TO user1;
GRANT SELECT ON PractitionerGuardianID TO user1;
GRANT INSERT, SELECT, UPDATE ON ChildCheckProtectiveFactors TO user1;
GRANT INSERT, SELECT, UPDATE ON HealthChecks TO user1;

GRANT SELECT ON HealthChecks TO user1;
GRANT DELETE ON IllnessInjuries TO user1;
GRANT DELETE ON BirthDetails TO user1;
GRANT DELETE ON NewbornExamination TO user1;
GRANT DELETE ON NBTable TO user1;
GRANT DELETE ON NewBornHearing TO user1;
GRANT DELETE ON HearingPreScreening TO user1;
GRANT DELETE ON Hearingscreen TO user1;
GRANT DELETE ON FourMonthImmunisation TO user1;
GRANT DELETE ON ChildCheck TO user1;
GRANT DELETE ON ChildCheckQuestion TO user1;
GRANT DELETE ON ProgressNotes TO user1;
GRANT DELETE ON HealthChecks TO user1;
GRANT DELETE ON ImmunisationRecord TO user1;
GRANT DELETE ON ChildCheckAssessment TO user1;
GRANT DELETE ON ChildCheckAssessmentVariables TO user1;
GRANT DELETE ON ChildCheckProtectiveFactors TO user1;
GRANT DELETE ON ChildCheckSignage TO user1;
