import java.util.HashMap;
import java.util.LinkedList;

public class ExpressionVisitor implements ExpressionParserVisitor{
	
	private LinkedList stack=new LinkedList();
	private HashMap symbolTable=new HashMap();

	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this,data);
		return null;
	}

	public Object visit(ASTstart node, Object data) {
		node.childrenAccept(this,data);
		return symbolTable;
	}

	public Object visit(ASTNumber node, Object data) {
		node.childrenAccept(this,data);
		stack.addFirst(node.data.get("value"));
		return null;
	}

	public Object visit(ASTStatement node, Object data) {
		node.childrenAccept(this,data);
		Integer value=(Integer)stack.removeFirst();
		String var=(String)stack.removeFirst();
		symbolTable.put(var,value);
		return null;
	}

	private Integer pop()
	{
		return (Integer)stack.removeFirst();
	}

    @Override
    public Object visit(ASTTable node, Object data) {
        node.childrenAccept(this,data);
        stack.addFirst(node.data.get("value"));
        return null;
    }

    @Override
    public Object visit(ASTcreateStatement node, Object data) {
        node.childrenAccept(this,data);
        Integer value=(Integer)stack.removeFirst();
        String var=(String)stack.removeFirst();
        symbolTable.put(var,value);
        return null;
    }

}
