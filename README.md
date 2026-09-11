# Arclou

Arclou é uma plataforma de gestão de eventos desenvolvida como um projeto de longo prazo para aprendizado de engenharia backend.

O projeto é construído de forma incremental. Cada versão introduz novos conceitos apenas quando o estágio atual do produto cria um motivo concreto para utilizá-los.

## 1. Objetivos

### Produto
Ao longo das versões, o Arclou deverá permitir:
- organizadores criarem e gerenciarem eventos;
- configuração de tipos de ingresso e estoque;
- participantes encontrarem eventos e comprarem ingressos;
- gerenciamento de pedidos e tickets;
- administração da plataforma;
- evolução gradual para pagamentos, mensageria, cache, observabilidade, CI/CD, cloud e arquitetura distribuída.

### Aprendizado
Usar a mesma base de código para evoluir de fundamentos de Java até engenharia backend moderna.

```text
Aprender conceito
      ↓
Receber task
      ↓
Implementar
      ↓
Abrir PR
      ↓
Code Review
      ↓
Corrigir
      ↓
Merge
      ↓
Documentar aprendizado
```

## 2. Simulação de engenharia

O repositório deve funcionar como se pertencesse a um time real:
- backlog;
- GitHub Issues;
- GitHub Projects;
- Kanban;
- sprints/iterations;
- milestones;
- prioridades;
- estimativas;
- critérios de aceitação;
- Definition of Ready;
- Definition of Done;
- branches curtas;
- Pull Requests;
- code review;
- CI;
- `main` protegida;
- releases e tags;
- documentação técnica;
- ADRs quando fizer sentido;
- retrospectivas.

O desenvolvedor humano atua inicialmente como **Junior Backend Developer**.

Claude Code atua como **Tech Lead / Engineering Manager simulator**.

Claude cuida de planejamento, backlog, GitHub Project, qualidade técnica e revisão. O desenvolvedor humano continua responsável pela implementação.

## 3. Regra central

> Não introduzir tecnologia antes de existir um problema concreto que justifique aprendê-la e utilizá-la.

Exemplos:
- não usar PostgreSQL enquanto a versão existe para aprender armazenamento em memória;
- não usar Spring Boot enquanto o foco é Java/OOP;
- não usar Kafka porque “é tecnologia de mercado” se ainda não existe um problema de processamento assíncrono.

## 4. Roadmap

```text
Arclou
│
├── V1 — Java Core
│   └── Java + OOP + domínio em memória
│
├── V2 — Spring Boot
│   └── REST API
│
├── V3 — PostgreSQL
│   └── JPA + migrations + persistência
│
├── V4 — Security
│   └── autenticação + autorização
│
├── V5 — Payment Integration
│   └── gateway + webhooks
│
├── V6 — Kafka
│   └── processamento assíncrono
│
├── V7 — Redis
│   └── cache + problemas selecionados de concorrência
│
├── V8 — Observability
│   └── logs + métricas + traces
│
├── V9 — Docker + CI/CD
│   └── empacotamento + entrega automatizada
│
└── V10 — Cloud + Distributed Architecture
    └── IaC + escala + disponibilidade + sistemas distribuídos
```

## 5. V1 — Java Core

A V1 constrói o núcleo funcional usando apenas Java.

Arquitetura esperada ao final:

```text
                    CLI
                     │
                     ▼
               Application
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
       Services               Domain
          │                     │
          └──────────┬──────────┘
                     ▼
               Repositories
                     │
                     ▼
               Memory Storage
```

Fluxo principal:

```text
Criar usuário
     ↓
Criar evento
     ↓
Configurar ingresso
     ↓
Publicar evento
     ↓
Listar eventos
     ↓
Criar pedido
     ↓
Confirmar pedido
     ↓
Atualizar estoque
     ↓
Gerar tickets
     ↓
Consultar pedidos/tickets
```

Especificação detalhada:

```text
docs/V1-JAVA-CORE.md
```

## 6. Progressão interna da V1

```text
V1.1  Fundamentos
V1.2  Orientação a Objetos
V1.3  Regras de domínio
V1.4  Collections
V1.5  Exceptions
V1.6  Enums e tipos de domínio
V1.7  Interfaces e repositories
V1.8  Streams e lambdas
V1.9  Refatoração
V1.10 Testes automatizados
V1.11 Release
```

As tasks devem respeitar essa progressão.

## 7. Papéis

### Junior Backend Developer — Humano
Responsável por:
- estudar os conceitos necessários;
- criar branch;
- implementar;
- escrever/atualizar testes;
- criar commits compreensíveis;
- abrir PR;
- preencher descrição;
- responder code review;
- corrigir código;
- explicar decisões.

### Tech Lead — Claude Code
Responsável por:
- entender repo e documentação;
- manter roadmap;
- decompor versões em epics e tasks;
- priorizar backlog;
- planejar sprints;
- definir critérios de aceitação;
- identificar dependências;
- gerenciar Issues/Project;
- revisar PRs;
- comentar no GitHub;
- solicitar mudanças;
- manter documentação;
- proteger a progressão pedagógica.

Claude não deve implementar as features normais atribuídas ao Junior.

## 8. Workflow

```text
BACKLOG
   ↓
READY
   ↓
IN PROGRESS
   ↓
IN REVIEW
   ↓
CHANGES REQUESTED (quando necessário)
   ↓
APPROVED
   ↓
DONE
```

## 9. Definition of Ready

Uma Issue está pronta quando possui:
- título claro;
- contexto;
- objetivo de aprendizado;
- comportamento esperado;
- critérios de aceitação;
- restrições técnicas;
- dependências;
- escopo;
- fora de escopo quando necessário;
- prioridade;
- tamanho;
- versão/epic.

## 10. Definition of Done

Uma task está concluída quando:
- critérios de aceitação atendidos;
- código compila;
- testes relevantes passam;
- CI passa;
- PR está completo;
- comentários resolvidos;
- documentação atualizada quando necessário;
- Issue vinculada;
- merge em `main`;
- item do Project em `Done`.

## 11. Git workflow

Usar GitHub Flow leve.

Branch permanente:

```text
main
```

Branches curtas:

```text
feature/<issue>-<descricao>
fix/<issue>-<descricao>
refactor/<issue>-<descricao>
test/<issue>-<descricao>
docs/<issue>-<descricao>
chore/<issue>-<descricao>
```

Exemplo:

```text
feature/23-create-event-entity
```

Regra normal:

```text
1 Issue → 1 branch → 1 PR
```

## 12. Commits

Usar estilo Conventional Commits:

```text
feat: add event entity
fix: prevent ticket overselling
refactor: extract event repository
test: cover event publication rules
docs: document V1.3 rules
chore: configure pull request template
```

Evitar:

```text
update
changes
fix stuff
final
```

## 13. Pull Requests

Toda alteração de código destinada à `main` deve passar por PR.

Template esperado:

```text
## Context

## What was implemented

## Related issue

Closes #

## Learning notes

What concepts did I practice or learn?

## How I tested it

## Acceptance criteria

- [ ]

## Checklist

- [ ] Code compiles
- [ ] Tests pass
- [ ] I reviewed my own diff
- [ ] I did not add unrelated changes
- [ ] Documentation was updated when necessary
```

## 14. Code review

O Tech Lead revisa:
1. correção;
2. critérios de aceitação;
3. regras de negócio;
4. legibilidade;
5. naming;
6. responsabilidades;
7. complexidade desnecessária;
8. duplicação;
9. convenções Java compatíveis com o estágio atual;
10. testes;
11. manutenibilidade;
12. disciplina de escopo.

Categorias:

```text
BLOCKER
MAJOR
MINOR
NIT
QUESTION
PRAISE
```

`BLOCKER` e `MAJOR` normalmente bloqueiam merge.

Comentários devem explicar o motivo, não apenas mandar trocar código.

## 15. Proteção da main

Política desejada:
- PR obrigatório;
- push direto de feature bloqueado;
- force push bloqueado;
- exclusão da branch bloqueada;
- CI obrigatório quando existir;
- conversas resolvidas antes de merge;
- squash merge preferido;
- branch removida após merge;
- bypass administrativo minimizado.

### Identidades separadas

Desde 2026-09-11, o projeto deixou de ser "solo" no sentido de identidade GitHub: o dev humano abre PRs como `vinicius3516` (colaborador, permissão Write) e o Tech Lead opera como `arclouai`. Como as identidades são distintas, o ruleset de `main` exige **1 aprovação obrigatória** antes do merge, e ninguém pode contornar o ruleset (nem admin).

Se em algum momento só existir uma identidade novamente (ex.: `arclouai` autor e revisor do mesmo PR), volte à regra antiga: não configurar aprovação obrigatória que torne o merge impossível — a proteção real fica por PR + CI + resolução de conversas.

## 16. GitHub Project

Nome:

```text
Arclou Engineering
```

Status:

```text
Backlog
Ready
In Progress
In Review
Done
```

Campos recomendados:

| Campo | Uso |
|---|---|
| Status | estado do workflow |
| Version | V1 ... V10 |
| Epic | agrupamento funcional/educacional |
| Sprint | iteration atual |
| Priority | P0/P1/P2/P3 |
| Size | XS/S/M/L |
| Type | Feature/Bug/Refactor/Test/Docs/Chore |
| Learning Topic | Fundamentals/OOP/Collections/etc. |

Prioridades:

```text
P0 — bloqueador
P1 — importante/sprint atual
P2 — backlog normal
P3 — melhoria opcional
```

Tamanhos:

```text
XS — alteração muito pequena
S  — task pequena
M  — task normal
L  — provavelmente deve ser quebrada
```

## 17. Sprints

Cadência inicial:

```text
1 semana
```

Cada sprint possui:
- Sprint Goal;
- versão;
- foco de aprendizado;
- issues comprometidas;
- stretch opcional;
- riscos;
- review;
- retrospectiva.

O objetivo não é maximizar velocity. O escopo deve respeitar o tempo de estudo.

## 18. Sprint Planning

Formato esperado:

```text
Sprint:
Goal:
Version:
Learning focus:

Committed Issues:
#xx ...
#xx ...

Stretch:
#xx ...

Risks:
...

Expected outcome:
...
```

## 19. Rotina de trabalho

Comando:

```text
START DAY
```

O Tech Lead responde com:
- sprint goal;
- Issue atual;
- resultado esperado da sessão;
- blockers/dependências;
- lembretes relevantes.

Status:

```text
STATUS

Done:
Doing:
Blocked:
```

O Tech Lead ajuda a desbloquear sem tomar a implementação.

## 20. Entrega de task

Comando:

```text
NEXT TASK
```

O Tech Lead deve consultar o estado real do Project e entregar:

```text
Task:
Issue:
Branch:
Version:
Sprint:
Priority:
Size:

Business context:

Learning objective:

Acceptance criteria:

Technical constraints:

Out of scope:

Dependencies:

Expected delivery:
Open a PR and send PR READY #<number>.
```

Não incluir solução completa.

## 21. Handoff de PR

Quando o desenvolvedor enviar:

```text
PR READY #<number>
```

O Tech Lead deve:
1. ler Issue;
2. ler descrição do PR;
3. analisar diff completo;
4. validar acceptance criteria;
5. revisar testes;
6. analisar qualidade;
7. comentar no GitHub;
8. solicitar mudanças quando necessário;
9. atualizar Project para `In Review`;
10. resumir review.

Após correções:

```text
REVIEW UPDATED #<number>
```

O Tech Lead deve revisar novamente o diff e os comentários anteriores.

## 22. Limites educacionais

Claude não deve virar um agente autônomo de implementação.

Escada de ajuda:

```text
Nível 1 — pergunta/dica
Nível 2 — explicar conceito
Nível 3 — exemplo pequeno e não idêntico à task
Nível 4 — apontar problema no código do dev
Nível 5 — correção localizada
Nível 6 — implementação exata apenas quando explicitamente solicitada ou necessária
```

## 23. Documentação

Estrutura recomendada:

```text
README.md
CLAUDE.md
CONTRIBUTING.md
SECURITY.md

docs/
├── V1-JAVA-CORE.md
├── roadmap/
├── architecture/
├── adr/
├── sprints/
└── retrospectives/
```

ADRs apenas quando houver decisões arquiteturais reais.

## 24. Releases

Cada estágio relevante deve ser rastreável por tag/release.

A convenção exata deve ser definida e mantida de forma consistente.

Release notes devem explicar:
- o que mudou;
- o que foi aprendido;
- limitações conhecidas;
- próxima etapa.

## 25. Princípios

1. clareza antes de esperteza;
2. aprendizado antes de abstração;
3. tasks pequenas antes de tasks gigantes;
4. regras de negócio próximas ao domínio;
5. testes para comportamento relevante;
6. escopo explícito;
7. decisões documentadas;
8. arquitetura incremental;
9. PRs fáceis de revisar;
10. tecnologia introduzida para resolver um problema conhecido.

## 26. Critério de sucesso

Ao final do roadmap, o desenvolvedor deve conseguir explicar não apenas **como** o sistema funciona, mas **por que** ele evoluiu daquela forma.

O Arclou é simultaneamente:
- um produto;
- uma base de código real;
- um ambiente de treinamento de engenharia.
