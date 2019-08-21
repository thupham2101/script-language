import java.util.ArrayList;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;;

public class SQLVisitor implements ExpressionParserVisitor{

    @Override
    public Object visit(SimpleNode node, Object data) {
        if(node instanceof ASTStatement) {
            return visit((ASTStatement) node, data);
        }
        else if(node instanceof ASTstart) {
            return visit((ASTstart) node, data);
        }
        else if(node instanceof ASTcreateStatement) {
            return visit((ASTcreateStatement) node, data);
        }
        else if(node instanceof ASTdoExactly) {
            return visit((ASTdoExactly) node, data);
        }
        else if(node instanceof ASTdoAtMost) {
            return visit((ASTdoAtMost) node, data);
        }
        else if(node instanceof ASTProperty) {
            return visit((ASTProperty) node, data);
        }
        else if(node instanceof ASTValue) {
            return visit((ASTValue) node, data);
        }
        else if(node instanceof ASTTable) {
            return visit((ASTTable) node, data);
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
    public Object visit(ASTcreateStatement node, Object data) {
        Integer number = (Integer) visit((SimpleNode) node.jjtGetChild(0), data);
        String table = (String) visit((SimpleNode) node.jjtGetChild(1), data);
        String output = (String) data;
        for(int i = 0; i < number; i++) {
            Insert insert = new Insert();
            insert.setTable(new Table(null,table));
            ExpressionList expList = new ExpressionList();
            expList.setExpressions(new ArrayList<>());
            insert.setItemsList(expList);
            output = output.concat(insert.toString()).concat("\n");
        }
        return output;
    }

    @Override
    public Object visit(ASTdoExactly node, Object data) {
        Integer number = (Integer) visit((SimpleNode) node.jjtGetChild(0), data);
        String table = (String) visit((SimpleNode) node.jjtGetChild(1), data);
        String property = (String) visit((SimpleNode) node.jjtGetChild(2), data);
        String value = (String) visit((SimpleNode) node.jjtGetChild(3), data);
        String output = (String) data;

        for(int i = 0; i < number; i++) {
     //   	Update update = new Update();
     //   	update.setTable(new Table(null,table));
    //        ExpressionList expList = new ExpressionList();
    //        expList.setExpressions(new ArrayList<>());
    //        update.accept((StatementVisitor) expList);
      //      output = output.concat(concat("update").concat("\n");
        	output = output.concat("update exactly " + table + " set" + property + " = " + value + "\n");
        }
        return output;
    }
    
    @Override
    public Object visit(ASTdoAtMost node, Object data) {
        Integer number = (Integer) visit((SimpleNode) node.jjtGetChild(0), data);
        String table = (String) visit((SimpleNode) node.jjtGetChild(1), data);
        String property = (String) visit((SimpleNode) node.jjtGetChild(2), data);
        String value = (String) visit((SimpleNode) node.jjtGetChild(3), data);
        String output = (String) data;

        for(int i = 0; i < number; i++) {
     //   	Update update = new Update();
     //   	update.setTable(new Table(null,table));
    //        ExpressionList expList = new ExpressionList();
    //        expList.setExpressions(new ArrayList<>());
    //        update.accept((StatementVisitor) expList);
      //      output = output.concat(concat("update").concat("\n");
        	output = output.concat("update at most" + table + " set " + property + " = " + value + "\n");
        }
        return output;
    }
    
    @Override
    public Object visit(ASTNumber node, Object data) {
        return node.data.get("value");
    }

    @Override
    public Object visit(ASTTable node, Object data) {
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
    


}
