
public class ScriptingProcedure {
    public static final String ADD_N = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS createInstances $$\r\n" + 
            "CREATE PROCEDURE createInstances(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE var INT;\r\n" + 
            "SET var = 0;\r\n" + 
            "WHILE var < n DO\r\n" + 
            "INSERT INTO %s VALUES ();\r\n" + 
            "SET var = var + 1;\r\n" + 
            "END WHILE;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call createInstances(%d);";
    public static final String DO_EXACTLY_N = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS doExactly $$\r\n" + 
            "CREATE PROCEDURE doExactly(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) INTO preSize\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %3$s;\r\n" + 
            "IF(preSize > n) THEN\r\n" +
            "BEGIN\r\n" +
            "DECLARE x INT;\r\n" + 
            "SET x = preSize - n;\r\n" + 
            "UPDATE %2$s \r\n" + 
            "SET %4$s \r\n" + 
            "WHERE %3$s \r\n" + 
            "LIMIT x;\r\n" + 
            "END;\r\n" +
            "ELSE\r\n" +
            "BEGIN\r\n" +
            "DECLARE x INT;\r\n" + 
            "DECLARE var INT;\r\n" + 
            "SET x = n - preSize;\r\n" + 
            "SET var = 0;\r\n" + 
            "WHILE var < x DO\r\n" + 
            "INSERT INTO %2$s (%5$s) VALUES (%6$s);\r\n" + 
            "SET var = var + 1;\r\n" + 
            "END WHILE;\r\n" + 
            "END;\r\n" +
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call doExactly(%d);";  
    public static final String DO_AT_LEAST_N =
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS doAtLeast $$\r\n" + 
            "CREATE PROCEDURE doAtLeast(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) into preSize\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %3$s;\r\n" + 
            "IF(preSize < n) THEN\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE x INT;\r\n" + 
            "DECLARE var INT;\r\n" + 
            "SET x = n - preSize;\r\n" + 
            "SET var = 0;\r\n" + 
            "WHILE var < x DO\r\n" + 
            "INSERT INTO %2$s (%5$s) VALUES (%6$s);\r\n" + 
            "SET var = var + 1;\r\n" + 
            "END WHILE;\r\n" + 
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call doAtLeast(%d);";
    public static final String DO_AT_MOST_N =
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS doAtMost $$\r\n" + 
            "CREATE PROCEDURE doAtMost(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) into preSize\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %3$s;\r\n" + 
            "IF(preSize > n) THEN\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE x INT;\r\n" + 
            "SET x = preSize - n;\r\n" + 
            "UPDATE %2$s \r\n" + 
            "SET %4$s \r\n" + 
            "WHERE %3$s\r\n" + 
            "LIMIT x;\r\n" + 
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call doAtMost(%d);";
    public static final String UPDATE_ALL = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS updatePropertyAll $$\r\n" + 
            "CREATE PROCEDURE updatePropertyAll()\r\n" + 
            "BEGIN\r\n" + 
            "UPDATE %1$s\r\n" + // %1: Table
            "SET %2$s\r\n" +  // %2: color = 'black'
            "%3$s;\r\n" + // %3: brand = 'BMW'
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updatePropertyAll();";
    public static final String UPDATE_N = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS updateProperty $$\r\n" + 
            "CREATE PROCEDURE updateProperty(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSizeSatisfy INT;\r\n" + 
            "DECLARE preSizeWhole INT;\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) INTO preSizeSatisfy\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %5$s %6$s;\r\n" + 
            "SELECT COUNT(*) INTO preSizeWhole\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %5$s;\r\n" + 
            "SET preSize = preSizeWhole - preSizeSatisfy;\r\n" +
            "IF(preSize >= n) THEN\r\n" + 
            "BEGIN\r\n" + 
            "UPDATE %2$s\r\n" + 
            "SET %3$s\r\n" + 
            "WHERE %5$s AND %2$s_id NOT IN\r\n" + 
            "(SELECT %2$s_id\r\n" +
            "FROM (SELECT * FROM %2$s) AS TEMP\r\n" +
            "WHERE %4$s)\r\n" +
            "LIMIT n;\r\n" +
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updateProperty(%d);";
}
