# ADR-0003 — Produtora e conta única

## Status
Accepted — 2026-10-06

## Context
A V1 modela quem organiza eventos como um usuário com papel `ORGANIZER`, e a RN15 diz que "um organizador só gerencia os próprios eventos".

O design do produto final (ver `docs/produto-e-design.md`) mostra outra realidade:

- Eventos, repasses, conta bancária, página pública (`/o/<slug>`) e equipe pertencem à **produtora** (ex.: "Arclou Produções"), não a uma pessoa.
- A equipe tem papéis próprios: **Proprietário, Administrador, Financeiro, Check-in** (CF-A5, RN33).
- A mesma pessoa pode **comprar ingressos** no site e **trabalhar** numa produtora no app — nos dados de exemplo, Ana Souza é compradora do pedido #1042 e Administradora da Arclou Produções.

Alternativas consideradas:

1. Manter `Event.organizer = User` e tratar a equipe como ajudantes desse usuário.
2. Introduzir a produtora como entidade, com vínculo de membros e papéis, e unificar as contas.

E, para a alternativa 2, em que versão introduzi-la: já na V2 (primeira API) ou na V4 (autenticação e autorização).

## Decision

**Produtora como entidade, com vínculo de membros, e conta única:**

```text
User ──< Membership (papel) >── Organization (Produtora)
                                   ├── Events
                                   ├── conta bancária e repasses
                                   ├── página pública /o/<slug>
                                   └── configurações e padrões de novos eventos
```

- Papéis do vínculo: `OWNER` (exatamente um por produtora), `ADMIN`, `FINANCE`, `CHECK_IN`.
- **Conta única:** qualquer conta compra ingressos. "Organizador" deixa de ser um papel do usuário e passa a ser quem tem vínculo com uma produtora. O papel de plataforma fica restrito a `SUPER_ADMIN`.
- Cadastro pelo app cria a conta **e** a produtora (a pessoa vira `OWNER`); cadastro pelo site cria só a conta. A mesma conta funciona nas duas superfícies (RN34).
- **RN15 passa a ser:** um usuário só gerencia eventos das produtoras de que é membro, dentro do que o papel permite.
- A interface trabalha com **uma produtora por conta** (é o que está desenhado). O modelo aceita várias, para quando houver demanda.

**Versão: V4.** V1 a V3 mantêm `Event.organizer = User` e `UserRole` como estão na spec. Na V4, junto com autenticação e autorização, entram `Organization` e `Membership`; uma migration cria uma produtora para cada organizador existente, com ele como `OWNER`.

## Consequences

Positivas:
- O domínio passa a representar quem realmente é dono do dinheiro e dos eventos; trocar de pessoa responsável não exige mexer nos eventos.
- Permissões da equipe (CF-A5, check-in) viram regras de autorização claras na V4.
- A V1 e a Sprint 5 não mudam.

Negativas / pontos de atenção:
- **V4:** migração de dados (V3 já persiste `organizer_id` nos eventos) e mudança de `UserRole` — é parte do aprendizado de migrations e autorização.
- **V2/V3:** a API nasce com eventos ligados a um usuário; os contratos da V2 devem evitar expor esse detalhe de forma que dificulte a troca na V4 (ex.: rotas por evento, não por usuário).
- Pagamentos (V5) e repasses passam a ser da produtora, não do usuário.

O detalhamento de campos por versão está em [`docs/modelo-de-dominio.md`](../modelo-de-dominio.md).
