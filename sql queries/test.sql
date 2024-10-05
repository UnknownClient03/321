USE BlueBookDB;

INSERT INTO Guardian VALUES (0, 'John', 'Doe', 1234567890, 0987654321, '');
INSERT INTO GuardianAccountDetails VALUES (0, 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', '', '');
INSERT INTO Child VALUES (0, 0, 'John', 'Doe', '2024-06-30', 'M');
INSERT INTO GuardianLanguage VALUES (0, 3, 'yellow');
INSERT INTO Address VALUES (0, '', '', '', 0, null, 1234);
INSERT INTO Parent VALUES (0, 'Mother', 'sam', 'smith', '1-7-2024', 1234567890, 0, 0, 'banker');
INSERT INTO familyHealthHistory VALUES (100, 0, '', 0, '');


SELECT * FROM Guardian;
SELECT ID FROM Child WHERE ID = 0 AND guardianID = 0;
SELECT * FROM GuardianLanguage WHERE guardianID = 0;
SELECT * FROM Address WHERE guardianID = 0;
SELECT * FROM Parent WHERE guardianID = 0 AND parent = 'Mother';
SELECT * FROM familyHealthHistory WHERE childID = 0;
SELECT * FROM FamilyDentalHistory WHERE childID = 0;
SELECT * FROM ImmunisationRecord WHERE childID = 0 ORDER BY age ASC;
SELECT * FROM FourMonthImmunisation WHERE childID = 0 ORDER BY CAST(SUBSTRING(question, PATINDEX('%[0-9]%', question), LEN(question)) AS INT);
SELECT * FROM Appointments;

DROP TABLE GuardianLanguage;
GRANT INSERT, SELECT ON GuardianLanguage TO user1;

