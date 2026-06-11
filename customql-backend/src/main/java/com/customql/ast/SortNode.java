package com.customql.ast;

public class SortNode implements AstNode {
    private String identifier;
    private String direction;

    public SortNode(String identifier, String direction) {
        this.identifier = identifier;
        this.direction = direction;
    }

    public String getIdentifier() { return identifier; }
    public String getDirection() { return direction; }

    @Override
    public String getType() {
        return "Sort";
    }
}
