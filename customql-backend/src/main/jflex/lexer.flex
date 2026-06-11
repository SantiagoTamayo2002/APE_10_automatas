package com.customql;

import java_cup.runtime.Symbol;

%%
%class CustomLexer
%public
%unicode
%cup
%line
%column

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

/* Whitespace */
Whitespace = [ \t\f\r\n]+

/* Identifiers */
Identifier = [a-zA-Z_][a-zA-Z0-9_]*

/* Numbers */
NumberLiteral = [0-9]+(\.[0-9]+)?

/* Strings */
StringLiteral = \"[^\"]*\"

%%



/* Keywords */
"GET"   { return symbol(sym.GET); }
"WHERE" { return symbol(sym.WHERE); }
"AND"   { return symbol(sym.AND); }
"OR"    { return symbol(sym.OR); }
"SORT"  { return symbol(sym.SORT); }
"BY"    { return symbol(sym.BY); }
"ASC"   { return symbol(sym.ASC); }
"DESC"  { return symbol(sym.DESC); }




/* Operators */
">="    { return symbol(sym.GTE); }
"<="    { return symbol(sym.LTE); }
"!="    { return symbol(sym.NEQ); }
">"     { return symbol(sym.GT); }
"<"     { return symbol(sym.LT); }
"="     { return symbol(sym.EQ); }

/* Punctuation */
";"     { return symbol(sym.SEMI); }


/* Literals */
{Identifier}     { return symbol(sym.IDENTIFIER, yytext()); }
{NumberLiteral}  { return symbol(sym.NUMBER_LITERAL, yytext()); }
{StringLiteral}  { return symbol(sym.STRING_LITERAL, yytext()); }

/* Ignore Whitespace */
{Whitespace}     { /* ignore */ }

/* Fallback for invalid characters */
[^]              { throw new Error("Illegal character <"+yytext()+"> at line " + (yyline+1) + ", column " + (yycolumn+1)); }
