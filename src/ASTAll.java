/* Generated By:JJTree: Do not edit this line. ASTAll.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAll extends SimpleNode {
  public ASTAll(int id) {
    super(id);
  }

  public ASTAll(ExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public String jjtAccept(ExpressionParserVisitor visitor, String data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3201b80e83b736dadf95784695e9fb50 (do not edit this line) */
