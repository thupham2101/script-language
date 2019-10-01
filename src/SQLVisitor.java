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
        int pivot = 0;
        String quantifier = node.jjtGetChild(pivot++).jjtAccept(this, data);
        Integer number = Integer.parseInt(node.jjtGetChild(pivot++).jjtAccept(this, data));
        String table = node.jjtGetChild(pivot++).jjtAccept(this, data);
        List<String> properties = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        while(pivot < node.jjtGetNumChildren()) {
            SSLAssignment assignment = (SSLAssignment) node.jjtGetChild(pivot++);
            String property = assignment.jjtGetChild(0).jjtAccept(this, data);
            String value = assignment.jjtGetChild(1).jjtAccept(this, data);
            properties.add(property);
            values.add(value);
        }
        String propertyList = StringUtils.join(properties, ",");
        String valueList = StringUtils.join(values, ",");
        String propertyNULLAssignment = StringUtils.setPropertiesToNull(properties);
        String propertyValueAssignment = StringUtils.setPropertiesToValues(properties, values, "AND");
        String procedure = "EXACTLY".equalsIgnoreCase(quantifier) ? ScriptingProcedure.DO_EXACTLY_N
                : "AT MOST".equalsIgnoreCase(quantifier) ? ScriptingProcedure.DO_AT_MOST_N
                        : ScriptingProcedure.DO_AT_LEAST_N;
        data = data.concat(
                String.format(procedure, number, table, propertyValueAssignment, propertyNULLAssignment, propertyList, // is
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

    @Override
    public String visit(SSLUpdateStatement node, String data) {
        int pivot = 0;
        String quantity = node.jjtGetChild(pivot++).jjtAccept(this, data);
        String table = node.jjtGetChild(pivot++).jjtAccept(this, data);
        List<String> properties = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        while(pivot < node.jjtGetNumChildren() && node.jjtGetChild(pivot) instanceof SSLAssignment) {
            SSLAssignment assignment = (SSLAssignment) node.jjtGetChild(pivot++);
            String property = assignment.jjtGetChild(0).jjtAccept(this, data);
            String value = assignment.jjtGetChild(1).jjtAccept(this, data);
            properties.add(property);
            values.add(value);
        }
        String propertyValueAssignmentWithAnd = StringUtils.setPropertiesToValues(properties, values, "AND");
        String propertyValueAssignmentWithComma = StringUtils.setPropertiesToValues(properties, values, ",");
        String propConValueAssignment = "";
        if (pivot < node.jjtGetNumChildren()) {
            SSLWhereCondition whereCon = (SSLWhereCondition) node.jjtGetChild(pivot);
            if(whereCon.jjtGetChild(0) instanceof SSLOclExp) {
                String toPSQL = whereCon.jjtGetChild(0).jjtAccept(this, "");
                if ("*".equals(quantity)) {
                    toPSQL = table.concat("_id IN ( SELECT res FROM (")
                            .concat(toPSQL).concat(") AS TEMP)");
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_ALL_WHERE, table, propertyValueAssignmentWithComma,
                            toPSQL)).concat("\n");
                } else {
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_N_WHERE_OCL, Integer.parseInt(quantity), table,
                            propertyValueAssignmentWithComma, propertyValueAssignmentWithAnd, toPSQL,
                            "AND ".concat(propertyValueAssignmentWithAnd))).concat("\n");
                }
            }
            else {
                int pivotWhere = 0;
                List<String> whereProperties = new ArrayList<String>();
                List<String> whereValues = new ArrayList<String>();
                while(pivotWhere < whereCon.jjtGetNumChildren()) {
                    SSLAssignment assignment = (SSLAssignment) whereCon.jjtGetChild(pivotWhere++);
                    String property = assignment.jjtGetChild(0).jjtAccept(this, data);
                    String value = assignment.jjtGetChild(1).jjtAccept(this, data);
                    whereProperties.add(property);
                    whereValues.add(value);
                }
                propConValueAssignment = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
                if ("*".equals(quantity)) {
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_ALL_WHERE, table, propertyValueAssignmentWithComma,
                            propConValueAssignment)).concat("\n");
                } else {
                    data = data.concat(String.format(ScriptingProcedure.UPDATE_N_WHERE, Integer.parseInt(quantity), table,
                            propertyValueAssignmentWithComma, propertyValueAssignmentWithAnd, propConValueAssignment,
                            "AND ".concat(propertyValueAssignmentWithAnd))).concat("\n");
                }
            }
        }
        else {
            if ("*".equals(quantity)) {
                data = data.concat(String.format(ScriptingProcedure.UPDATE_ALL, table, propertyValueAssignmentWithComma));
            }
            else {
                data = data.concat(String.format(ScriptingProcedure.UPDATE_N, Integer.parseInt(quantity), table,
                        propertyValueAssignmentWithComma, propertyValueAssignmentWithAnd, propConValueAssignment,
                        "AND ".concat(propertyValueAssignmentWithAnd))).concat("\n");
            }
        }
        return data;
    }

    @Override
    public String visit(SSLAll node, String data) {
        return (String) node.data.get("value");
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
        if(node.jjtGetChild(pos) instanceof SSLWhereCondition) {
            SSLWhereCondition whereCon1 = (SSLWhereCondition) node.jjtGetChild(pos++);
            if(whereCon1.jjtGetChild(0) instanceof SSLOclExp) {
                whereCondition1 = whereCon1.jjtGetChild(0).jjtAccept(this, "");
                whereCondition1 = table1.concat("_id IN \r\n(SELECT res FROM \r\n(").concat(whereCondition1).concat(") AS TEMP");
                whereCondition1 = "WHERE ".concat(whereCondition1).concat(")\r\n");
            }
            else {
                List<String> whereProperties = new ArrayList<String>();
                List<String> whereValues = new ArrayList<String>();
                int pivotWhere = 0;
                while(pivotWhere < whereCon1.jjtGetNumChildren()) {
                    SSLAssignment assignment = (SSLAssignment) whereCon1.jjtGetChild(pivotWhere++);
                    String property = assignment.jjtGetChild(0).jjtAccept(this, data);
                    String value = assignment.jjtGetChild(1).jjtAccept(this, data);
                    whereProperties.add(property);
                    whereValues.add(value);
                }
                whereCondition1 = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
                whereCondition1 = "WHERE ".concat(whereCondition1).concat("\r\n");
            }
        }
        Integer quantity2 = Integer.parseInt(node.jjtGetChild(pos++).jjtAccept(this, data));
        String table2 = node.jjtGetChild(pos++).jjtAccept(this, data);
        String whereCondition2 = "";
        if(node.jjtGetChild(pos) instanceof SSLWhereCondition) {
            SSLWhereCondition whereCon2 = (SSLWhereCondition) node.jjtGetChild(pos++);
            if(whereCon2.jjtGetChild(0) instanceof SSLOclExp) {
                whereCondition2 = whereCon2.jjtGetChild(0).jjtAccept(this, "");
                whereCondition2 = table2.concat("_id IN \r\n(SELECT res FROM \r\n(").concat(whereCondition2).concat(") AS TEMP");
                whereCondition2 = "WHERE ".concat(whereCondition2).concat(")\r\n");
            }
            else {
                List<String> whereProperties = new ArrayList<String>();
                List<String> whereValues = new ArrayList<String>();
                int pivotWhere = 0;
                while(pivotWhere < whereCon2.jjtGetNumChildren()) {
                    SSLAssignment assignment = (SSLAssignment) whereCon2.jjtGetChild(pivotWhere++);
                    String property = assignment.jjtGetChild(0).jjtAccept(this, data);
                    String value = assignment.jjtGetChild(1).jjtAccept(this, data);
                    whereProperties.add(property);
                    whereValues.add(value);
                }
                whereCondition2 = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
                whereCondition2 = "WHERE ".concat(whereCondition2).concat("\r\n");
            }
        }
        String assocClass = node.jjtGetChild(pos).jjtAccept(this, data);
        String end1 = "";
        String end2 = "";
        try {
            end1 = Utilities.getAssociationAttribute((JSONArray) new JSONParser().parse(this.jsonContext), assocClass, table1);
            end2 = Utilities.getAssociationAttribute((JSONArray) new JSONParser().parse(this.jsonContext), assocClass, table2);
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

    @Override
    public String visit(SSLWhereCondition node, String data) {
        // TODO Auto-generated method stub
        return null;
    }
}
