/* Generated By:JJTree: Do not edit this line. ASTAddValue.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAddValue extends SimpleNode {
  public ASTAddValue(int id) {
    super(id);
  }

  public ASTAddValue(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Statements jjtAccept(ExpressionParserVisitor visitor, Statements data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4262d9c53c2058b208f5e783ee993d9a (do not edit this line) */