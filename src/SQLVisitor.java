import java.util.ArrayList;
import java.util.Arrays;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

public class SQLVisitor implements ExpressionParserVisitor{

    @Override
    public Object visit(SimpleNode node, Object data) {
        if(node instanceof ASTStatement) {
            return visit((ASTStatement) node, data);
        }
        else if(node instanceof ASTstart) {
            return visit((ASTstart) node, data);
        }
        else if(node instanceof ASTaddStatement) {
            return visit((ASTaddStatement) node, data);
        }
        else if(node instanceof ASTdoStatement) {
            return visit((ASTdoStatement) node, data);
        }
        else if(node instanceof ASTClass) {
            return visit((ASTClass) node, data);
        }
        else if(node instanceof ASTQuantifier) {
            return visit((ASTQuantifier) node, data);
        }
        else if(node instanceof ASTProperty) {
            return visit((ASTProperty) node, data);
        }
        else if(node instanceof ASTValue) {
            return visit((ASTValue) node, data);
        }
        else
            return visit((ASTNumber) node, data);
    }

    @Override
    public Object visit(ASTstart node, Object data) {
        String output = (String) data;
        for(Node child : node.children) {
            SimpleNode simpleChild = (SimpleNode) child; 
            output = output
                    .concat((String) visit(simpleChild, output))
                    .concat("\n");
        }
        return output;
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        SimpleNode simpleChild = (SimpleNode) node.jjtGetChild(0);
        return visit(simpleChild, data);
    }

    @Override
    public Object visit(ASTaddStatement node, Object data) {
        Integer number = (Integer) visit((SimpleNode) node.jjtGetChild(0), data);
        String table = (String) visit((SimpleNode) node.jjtGetChild(1), data);
        String output = (String) data;
        for(int i = 0; i < number; i++) {
            Insert insert = new Insert();
            insert.setTable(new Table(null,table));
            ExpressionList expList = new ExpressionList();
            expList.setExpressions(new ArrayList<Expression>());
            insert.setItemsList(expList);
            output = output.concat(insert.toString()).concat(";\n");
        }
        return output;
    }
    
    @Override
    public Object visit(ASTNumber node, Object data) {
        return node.data.get("value");
    }

    @Override
    public Object visit(ASTClass node, Object data) {
        return node.data.get("value");
    }

    @Override
    public Object visit(ASTdoStatement node, Object data) {
        String quantifier = (String) visit((SimpleNode) node.jjtGetChild(0), data);
        Integer number = (Integer) visit((SimpleNode) node.jjtGetChild(1), data);
        String table = (String) visit((SimpleNode) node.jjtGetChild(2), data);
        String property = (String) visit((SimpleNode) node.jjtGetChild(3), data);
        String value = (String) visit((SimpleNode) node.jjtGetChild(4), data);
        String output = (String) data;
        if("EXACTLY".equals(quantifier)) {
            MyUpdate update = new MyUpdate();
            Table tgtTable = new Table(null, table);
            update.setTable(tgtTable);
            update.setColumns(Arrays.asList(new Column(tgtTable, property)));
            update.setExpressions(Arrays.asList(new StringValue(value)));
            BinaryExpression whereExp = new NotEqualsTo();
            whereExp.setLeftExpression(new Column(tgtTable, property));
            whereExp.setRightExpression(new StringValue(value));
            update.setWhere(whereExp);
            return output.concat(update.toString()).concat(";\n");
        }
        else if("AT MOST".equals(quantifier)) {
            return "";
        }
        else {
            return "";
        }
    }

    @Override
    public Object visit(ASTQuantifier node, Object data) {
        return node.data.get("value");
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        return node.data.get("value");
    }
    
    @Override
    public Object visit(ASTValue node, Object data) {
        return node.data.get("value");
    }
//    


}
