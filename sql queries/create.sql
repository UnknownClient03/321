USE BlueBookDB;

CREATE TABLE Guardian (
	ID int not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	phoneNumber bigint not null,
	altPhoneNumber bigint not null,
	email varchar(127) not null,
	PRIMARY KEY (ID),
	UNIQUE(email)
);

CREATE TABLE GuardianAccountDetails(
	guardianID int not null,
	Hashpassword varchar(64) not null,
	salt varchar(16) not null,
	pepper varchar(16) not null,
	PRIMARY KEY (guardianID),
	FOREIGN KEY (guardianID) REFERENCES Guardian(ID)
);

CREATE TABLE Address (
	guardianID int not null,
	Country varchar(31) not null,
	City varchar(31) not null,
	Street varchar(31) not null,
	StreetNumber int not null,
	unit CHAR,
	postcode int,
	PRIMARY KEY (guardianID),
	FOREIGN KEY (guardianID) REFERENCES Guardian(ID),
	CHECK (postcode < 100000),
);

CREATE TABLE GuardianLanguage(
	guardianID int not null,
	ID int not null,
	language varchar(31) not null,
	PRIMARY KEY(ID),
	FOREIGN KEY (guardianID) REFERENCES Guardian(ID)
);

CREATE TABLE UsefulContact(
	guardianID int not null,
	name varchar(31) not null,
	phoneNumber bigint,
	email varchar(63),
	Country varchar(31) not null,
	City varchar(31) not null,
	Street varchar(31) not null,
	StreetNumber int not null,
	unit CHAR,
	postcode int not null,
	CHECK (name = 'Family doctor' OR
		   name = 'Child and Family Health Centre' OR
		   name = 'Dentist' OR
		   name = 'Specialist doctor' OR
		   name = 'Family daycare/Childcare centre' OR
		   name = 'Pre-school/Kindergarten' OR
		   name = 'Community Health Centre' OR
		   name = 'Primary school' OR
		   name = 'High school' OR
		   name = 'Local government/Council'),
	FOREIGN KEY (guardianID) REFERENCES Guardian(ID)
);

CREATE TABLE Child(
	ID int not null,
	guardianID int not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	DOB DATE not null,
	sex CHAR(1) not null,
	PRIMARY KEY(ID),
	CHECK (sex = 'M' OR sex = 'F'),
	FOREIGN KEY (guardianID) REFERENCES Guardian(ID)
);

CREATE TABLE Parent (
	childID int not null,
	parent varchar(6) not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	DOB DATE not null,
	MRN int not null,
	isAboriginal BIT DEFAULT 0,
	isTorresStraitIslander BIT  DEFAULT 0,
	career varchar(31) not null,
	CHECK (parent = 'Mother' OR parent = 'Father'),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE familyHealthHistory(
	ID int not null,
	childID int not null,
	riskFactor varchar(255) not null,
	condition BIT DEFAULT 0,
	note varchar(255),
	PRIMARY KEY(ID),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE FamilyDentalHistory(
	ID int not null,
	childID int not null,
	riskFactor varchar(127) not null,
	condition BIT DEFAULT 0,
	note varchar(255),
	PRIMARY KEY(ID),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE HealthChecks(
	childID int not null,
	ID int not null,
	age int not null,
	dateTime DATETIME not null,
	comments varchar(255),
	PRIMARY KEY(ID),
	CHECK(age = '1-4 weeks' OR 
		  age = '6-8 weeks' OR 
		  age = '6 months' OR 
		  age = '12 months' OR 
		  age = '18 months' OR 
		  age = '2 years' OR 
		  age = '3 years' OR 
		  age = '4 years'),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE ProgressNotes(
	childID int not null,
	ID int not null,
	date DATE not null,
	age int not null,
	reason varchar(255),
	PRIMARY KEY(ID),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE IllnessInjuries(
	childID int not null,
	ID int not null,
	date DATE not null,
	problem varchar(255) not null,
	signed varchar(31) not null,
	PRIMARY KEY(ID),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE BirthDetails(
	childID int not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	birthFacility varchar(63) not null,
	DOB DATETIME not null,
	sex CHAR(1) not null,
	--maternal Infomation
	mothersFName varchar(31) not null,
	mothersLName varchar(31) not null,
	mothersMRN int not null,
	pregnancyComplications varchar(255),
	bloodGroup varchar(15) not null,
	antiDGigen bit DEFAULT 0 not null,
	labour varchar(15) not null,
	labourComplications varchar(255),
	typeofBirth varchar (15) not null,
	--Neonatal Infomation
	estimatedGestation int not null,
	apgarScore1Min int not null,
	apgarScore5Min int not null,
	abnormaltiesAtBirth varchar(255),
	problemsRequiringTreatment varchar(255),
	birthWeight int,
	birthLength int,
	birthHeadCirc int,
	newBornHearingScreenCompleted BIT DEFAULT 0 not null,
	newBornBloodspotScreenTest DATE,
	vitaminKGiven varchar(10),
	vitaminKGiven1st DATE,
	vitaminKGiven2nd DATE,
	vitaminKGiven3nd DATE,
	helpBImmunisationGiven DATE,
	helpBImmunoglobinGiven DATE,
	--Discharge Infomation
	postPartumComplications varchar(255),
	feedingAtDischarge varchar(6),
	difficultiesWithFeeding varchar(255),
	dateOfDischarge DATE,
	dischargeWeight int,
	headCirc int,
	printName varchar(31),
	signature varchar(max),
	designation varchar(31),
	--Constraints
	PRIMARY KEY(childID),
	CHECK (apgarScore1Min > 0 AND apgarScore1Min < 10),
	CHECK (apgarScore5Min > 0 AND apgarScore5Min < 10),
	CHECK(vitaminKGiven = 'injection' OR vitaminKGiven = 'oral'),
	CHECK (feedingAtDischarge = 'breast' OR feedingAtDischarge = 'bottle'),
	FOREIGN KEY (childID) REFERENCES Child(ID),
);

CREATE TABLE NewbornExamination(
	childID int not null,
	DOB DATE not null,
	age int not null,
	sex char(1) not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	anyConcerns BIT DEFAULT 0 not null,
	anyConcernsComment varchar(255),
	examiner varchar(31) not null,
	designation varchar(31) not null,
	signature varchar(max) not null,
	date DATE not null,
	PRIMARY KEY(childID),
	FOREIGN KEY (childID) REFERENCES Child(ID),
);

CREATE TABLE NBTable(
	childID int not null,
	_check varchar(31) not null,
	isNormal BIT not null,
	comment varchar(255),
	CHECK ( _check = 'Head and Fontanelles' OR 
		_check = 'Eyes' OR 
		_check = 'Ears' OR 
		_check = 'Mouth and palate' OR 
		_check = 'Cardiovascular' OR 
		_check = 'Femoral Pulses' OR 
		_check = 'Resiratory rate' OR 
		_check = 'Abdomen and umbilicus' OR 
		_check = 'Anus' OR 
		_check = 'Genitalia' OR 
		_check = 'Testes fully descended R/L' OR 
		_check = 'Musculo-skeletal' OR 
		_check = 'Hips' OR 
		_check = 'Skin' OR 
		_check = 'Reflexes'
	),
	FOREIGN KEY (childID) REFERENCES NewbornExamination(childID),
);

CREATE TABLE NewBornHearing(
	childID int not null,
	preScreeningVal varchar(6) not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	DOB DATE not null,
	requiresrepeatScreen BIT DEFAULT 0 not null,
	hearingRiskFactorIdentified BIT DEFAULT 0 not null,
	coordinatorTelephone int,
	PRIMARY KEY(childID),
	CHECK (preScreeningVal = 'Normal' OR
		   preScreeningVal = 'review' OR
		   preScreeningVal = 'Refer'),
	FOREIGN KEY (childID) REFERENCES Child(ID),
);

CREATE TABLE HearingPreScreening(
	childID int not null,
	question varchar(63) not null,
	condition BIT DEFAULT 0 not null,
	FOREIGN KEY (childID) REFERENCES newBornHearing(childID),
);

CREATE TABLE Hearingscreen(
	childID int not null,
	screenNumber int not null,
	location varchar(127) not null,
	date DATE not null,
	screenBy varchar(31) not null,
	signature varchar(max) not null,
	rightOutcome varchar(5) not null,
	leftOutcome varchar(5) not null,
	referToAudiologist BIT DEFAULT 0 not null,
	PRIMARY KEY(screenNumber),
	CHECK(rightOutcome = 'Pass' OR rightOutcome = 'Refer'),
	CHECK(leftOutcome = 'Pass' OR leftOutcome = 'Refer'),
	CHECK(screenNumber = 1 OR screenNumber = 2),
	FOREIGN KEY (childID) REFERENCES newBornHearing(childID),
);

CREATE TABLE ImmunisationRecord(
	childID int not null,
	ID int not null,
	age varchar(20) not null,
	vaccine varchar(31) not null,
	dateGiven DATE not null,
	batchNum int not null,
	PRIMARY KEY(ID),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE FourMonthImmunisation(
	childID int not null,
	question varchar(63) not null,
	answer BIT DEFAULT 0 not null,
	PRIMARY KEY(childID, question),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE ChildCheck(
	childID int not null,
	childCheckID int not null,
	checkType varchar(10),
	DOB DATE not null,
	age int not null,
	sex char(1) not null,
	fname varchar(31) not null,
	lname varchar(31) not null,
	childStatus varchar(6) not null,
	parentsNotes varchar(255),
	feeding BIT DEFAULT 0,
	--health assessement & protective factors
	weight int not null,
	length int not null,
	headCirc int not null,
	outcome varchar(6) not null,
	healthInfoDiscussed BIT DEFAULT 0 not null,
	--signage
	results varchar(255),
	comments varchar(255),
	actionTaken varchar(255),
	nameOfDoctor varchar(31) not null,
	signature varchar(max) not null,
	venue varchar(31) not null,
	date DATE not null,
	PRIMARY KEY(childCheckID),
	CHECK(checkType = '1-4 weeks' OR
	      checkType = '6-8 weeks' OR
	      checkType = '4 month' OR
	      checkType = '6 month' OR
	      checkType = '12 month' OR
	      checkType = '18 month' OR
	      checkType = '2 year' OR
	      checkType = '3 year' OR
	      checkType = '4 year'),
	CHECK(childStatus = 'normal' OR
		  childStatus = 'review' OR
		  childStatus = 'refer'),
	CHECK(outcome = 'normal' OR
		  outcome = 'review' OR
		  outcome = 'refer'),
	FOREIGN KEY (childID) REFERENCES Child(ID)
);

CREATE TABLE ChildCheckQuestion(
	childCheckID int not null,
	question varchar(63) not null,
	condition BIT DEFAULT 0 not null,
	FOREIGN KEY (childCheckID) REFERENCES childCheck(childCheckID)
);

CREATE TABLE ChildCheckAssessment(
	childCheckID int not null,
	item varchar(63) not null,
	status varchar(6) not null,
	PRIMARY KEY(childCheckID),
	CHECK (status = 'normal' OR
		   status = 'review' OR
		   status = 'Refer'),
	CHECK (item = 'weight' OR
		   item = 'length/height' OR
		   item = 'BMI' OR
		   item = 'headCirc' OR
		   item = 'frontanelles' OR
		   item = 'eyes' OR
		   item = 'eyes - observation' OR
		   item = 'eyes - corneal light reflection' OR
		   item = 'eyes - fixation' OR
		   item = 'eyes - response to looking with one eye' OR
		   item = 'eyes - eye movements' OR
		   item = 'cardiovascular' OR
		   item = 'oral - visible plaque' OR
		   item = 'oral - bleeding gums' OR
		   item = 'oral - white spot or carious' OR
		   item = 'oral - facial swelling' OR
		   item = 'evaluate gait' OR
		   item = 'umbilicus' OR
		   item = 'femoral pulses' OR
		   item = 'Hips - test for dislocation' OR
		   item = 'Hips - clinical observation of physical signs' OR
		   item = 'Testes fully descended R/L' OR
		   item = 'genitalia' OR
		   item = 'anal region' OR
		   item = 'skin' OR
		   item = 'reflexes'),
	FOREIGN KEY (childCheckID) REFERENCES childCheck(childCheckID)
);

CREATE TABLE ChildHealthFactors(
	childCheckID int not null,
	immunisationUpToDate BIT DEFAULT 0 not null,
	hearing BIT DEFAULT 0 not null,
	vision BIT DEFAULT 0 not null,
	hips BIT,
	oralHealth BIT DEFAULT 0 not null,
	PRIMARY KEY(childCheckID),
	FOREIGN KEY (childCheckID) REFERENCES childCheck(childCheckID)
);
