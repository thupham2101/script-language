/* Generated By:JJTree: Do not edit this line. ASTupdateStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTupdateStatement extends SimpleNode {
  public ASTupdateStatement(int id) {
    super(id);
  }

  public ASTupdateStatement(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public String jjtAccept(ExpressionParserVisitor visitor, String data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fdf51cda4625a8f7305c0457dee98b0b (do not edit this line) */
