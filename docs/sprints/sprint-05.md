# Sprint 5

```text
Sprint: 5
Goal: Iniciar a V1.3 — o Event passa a ter ciclo de vida (DRAFT → PUBLISHED / CANCELLED) controlado por comportamento, com tipos de ingresso e regras bloqueando estados inválidos.
Version: V1.3 — Domínio e regras de negócio
Learning focus: comportamento no domínio, encapsulamento de estado, static vs instância, máquina de estados simples, dinheiro em centavos

Committed Issues:
#23 refactor: generate unique ids for Event and User (RN13)          — S
#24 feat: Event starts as DRAFT and exposes its status (RN03)        — S
#25 feat: configure ticket types for an event (RN07, RN08)           — M

Stretch:
#26 feat: publish and cancel events with state rules (RN04)          — M

Risks:
- #25 é o primeiro objeto guardando uma coleção de outros objetos (TicketType[] dentro de Event) sem Collections; expor esses dados sem quebrar o encapsulamento é o ponto mais difícil da sprint.
- Menu da CLI cresce (Configurar ingressos, Publicar, Cancelar); cada opção nova aumenta o custo de teste manual de regressão.
- Métodos de regra retornam boolean: a mensagem de erro fica genérica. Isso é esperado nesta fase (ver decisões).

Expected outcome:
Todo Event nasce DRAFT, tem id único e aceita tipos de ingresso só quando as regras permitem. Se o stretch entrar, eventos podem ser publicados (com ingresso) e cancelados, e a Main não contém nenhuma regra de transição.
```

## Decisões de sequenciamento da V1.3

| Regra | Onde entra | Por quê |
|---|---|---|
| RN03, RN04, RN07, RN08, RN13 | V1.3 — Sprint 5 (#23–#26) | Regras que o objeto consegue proteger por comportamento, retornando `boolean` |
| RN05, RN06/RN12, RN09, RN10, RN11 | V1.3 — Sprint 6 (#27, #28, a refinar) | Dependem de tipos de ingresso e do ciclo de vida do evento |
| RN01 (nome obrigatório), RN02 (organizador obrigatório) | V1.5 — Exceptions | Bloquear no construtor exige lançar exceção; sem ela, a regra ficaria na `Main`, contrariando o objetivo da V1.3 |
| RN14 (e-mail único) | V1.4 — Collections | Precisa de uma coleção de usuários para comparar |
| RN15 (isolamento do organizador) | A definir (V1.7) | Depende do conceito de "usuário atual" e de repositórios |

- **Status como constantes `String`** nesta fase; a troca por enum é explicitamente trabalho da V1.6.
- **Retorno `boolean`** nos métodos de regra; a limitação ("falhou, mas por quê?") é a motivação da V1.5.
- **Preço em centavos (inteiro)**, nunca `double`.
