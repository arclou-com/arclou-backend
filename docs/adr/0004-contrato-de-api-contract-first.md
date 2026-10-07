# ADR-0004 — Contrato de API contract-first

## Status
Proposed — 2026-10-07

## Context
O app do organizador está 100% desenhado e prototipado (115 telas, ver `docs/produto-e-design.md`). A API só começa a existir na V2, e várias partes dela só chegam nas versões seguintes (autenticação na V4, pagamentos na V5, notificações na V6, check-in offline na V7).

O app (front) e o backend precisam de um acordo único sobre recursos, campos e erros **antes** de o backend existir — senão o app é desenhado contra suposições e o backend descobre o formato só na integração.

Alternativas consideradas:

1. **Code-first:** escrever os controllers da V2 e gerar o OpenAPI a partir do código (springdoc).
2. **Contract-first:** escrever o OpenAPI agora, a partir das telas, e implementar o backend contra ele.
3. Documentar endpoints em markdown, sem especificação formal.

## Decision

**Contract-first, com OpenAPI 3.1** em [`docs/api/openapi.yaml`](../api/openapi.yaml), guia em [`docs/api/README.md`](../api/README.md).

- O contrato descreve o **produto final**. Cada operação diz em que versão entra (`x-since`) e que papéis podem chamá-la (`x-roles`).
- Convenções fixas: JSON `camelCase`, dinheiro como `Money` em centavos, datas ISO-8601, paginação por cursor, erros RFC 9457 com o código da regra (`rule: RN04`), transições de estado como `POST` em sub-recursos (`/publish`, `/cancel`), `Idempotency-Key` em ações com dinheiro.
- As rotas já nascem por **produtora** (`/organizations/{id}/…`) e por recurso (`/events/{id}`). Na V2/V3, antes de `Organization` existir (ADR-0003), o id da produtora é derivado do usuário organizador; na V4, a migration cria a produtora com o mesmo id.
- Escopo deste contrato: o **app do organizador**. O site do participante terá contrato próprio, com as mesmas convenções.
- A CI valida o contrato (Redocly lint) em todo PR.

Code-first foi descartado porque o app precisa do contrato agora, quatro versões antes de parte do backend existir. Markdown solto foi descartado porque não gera mock, cliente nem validação.

## Consequences

Positivas:
- O app pode ser construído inteiro contra um mock gerado do contrato.
- Cada Issue de backend a partir da V2 tem formato de entrada e saída definido; o review compara o endpoint com o contrato.
- Erros com `code` e `rule` ligam o que o usuário vê às regras RN do produto.

Negativas / pontos de atenção:
- O contrato pode envelhecer em relação ao código. Mitigação: na V2, testes de integração validam as respostas contra o `openapi.yaml`.
- Mudanças de tela passam a exigir mudança no contrato no mesmo PR.
- O dev escreve os controllers da V2 seguindo um formato pronto — o aprendizado de **desenho** de API acontece no review das mudanças do contrato, não do zero.
- Mudança que quebra o app exige ADR e `/v2` na URL.
