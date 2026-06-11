package com.customql.ast;

public class QueryNode implements AstNode {
    private String target;
    private AstNode whereClause;
    private AstNode sortClause;

    public QueryNode(String target, AstNode whereClause, AstNode sortClause) {
        this.target = target;
        this.whereClause = whereClause;
        this.sortClause = sortClause;
    }

    public String getTarget() { return target; }
    public AstNode getWhereClause() { return whereClause; }
    public AstNode getSortClause() { return sortClause; }

    @Override
    public String getType() {
        return "Query";
    }
}
