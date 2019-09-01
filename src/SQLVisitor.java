import java.util.ArrayList;
import java.util.List;


public class SQLVisitor implements ExpressionParserVisitor{

    @Override
    public String visit(SimpleNode node, String data) {
        return node.jjtAccept(this, data);
    }

    @Override
    public String visit(SSLStart node, String data) {
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
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
        for(int i = 0; i < assignments.jjtGetNumChildren(); i++) {
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
        data = data.concat(String.format(
                procedure, number,
                table,
                propertyValueAssignment,
                propertyNULLAssignment,
                propertyList, // is used for insert multiple
                valueList)).concat("\n");
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
	        for(String assignment : assignmentList) {
	            String[] assign = assignment.split("=");
	            properties.add(assign[0]);
	            values.add(assign[1]);
	        }
	        String propertyValueAssignmentWithAnd = StringUtils.setPropertiesToValues(properties, values, "AND");
	        String propertyValueAssignmentWithComma = StringUtils.setPropertiesToValues(properties, values, ",");
	        String propConValueAssignment = "";
	        if(node.jjtGetNumChildren() > 3) {
	            String whereAssignments = node.jjtGetChild(3).jjtAccept(this, "");
	            List<String> whereProperties = new ArrayList<String>();
	            List<String> whereValues = new ArrayList<String>();
	            String[] whereAssignmentList = whereAssignments.split(",");
	            for(String assignment : whereAssignmentList) {
	                String[] assign = assignment.split("=");
	                whereProperties.add(assign[0]);
	                whereValues.add(assign[1]);
	            }
	            propConValueAssignment = StringUtils.setPropertiesToValues(whereProperties, whereValues, "AND");
	        }
	        if ("*".equals(quantity)) {
	        	propConValueAssignment = "WHERE ".concat(propConValueAssignment);
	        	data = data.concat(String.format(
	        	        ScriptingProcedure.UPDATE_ALL, 
	                    table,
	                    propertyValueAssignmentWithComma,
	                    propConValueAssignment
	                    )).concat("\n");
	        }
	        else {
	        	data = data.concat(String.format(
	        	        ScriptingProcedure.UPDATE_N, 
	        	        Integer.parseInt(quantity),
	                    table,
	                    propertyValueAssignmentWithComma,
	                    propertyValueAssignmentWithAnd,
	                    propConValueAssignment,
	                    "AND ".concat(propertyValueAssignmentWithAnd)
	                    )).concat("\n");
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
        if(!data.isEmpty()) {
            data = data.concat(",").concat(concatenation);
        } else {
            data = concatenation;
        }
        return data;
    }
}
