package com.customql.ast;

public class ConditionNode implements AstNode {
    private String identifier;
    private String operator;
    private String value;

    public ConditionNode(String identifier, String operator, String value) {
        this.identifier = identifier;
        this.operator = operator;
        this.value = value;
    }

    public String getIdentifier() { return identifier; }
    public String getOperator() { return operator; }
    public String getValue() { return value; }

    @Override
    public String getType() {
        return "Condition";
    }
}
