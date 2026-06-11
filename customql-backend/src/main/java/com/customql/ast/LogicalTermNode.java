package com.customql.ast;

public class LogicalTermNode implements AstNode {
    private AstNode left;
    private AstNode right;

    public LogicalTermNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    public AstNode getLeft() { return left; }
    public AstNode getRight() { return right; }

    @Override
    public String getType() {
        return "AND";
    }
}
