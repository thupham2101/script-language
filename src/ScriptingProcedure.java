
public class ScriptingProcedure {
    public static final String COMMENTS = "/* %s */\r\n";
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
            "UPDATE %1$s\r\n" +
            "SET %2$s\r\n" + 
            "%3$s;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updatePropertyAll();";
    public static final String UPDATE_N = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS updateProperty $$\r\n" + 
            "CREATE PROCEDURE updateProperty(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) INTO preSize\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %5$s;\r\n" + 
            "IF(preSize >= n) THEN\r\n" + 
            "BEGIN\r\n" + 
            "UPDATE %2$s\r\n" + 
            "SET %3$s\r\n" + 
            "WHERE %5$s\r\n" + 
            "LIMIT n;\r\n" +
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updateProperty(%d);";
    public static final String UPDATE_N_OCL = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS updateProperty $$\r\n" + 
            "CREATE PROCEDURE updateProperty(n INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSize INT;\r\n" + 
            "SELECT COUNT(*) INTO preSize\r\n" + 
            "FROM %2$s\r\n" + 
            "WHERE %2$s_id IN (\r\n" + 
            "SELECT res\r\n" +
            "FROM (%5$s) AS TEMP);\r\n" +
            "IF(preSize >= n) THEN\r\n" + 
            "BEGIN\r\n" + 
            "UPDATE %2$s\r\n" + 
            "SET %3$s\r\n" + 
            "WHERE %2$s_id IN (\r\n" + 
            "SELECT res\r\n" +
            "FROM (%5$s) AS TEMP);\r\n" +
            "LIMIT n;\r\n" +
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updateProperty(%d);";
    public static final String LINK = 
            "DELIMITER $$\r\n" + 
            "DROP PROCEDURE IF EXISTS link $$\r\n" + 
            "CREATE PROCEDURE link(left INT, right INT)\r\n" + 
            "BEGIN\r\n" + 
            "DECLARE preSizeLeft INT;\r\n" + 
            "DECLARE preSizeRight INT;\r\n" + 
            "SELECT COUNT(*) INTO preSizeLeft\r\n" + 
            "FROM %3$s\r\n" + 
            "%5$s" + 
            "SELECT COUNT(*) INTO preSizeRight\r\n" + 
            "FROM %4$s\r\n" + 
            "%6$s" + 
            "IF(preSizeLeft >= left AND preSizeRight >= right) THEN\r\n" + 
            "BEGIN\r\n" + 
            "DELETE FROM %7$s;\r\n" + 
            "INSERT INTO %7$s (%8$s, %9$s)\r\n" + 
            "SELECT src, tgt \r\n" + 
            "FROM \r\n" +
            "(SELECT %3$s_id as src FROM %3$s %5$s) AS TEMP_left,\r\n" +
            "(SELECT %4$s_id as tgt FROM %4$s %6$s) AS TEMP_right;\r\n" +
            "END;\r\n" + 
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call updateProperty(%d, %d);";
}
