USE BlueBookDB;

CREATE LOGIN user1 WITH PASSWORD = '';
CREATE USER user1 FOR LOGIN user1;
GRANT INSERT, SELECT ON Guardian TO user1;
GRANT INSERT, SELECT ON GuardianAccountDetails TO user1;
GRANT INSERT, SELECT ON Child TO user1;
GRANT INSERT, SELECT ON GuardianLanguage TO user1;
GRANT INSERT, SELECT ON Address TO user1;
GRANT INSERT, SELECT ON Parent TO user1;
GRANT INSERT, SELECT ON familyHealthHistory TO user1;
GRANT INSERT, SELECT ON FamilyDentalHistory TO user1;
GRANT INSERT, SELECT ON ProgressNotes TO user1;
GRANT INSERT, SELECT ON IllnessInjuries TO user1;
GRANT INSERT, SELECT, UPDATE ON UsefulContact TO user1;
