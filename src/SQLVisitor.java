import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vgu.ocl2psql.main.OCL2PSQL;
import org.vgu.ocl2psql.ocl.exception.OclParseException;
import org.vgu.ocl2psql.ocl.expressions.Utilities;

public class SQLVisitor implements ExpressionParserVisitor {

    private String[] statements;
    private String jsonContext = "[{\"class\":\"Car\",\"attributes\":[{\"name\":\"color\", \"type\":\"String\"}]},{\"class\":\"Person\",\"attributes\":[{\"name\":\"name\", \"type\":\"String\"}]},{\"association\":\"Ownership\",\"ends\":[\"owners\", \"ownedCars\"],\"classes\":[\"Car\", \"Person\"]}]";

    public SQLVisitor(String[] statements) {
        this.statements = new String[statements.length];
        int pos = 0;
        for(String statement : statements) {
            statement = statement.replaceAll("o([s,S][i,I][n,N][g,G])", "u$1");
            statement = statement.replaceAll("O([s,S][i,I][n,N][g,G])", "U$1");
            this.statements[pos] = statement;
        }
    }

    @Override
    public String visit(SimpleNode node, String data) {
        return node.jjtAccept(this, data);
    }

    @Override
    public String visit(SSLStart node, String data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            data = data.concat(String.format(ScriptingProcedure.COMMENTS, statements[i]));
            data = node.jjtGetChild(i).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public String visit(SSLAddStatement node, String data) {
        Integer number = Integer.parseInt(node.jjtGetChild(0).jjtAccept(this, data));
        String table = node.jjtGetChild(1).jjtAccept(this, data);
        data = data.concat(String.format(ScriptingProcedure.ADD_N, table, number)).concat("\n");
        return data;
    }

    @Override
    public String visit(SSLNumber node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(SSLClass node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(SSLDoStatement node, String data) {
        String quantifier = node.jjtGetChild(0).jjtAccept(this, data);
        Integer number = Integer.parseInt(node.jjtGetChild(1).jjtAccept(this, data));
        String table = node.jjtGetChild(2).jjtAccept(this, data);
        SSLAssignments assignments = (SSLAssignments) node.jjtGetChild(3);
        List<String> properties = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < assignments.jjtGetNumChildren(); i++) {
            SSLAssignment assignment = (SSLAssignment) assignments.jjtGetChild(i);
            String property = assignment.jjtGetChild(0).jjtAccept(this, data);
            String value = assignment.jjtGetChild(1).jjtAccept(this, data);
            properties.add(property);
            values.add(value);
        }
        String propertyList = StringUtils.join(properties, ",");
        String valueList = StringUtils.join(values, ",");
        String propertyNULLAssignment = StringUtils.setPropertiesToNull(properties);

        String propertyValueAssignment = StringUtils.setPropertiesToValues(properties, values, "AND");
        // multiple value. color = 'white', brand = 'BMW'
        String procedure = "EXACTLY".equalsIgnoreCase(quantifier) ? ScriptingProcedure.DO_EXACTLY_N
                : "AT MOST".equalsIgnoreCase(quantifier) ? ScriptingProcedure.DO_AT_MOST_N
                        : ScriptingProcedure.DO_AT_LEAST_N;
        data = data.concat(
                String.format(procedure, number, table, propertyValueAssignment, propertyNULLAssignment, propertyList, // is
                                                                                                                       // used
                                                                                                                       // for
                                                                                                                       // insert
                                                                                                                       // multiple
                        valueList))
                .concat("\n");
        return data;

    }

    @Override
    public String visit(SSLQuantifier node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(SSLProperty node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(SSLValue node, String data) {
        return (String) node.data.get("value");
    }
//    

    @Override
    public String visit(SSLUpdateStatement node, String data) {
        String quantity = node.jjtGetChild(0).jjtAccept(this, data);
        String table = node.jjtGetChild(1).jjtAccept(this, data);
        String assignments = node.jjtGetChild(2).jjtAccept(this, "");
        List<String> properties = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        String[] assignmentList = assignments.split(",");
        for (String assignment : assignmentList) {
            String[] assign = assignment.split("=");
            properties.add(assign[0]);
            values.add(assign[1]);
        }
        String propertyValueAssignmentWithAnd = StringUtils.setPropertiesToValues(properties, values, "AND");
        String propertyValueAssignmentWithComma = StringUtils.setPropertiesToValues(properties, values, ",");
        String propConValueAssignment = "";
        if (node.jjtGetNumChildren() > 3) {
            if(node.jjtGetChild(3) instanceof SSLOclExp) {
                String toPSQL = node.jjtGetChild(3).jjtAccept(this, "");
                if ("*".equals(quantity)) {
                    toPSQL = "WHERE ".concat(table).concat("_id IN ( SELECT res FROM (")
                            .concat(toPSQL).concat(") AS TEMP)");
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_ALL, table, propertyValueAssignmentWithComma,
                            toPSQL)).concat("\n");
                } else {
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_N_OCL, Integer.parseInt(quantity), table,
                            propertyValueAssignmentWithComma, propertyValueAssignmentWithAnd, toPSQL,
                            "AND ".concat(propertyValueAssignmentWithAnd))).concat("\n");
                }
            }
            else {
                String whereAssignments = node.jjtGetChild(3).jjtAccept(this, "");
                List<String> whereProperties = new ArrayList<String>();
                List<String> whereValues = new ArrayList<String>();
                String[] whereAssignmentList = whereAssignments.split(",");
                for (String assignment : whereAssignmentList) {
                    String[] assign = assignment.split("=");
                    whereProperties.add(assign[0]);
                    whereValues.add(assign[1]);
                }
                propConValueAssignment = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
                if ("*".equals(quantity)) {
                    propConValueAssignment = "WHERE ".concat(propConValueAssignment);
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_ALL, table, propertyValueAssignmentWithComma,
                            propConValueAssignment)).concat("\n");
                } else {
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_N, Integer.parseInt(quantity), table,
                            propertyValueAssignmentWithComma, propertyValueAssignmentWithAnd, propConValueAssignment,
                            "AND ".concat(propertyValueAssignmentWithAnd))).concat("\n");
                }
            }
        }
        return data;
    }

    @Override
    public String visit(SSLAll node, String data) {
        return (String) node.data.get("value");
    }

    @Override
    public String visit(SSLAssignments node, String data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            data = node.jjtGetChild(i).jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public String visit(SSLAssignment node, String data) {
        String property = node.jjtGetChild(0).jjtAccept(this, data);
        String value = node.jjtGetChild(1).jjtAccept(this, data);
        String concatenation = property.concat("=").concat(value);
        if (!data.isEmpty()) {
            data = data.concat(",").concat(concatenation);
        } else {
            data = concatenation;
        }
        return data;
    }

    @Override
    public String visit(SSLLinkStatement node, String data) {
        int pos = 0;
        Integer quantity1 = Integer.parseInt(node.jjtGetChild(pos++).jjtAccept(this, data));
        String table1 = node.jjtGetChild(pos++).jjtAccept(this, data);
        String whereCondition1 = "";
        if(node.jjtGetChild(pos) instanceof SSLAssignments) {
            String assignments = node.jjtGetChild(pos++).jjtAccept(this, "");
            List<String> whereProperties = new ArrayList<String>();
            List<String> whereValues = new ArrayList<String>();
            String[] whereAssignmentList = assignments.split(",");
            for (String assignment : whereAssignmentList) {
                String[] assign = assignment.split("=");
                whereProperties.add(assign[0]);
                whereValues.add(assign[1]);
            }
            whereCondition1 = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
            whereCondition1 = "WHERE ".concat(whereCondition1).concat(";\r\n");
        } else if(node.jjtGetChild(pos) instanceof SSLOclExp) {
            whereCondition1 = node.jjtGetChild(pos++).jjtAccept(this, "");
            whereCondition1 = table1.concat("_id IN \r\n(SELECT res FROM \r\n(").concat(whereCondition1).concat(") AS TEMP");
            whereCondition1 = "WHERE ".concat(whereCondition1).concat(";\r\n");
        }
        Integer quantity2 = Integer.parseInt(node.jjtGetChild(pos++).jjtAccept(this, data));
        String table2 = node.jjtGetChild(pos++).jjtAccept(this, data);
        String whereCondition2 = "";
        if(node.jjtGetChild(pos) instanceof SSLAssignments) {
            String assignments = node.jjtGetChild(pos++).jjtAccept(this, "");
            List<String> whereProperties = new ArrayList<String>();
            List<String> whereValues = new ArrayList<String>();
            String[] whereAssignmentList = assignments.split(",");
            for (String assignment : whereAssignmentList) {
                String[] assign = assignment.split("=");
                whereProperties.add(assign[0]);
                whereValues.add(assign[1]);
            }
            whereCondition2 = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
            whereCondition2 = "WHERE ".concat(whereCondition2).concat(";\r\n");
        } else if(node.jjtGetChild(pos) instanceof SSLOclExp) {
            whereCondition2 = node.jjtGetChild(pos++).jjtAccept(this, "");
            whereCondition2 = table2.concat("_id IN \r\n(SELECT res FROM \r\n(").concat(whereCondition2).concat(") AS TEMP");
            whereCondition2 = "WHERE ".concat(whereCondition2).concat(";\r\n");
        }
        String assocClass = node.jjtGetChild(pos).jjtAccept(this, data);
        String end1 = "";
        String end2 = "";
        try {
            end1 = Utilities.getAssociation((JSONArray) new JSONParser().parse(this.jsonContext), assocClass, table1);
            end2 = Utilities.getAssociation((JSONArray) new JSONParser().parse(this.jsonContext), assocClass, table2);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        data = data.concat(String.format(ScriptingProcedure.LINK, quantity1, quantity2,
                table1, table2, whereCondition1, whereCondition2, assocClass, end1, end2)).concat("\n");
        return data;
    }

    @Override
    public String visit(SSLOclExp node, String data) {
        String oclExpression = (String) node.data.get("value");
        OCL2PSQL ocl2psql = new OCL2PSQL();
        try {
            ocl2psql.setPlainUMLContext(jsonContext);
            return ocl2psql.mapToString(oclExpression);
        } catch (ParseException | OclParseException e) {
            return "";
        }
    }
    
    public void setJsonContext(String context) {
        this.jsonContext = context;
    }
}
