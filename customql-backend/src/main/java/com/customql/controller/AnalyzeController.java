package com.customql.controller;

import com.customql.CustomLexer;
import com.customql.CustomParser;
import com.customql.ast.AstNode;
import java_cup.runtime.Symbol;
import java_cup.runtime.Scanner;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // For development with React
public class AnalyzeController {

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestBody Map<String, String> payload) {
        String queryStr = payload.get("query");
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> tokensList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (queryStr == null || queryStr.isEmpty()) {
            response.put("errors", List.of("Query is empty"));
            return ResponseEntity.badRequest().body(response);
        }

        // 1. Lexical Analysis (collect tokens for frontend)
        try {
            CustomLexer lexerForTokens = new CustomLexer(new StringReader(queryStr));
            while (true) {
                Symbol sym = lexerForTokens.next_token();
                if (sym.sym == com.customql.sym.EOF) {
                    break;
                }
                Map<String, Object> tInfo = new HashMap<>();
                tInfo.put("type", getSymbolName(sym.sym));
                tInfo.put("value", sym.value);
                tInfo.put("line", sym.left + 1);
                tInfo.put("column", sym.right + 1);
                tokensList.add(tInfo);
            }
        } catch (Exception e) {
            errors.add("Lexical error: " + e.getMessage());
        }

        // 2. Syntactic Analysis
        AstNode ast = null;
        try {
            CustomLexer lexerForParser = new CustomLexer(new StringReader(queryStr));
            CustomParser parser = new CustomParser(lexerForParser);
            Symbol result = parser.parse();
            if (result != null && result.value instanceof AstNode) {
                ast = (AstNode) result.value;
            }
        } catch (Exception e) {
            errors.add("Syntactic error: " + e.getMessage());
        }

        response.put("tokens", tokensList);
        response.put("ast", ast);
        response.put("errors", errors);

        return ResponseEntity.ok(response);
    }

    // Helper to map symbol ID to name for the frontend
    private String getSymbolName(int symId) {
        // This is a rough mapping; in real scenarios we might use reflection on sym.class
        switch(symId) {
            case com.customql.sym.GET: return "GET";
            case com.customql.sym.WHERE: return "WHERE";
            case com.customql.sym.AND: return "AND";
            case com.customql.sym.OR: return "OR";
            case com.customql.sym.SORT: return "SORT";
            case com.customql.sym.BY: return "BY";
            case com.customql.sym.ASC: return "ASC";
            case com.customql.sym.DESC: return "DESC";
            case com.customql.sym.SEMI: return "SEMI";
            case com.customql.sym.GT: return "GT";
            case com.customql.sym.LT: return "LT";
            case com.customql.sym.EQ: return "EQ";
            case com.customql.sym.GTE: return "GTE";
            case com.customql.sym.LTE: return "LTE";
            case com.customql.sym.NEQ: return "NEQ";
            case com.customql.sym.IDENTIFIER: return "IDENTIFIER";
            case com.customql.sym.NUMBER_LITERAL: return "NUMBER_LITERAL";
            case com.customql.sym.STRING_LITERAL: return "STRING_LITERAL";
            default: return "UNKNOWN (" + symId + ")";
        }
    }
}
