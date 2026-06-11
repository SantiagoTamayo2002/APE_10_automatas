import React, { useState } from 'react';
import axios from 'axios';
import Tree from 'react-d3-tree';

function App() {
  const [query, setQuery] = useState('GET users WHERE age > 18 AND status = "active" SORT BY name ASC;');
  const [tokens, setTokens] = useState([]);
  const [ast, setAst] = useState(null);
  const [errors, setErrors] = useState([]);

  const analyzeQuery = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/analyze', { query });
      setTokens(response.data.tokens || []);
      setAst(response.data.ast);
      setErrors(response.data.errors || []);
    } catch (err) {
      setErrors([err.message || 'Error connecting to backend']);
      setTokens([]);
      setAst(null);
    }
  };

  // Convert Java AST structure to react-d3-tree structure
  const formatAstForTree = (node) => {
    if (!node) return null;
    
    let result = { name: node.type || 'Unknown' };

    let children = [];

    switch (node.type) {
      case 'Query':
        children.push({ name: `Target: ${node.target}` });
        if (node.whereClause) children.push(formatAstForTree(node.whereClause));
        if (node.sortClause) children.push(formatAstForTree(node.sortClause));
        break;
      case 'OR':
      case 'AND':
        if (node.left) children.push(formatAstForTree(node.left));
        if (node.right) children.push(formatAstForTree(node.right));
        break;
      case 'Condition':
        children.push({ name: `Ident: ${node.identifier}` });
        children.push({ name: `Op: ${node.operator}` });
        children.push({ name: `Val: ${node.value}` });
        break;
      case 'Sort':
        children.push({ name: `By: ${node.identifier}` });
        children.push({ name: `Dir: ${node.direction}` });
        break;
      default:
        break;
    }

    if (children.length > 0) {
      result.children = children;
    }

    return result;
  };

  const treeData = ast ? formatAstForTree(ast) : null;

  return (
    <div className="app-container">
      <header className="header">
        <h1>CustomQL Explorer</h1>
      </header>

      <section className="glass-panel editor-area">
        <textarea
          className="query-input"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Enter CustomQL query here..."
        />
        <button className="analyze-btn" onClick={analyzeQuery}>
          Analizar
        </button>
      </section>

      {errors.length > 0 && (
        <div className="error-banner">
          <strong>Errors:</strong>
          <ul>
            {errors.map((err, i) => (
              <li key={i}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      <div className="results-grid">
        <section className="glass-panel">
          <h2>Tokens Léxicos</h2>
          <div className="tokens-table-container">
            <table className="tokens-table">
              <thead>
                <tr>
                  <th>Token</th>
                  <th>Tipo</th>
                  <th>Línea</th>
                  <th>Col.</th>
                </tr>
              </thead>
              <tbody>
                {tokens.map((t, idx) => (
                  <tr key={idx}>
                    <td>{t.value || t.type}</td>
                    <td>{t.type}</td>
                    <td>{t.line}</td>
                    <td>{t.column}</td>
                  </tr>
                ))}
                {tokens.length === 0 && (
                  <tr>
                    <td colSpan="4">No tokens available.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="glass-panel">
          <h2>Árbol de Sintaxis Abstracta</h2>
          <div className="ast-container">
            {treeData ? (
              <Tree 
                data={treeData} 
                orientation="vertical"
                pathFunc="step"
                translate={{ x: 250, y: 50 }}
                nodeSize={{ x: 150, y: 100 }}
              />
            ) : (
              <div style={{ padding: '2rem', textAlign: 'center', color: '#888' }}>
                No AST to display
              </div>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

export default App;
