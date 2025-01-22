DROP TABLE IF EXISTS "+this.predictorName+";\n"+
                "CREATE TABLE "+this.predictorName+" AS \n" +
/*//////////// ORIGINALE  ////////////////////////////////////////////////////
SELECT id, 'A' AS class
FROM (
    SELECT 
        id,
        SUM(yyy) OVER (ORDER BY yyy desc ) AS run_sum,
        SUM(yyy) OVER () AS total
    FROM pazienti
) AS subquery
WHERE run_sum <= 0.8 * total

UNION

SELECT sub_ABE.id, 'B' as class
FROM
	(SELECT sub_AB.id
	 FROM (
		   SELECT id,
		   SUM(yyy) OVER (ORDER BY yyy desc ) AS run_sum,
		   SUM(yyy) OVER () AS total
		   FROM pazienti
		   ) AS sub_AB
	WHERE run_sum <= 0.95 * total) AS  sub_ABE

	LEFT JOIN

	(SELECT sub_A.id
	FROM (
		  SELECT id,
		  SUM(yyy) OVER (ORDER BY yyy desc) AS run_sum,
		  SUM(yyy) OVER () AS total
		  FROM pazienti
		) AS sub_A
	WHERE run_sum <= 0.8 * total) AS sub_AE
ON sub_ABE.id = sub_AE.id
WHERE sub_AE.id IS NULL

UNION

SELECT sub_ABE.id, 'C' as class
FROM
	(SELECT sub_AB.id
	 FROM (
		   SELECT id,
		   SUM(yyy) OVER (ORDER BY yyy desc ) AS run_sum,
		   SUM(yyy) OVER () AS total
		   FROM pazienti
		   ) AS sub_AB
	WHERE run_sum <= total) AS  sub_ABE

	LEFT JOIN

	(SELECT sub_A.id
	FROM (
		  SELECT id,
		  SUM(yyy) OVER (ORDER BY yyy desc) AS run_sum,
		  SUM(yyy) OVER () AS total
		  FROM pazienti
		) AS sub_A
	WHERE run_sum <= 0.95 * total) AS sub_AE
ON sub_ABE.id = sub_AE.id
WHERE sub_AE.id IS NULL
ORDER BY id;
*/////////// ORIGINALE END ////////////////////////////////////////////////////

UPDATE pazienti
SET yyy =
WHERE id = ;
----------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS pazienti;
CREATE TABLE pazienti (
    id BIGSERIAL PRIMARY KEY,
    xxx NUMERIC (10,0),
    yyy NUMERIC (10,1),
    ccc  NUMERIC (10,0)
);

INSERT INTO pazienti (xxx,yyy,ccc)
VALUES
(1,5,4),
(1,7,4),
(1,3,4),
(1,2,4),
(1,3,4),
(1,2,4),
(1,8,4),
(1,4,4),
(1,2,4),
(1,4,4);