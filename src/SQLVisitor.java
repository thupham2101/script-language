import java.util.ArrayList;
import java.util.List;

class ScriptingProcedure {
    public static final String ADD = 
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
    public static final String DO_EXACTLY = 
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
            "SET x = n - preSize;\r\n" + 
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
            "WHILE var < n DO\r\n" + 
            "INSERT INTO %2$s (%5$s) VALUES (%6$s);\r\n" + 
            "SET var = var + 1;\r\n" + 
            "END WHILE;\r\n" + 
            "END;\r\n" +
            "END IF;\r\n" + 
            "END $$\r\n" + 
            "DELIMITER ;\r\n" + 
            "call doExactly(%d);";  
}

public class SQLVisitor implements ExpressionParserVisitor{

    @Override
    public String visit(SimpleNode node, String data) {
        return node.jjtAccept(this, data);
    }

    @Override
    public String visit(ASTstart node, String data) {
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
            data = node.jjtGetChild(i).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public String visit(ASTStatement node, String data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public String visit(ASTaddStatement node, String data) {
        Integer number = Integer.parseInt(node.jjtGetChild(0).jjtAccept(this, data));
        String table = node.jjtGetChild(1).jjtAccept(this, data);
        data = data.concat(String.format(ScriptingProcedure.ADD, table, number)).concat("\n");
        return data;
    }
    
    @Override
    public String visit(ASTNumber node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(ASTClass node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(ASTdoStatement node, String data) {
        String quantifier = node.jjtGetChild(0).jjtAccept(this, data);
        Integer number = Integer.parseInt(node.jjtGetChild(1).jjtAccept(this, data));
        String table = node.jjtGetChild(2).jjtAccept(this, data);
        int propertyStartPivot = 3;
        List<String> properties = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        while(propertyStartPivot < node.jjtGetNumChildren()) {
            String property = node.jjtGetChild(3).jjtAccept(this, data);
            String value = node.jjtGetChild(4).jjtAccept(this, data);
            properties.add(property);
            values.add(value);
            propertyStartPivot += 2;
        }
        String propertyList = StringUtils.join(properties, ",");
        String valueList = StringUtils.join(values, ",");
        String propertyNULLAssignment = StringUtils.setPropertiesToNull(properties);
        String propertyValueAssignment = StringUtils.setPropertiesToValues(properties, values);
        if("EXACTLY".equals(quantifier)) {
            data = data.concat(String.format(
                    ScriptingProcedure.DO_EXACTLY, number,
                    table,
                    propertyValueAssignment,
                    propertyNULLAssignment,
                    propertyList,
                    valueList)).concat("\n");
            return data;
        }
        else if("AT MOST".equals(quantifier)) {
            return "";
        }
        else {
            return "";
        }
    }

    @Override
    public String visit(ASTQuantifier node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(ASTProperty node, String data) {
        return (String) node.data.get("value");
    }
    
    @Override
    public String visit(ASTValue node, String data) {
        return (String) node.data.get("value");
    }
//    


}
