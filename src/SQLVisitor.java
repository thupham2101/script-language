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
        for(int i = 0; i < number; i++) {
            Insert insert = new Insert();
            insert.setTable(new Table(null,table));
            ExpressionList expList = new ExpressionList();
            expList.setExpressions(new ArrayList<Expression>());
            insert.setItemsList(expList);
            data = data.concat(insert.toString()).concat(";\n");
        }
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
        String property = node.jjtGetChild(3).jjtAccept(this, data);
        String value = node.jjtGetChild(4).jjtAccept(this, data);
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
            return data.concat(update.toString()).concat(";\n");
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
