-- noinspection SqlDialectInspectionForFile

-- A database for holding JSL output statistics
-- Created 3-22-2018
-- Author: M. Rossetti, rossetti@uark.edu
--
-- This design assumes that the model hierarchy cannot change during a simulation run
-- The model hierarchy could change between runs. This means that model elements
-- are associated with specific simulation runs (i.e. they are id dependent on simulation runs)
CREATE SCHEMA "JSLDb";

CREATE TABLE "JSLDb"."SimulationRun" (
	"id" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
	"simName" VARCHAR(510) NOT NULL,
	"modelName" VARCHAR(510) NOT NULL,
	"expName" VARCHAR(510) NOT NULL,
	"expStartTimeStamp" TIMESTAMP,
	"expEndTimeStamp" TIMESTAMP,
	"numReps" INTEGER NOT NULL CHECK ("numReps" >=1),
	"lastRep" INTEGER, 
	"lengthOfReplication" DOUBLE PRECISION, 
	"lengthOfWarmUp" DOUBLE PRECISION,
	"hasMoreReps" BOOLEAN,
	"repAllowedExecTime" BIGINT,
	"repInitOption" BOOLEAN,
	"resetStartStreamOption" BOOLEAN,
	"antitheticOption" BOOLEAN,
	"advNextSubStreamOption" BOOLEAN,
	"numStreamAdvances" INTEGER
);

CREATE TABLE "JSLDb"."ModelElement" (
	"simRunIdFk" INTEGER NOT NULL,
	"elementName" VARCHAR(510) NOT NULL,
	"elementId" BIGINT NOT NULL,
	"className" VARCHAR(510) NOT NULL,
	"parentNameFk" VARCHAR(510)
);

ALTER TABLE "JSLDb"."ModelElement"
  ADD CONSTRAINT "mePrimKey" PRIMARY KEY ("simRunIdFk", "elementName");
ALTER TABLE "JSLDb"."ModelElement"
	ADD CONSTRAINT "meSimRunFk" FOREIGN KEY ("simRunIdFk") REFERENCES "JSLDb"."SimulationRun" ("id");

CREATE TABLE "JSLDb"."WithinRepStatistics" (
	"id" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
	"modelElementName" VARCHAR(510) NOT NULL,
	"simRunIdFk" INTEGER NOT NULL,
	"statName" VARCHAR(510),
	"repNum" INTEGER NOT NULL CHECK ("repNum" >=1),
	"statCount" DOUBLE PRECISION CHECK ("statCount" >=0),
	"average"  DOUBLE PRECISION,
	"minimum"  DOUBLE PRECISION,
	"maximum"  DOUBLE PRECISION,
	"weightedSum"  DOUBLE PRECISION,
	"sumOfWeights"  DOUBLE PRECISION,
	"weightedSSQ" DOUBLE PRECISION,
	"lastValue" DOUBLE PRECISION,
	"lastWeight" DOUBLE PRECISION	
);

ALTER TABLE "JSLDb"."WithinRepStatistics" 
	ADD CONSTRAINT "wrsSimRunFk" FOREIGN KEY ("simRunIdFk") REFERENCES "JSLDb"."SimulationRun" ("id");
ALTER TABLE "JSLDb"."WithinRepStatistics"
  ADD CONSTRAINT "wrsUniqueElementSimRunRepNum" UNIQUE ("modelElementName", "simRunIdFk", "repNum");
ALTER TABLE "JSLDb"."WithinRepStatistics"
  ADD CONSTRAINT "wrsModelElementFk" FOREIGN KEY ("simRunIdFk", "modelElementName") REFERENCES "JSLDb"."ModelElement" ("simRunIdFk", "elementName");

CREATE TABLE "JSLDb"."AcrossRepStatistics" (
	"id" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
	"modelElementName" VARCHAR(510) NOT NULL,
	"simRunIdFk" INTEGER NOT NULL,
	"statName" VARCHAR(510),
	"statCount" DOUBLE PRECISION CHECK ("statCount" >=0),
	"average"  DOUBLE PRECISION,
	"stdDev" DOUBLE PRECISION,
	"stdErr" DOUBLE PRECISION,
	"halfWidth" DOUBLE PRECISION,
	"confLevel" DOUBLE PRECISION,
	"minimum"  DOUBLE PRECISION,
	"maximum"  DOUBLE PRECISION,
	"weightedSum"  DOUBLE PRECISION,
	"sumOfWeights"  DOUBLE PRECISION,
	"weightedSSQ" DOUBLE PRECISION,
	"devSSQ" DOUBLE PRECISION,
	"lastValue" DOUBLE PRECISION,
	"lastWeight" DOUBLE PRECISION,
	"kurtosis" DOUBLE PRECISION,
	"skewness" DOUBLE PRECISION,
	"lag1Cov" DOUBLE PRECISION,
	"lag1Corr" DOUBLE PRECISION,
	"vonNeumanLag1Statistic" DOUBLE PRECISION,
	"numMissingObs" DOUBLE PRECISION
);

ALTER TABLE "JSLDb"."AcrossRepStatistics" 
	ADD CONSTRAINT "arsSimRunFk" FOREIGN KEY ("simRunIdFk") REFERENCES "JSLDb"."SimulationRun" ("id");

ALTER TABLE "JSLDb"."AcrossRepStatistics"
  ADD CONSTRAINT "arsModelElementFk" FOREIGN KEY ("simRunIdFk", "modelElementName") REFERENCES "JSLDb"."ModelElement" ("simRunIdFk", "elementName");

CREATE TABLE "JSLDb"."WithinRepCounterStatistics" (
	"id" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
	"modelElementName" VARCHAR(510) NOT NULL,
	"simRunIdFk" INTEGER NOT NULL,
	"statName" VARCHAR(510),
	"repNum" INTEGER NOT NULL CHECK ("repNum" >=1),
	"lastValue" DOUBLE PRECISION	
);

ALTER TABLE "JSLDb"."WithinRepCounterStatistics" 
	ADD CONSTRAINT "wrcsSimRunFk" FOREIGN KEY ("simRunIdFk") REFERENCES "JSLDb"."SimulationRun" ("id");

ALTER TABLE "JSLDb"."WithinRepCounterStatistics"
  ADD CONSTRAINT "wrcsUniqueElementSimRunRepNum" UNIQUE ("modelElementName", "simRunIdFk", "repNum");

ALTER TABLE "JSLDb"."WithinRepCounterStatistics"
  ADD CONSTRAINT "wrcsModelElementFk" FOREIGN KEY ("simRunIdFk", "modelElementName") REFERENCES "JSLDb"."ModelElement" ("simRunIdFk", "elementName");

CREATE TABLE "JSLDb"."BatchStatistics" (
	"id" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
	"modelElementName" VARCHAR(510) NOT NULL,
	"simRunIdFk" INTEGER NOT NULL,
	"statName" VARCHAR(510),
	"repNum" INTEGER NOT NULL CHECK ("repNum" >=1),
	"statCount" DOUBLE PRECISION CHECK ("statCount" >=0),
	"average"  DOUBLE PRECISION,
	"stdDev" DOUBLE PRECISION,
	"stdErr" DOUBLE PRECISION,
	"halfWidth" DOUBLE PRECISION,
	"confLevel" DOUBLE PRECISION,
	"minimum"  DOUBLE PRECISION,
	"maximum"  DOUBLE PRECISION,
	"weightedSum"  DOUBLE PRECISION,
	"sumOfWeights"  DOUBLE PRECISION,
	"weightedSSQ" DOUBLE PRECISION,
	"devSSQ" DOUBLE PRECISION,
	"lastValue" DOUBLE PRECISION,
	"lastWeight" DOUBLE PRECISION,
	"kurtosis" DOUBLE PRECISION,
	"skewness" DOUBLE PRECISION,
	"lag1Cov" DOUBLE PRECISION,
	"lag1Corr" DOUBLE PRECISION,
	"vonNeumanLag1Statistic" DOUBLE PRECISION,
	"numMissingObs" DOUBLE PRECISION,
	"minBatchSize" DOUBLE PRECISION,
	"minNumBatches" DOUBLE PRECISION,
	"maxNumBatchesMultiple" DOUBLE PRECISION,
	"maxNumBatches" DOUBLE PRECISION,
	"numRebatches" DOUBLE PRECISION,
	"currentBatchSize" DOUBLE PRECISION,
	"amtUnbatched" DOUBLE PRECISION,
	"totalNumObs" DOUBLE PRECISION
);

ALTER TABLE "JSLDb"."BatchStatistics"
	ADD CONSTRAINT "bsSimRunFk" FOREIGN KEY ("simRunIdFk") REFERENCES "JSLDb"."SimulationRun" ("id");

ALTER TABLE "JSLDb"."BatchStatistics"
  ADD CONSTRAINT "bsModelElementFk" FOREIGN KEY ("simRunIdFk", "modelElementName") REFERENCES "JSLDb"."ModelElement" ("simRunIdFk", "elementName");