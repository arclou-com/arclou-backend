# Security Policy

O Arclou (`arclou-event`) é atualmente um projeto educacional em estágio inicial (V1 — Java Core): aplicação Java local, sem rede, sem banco de dados, sem autenticação real e sem dados de usuários reais. O risco de segurança nesta fase é baixo por natureza.

## Escopo atual

Nesta versão não existem:
- API exposta publicamente;
- persistência de dados sensíveis;
- autenticação/autorização real;
- integrações externas.

Essas superfícies serão introduzidas gradualmente (V4 — Security, V5 — Payment) e esta política será expandida quando existirem.

## Reportando um problema

Se você encontrar uma vulnerabilidade ou prática insegura no código (mesmo em um projeto educacional, ex.: segredos commitados, injeção, manuseio inseguro de entrada), abra uma Issue privada ou entre em contato com o mantenedor do repositório em vez de abrir uma Issue pública detalhando o exploit.

## Segredos

Nunca commite tokens, senhas ou credenciais neste repositório, mesmo que o projeto seja local e educacional. Use variáveis de ambiente quando a necessidade surgir (a partir de V3+).
