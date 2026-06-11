package com.customql.ast;

public interface AstNode {
    // You can define a method to accept a visitor, or let Jackson serialize it directly.
    String getType();
}
