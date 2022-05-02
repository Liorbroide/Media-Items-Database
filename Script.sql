CREATE TABLE MediaItems
    (MID NUMBER(11,0), TITLE VARCHAR(200), PROD_YEAR NUMBER(4), 
    TITLE_LENGTH NUMBER(5),
    CONSTRAINT pk PRIMARY KEY(MID));
/
CREATE TABLE Similarity
    (
    MID1 NUMBER(11,0),
    MID2 NUMBER(11,0),
    SIMILARITY FLOAT,
    CONSTRAINT pk1 PRIMARY KEY (MID1, MID2),
    CONSTRAINT fk FOREIGN KEY (MID1) REFERENCES MediaItems(MID),
    CONSTRAINT fk1 FOREIGN KEY (MID2) REFERENCES MediaItems(MID));
/   

CREATE OR REPLACE TRIGGER AutoIncrement 
BEFORE INSERT 
    on MediaItems for each row
    DECLARE
    row_num NUMBER;
    BEGIN
    SELECT MAX(MID) INTO row_num FROM MediaItems;
    IF row_num is NULL THEN 
        :new.MID:=0;
    ELSE
        :new.MID:=row_num+1;
    END IF;
    :new.TITLE_LENGTH := LENGTH(:new.TITLE);
END;
/
CREATE OR REPLACE FUNCTION MaximalDistance

RETURN NUMBER
IS 
    ret_val NUMERIC := 0;
    minimal NUMBER := 0;
    maximal NUMBER := 0;
BEGIN 

    SELECT MIN(PROD_YEAR) INTO minimal FROM MediaItems;
    SELECT MAX(PROD_YEAR) INTO maximal FROM MediaItems;
    ret_val := POWER(maximal - minimal, 2);
    RETURN ret_val;
END;
/
CREATE OR REPLACE FUNCTION SimCalculation(MID1 NUMBER, MID2 NUMBER, maximal_distance NUMBER)
RETURN NUMBER
IS 
ret_val FLOAT := 0;
distance NUMBER := 0;
prod_year1 NUMBER := 0;
prod_year2 NUMBER := 0;
BEGIN   
SELECT PROD_YEAR INTO prod_year1 FROM MediaItems WHERE MID = MID1;
SELECT PROD_YEAR INTO prod_year2 FROM MediaItems WHERE MID = MID2;
distance := POWER(prod_year1 - prod_year2, 2);
ret_val := 1 - (distance/maximal_distance);

RETURN ret_val;
END;
/
    

    
    
    
    