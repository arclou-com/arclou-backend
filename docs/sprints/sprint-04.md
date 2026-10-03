# Sprint 4

```text
Sprint: 4
Goal: Fechar a V1.2 — introduzir User como classe e associar Event ao seu organizador.
Version: V1.2 — Orientação a Objetos
Learning focus: classe, composição/associação, construtor, encapsulamento

Committed Issues:
#19 feat: model User entity as a class
#20 feat: associate Event with its organizer (User)

Stretch:
(nenhum — a #20 depende diretamente da #19, e fecha a V1.2; não vale puxar V1.3 antes disso)

Risks:
- #20 introduz um novo passo no fluxo de "Criar evento" (selecionar/criar organizador) — pode mudar a UX do menu o suficiente pra exigir mais testes manuais de regressão nas 3 features já existentes.
- Ainda não existe RN02 validado de verdade (organizador obrigatório é regra de negócio, V1.3) — nesta sprint só criamos a associação, não a validação.

Expected outcome:
`User` existe como classe (id, name, email). `Event` guarda uma referência ao `User` que o organizou (composição/associação real, não FK/id solto). Criar, listar e buscar evento continuam funcionando, agora exibindo o organizador. V1.2 fecha; RN02 fica pronta pra ser validada na V1.3.
```

## Review

- #19 e #20 entregues (PRs #21 e #22). V1.2 concluída: `Event` guarda o `User` organizador por composição.
- #22 passou por 1 ciclo de *changes requested*: nome do atributo (`user` → `organizer`, critério de aceite) e id fixo do `User`.

## Retrospective

> Pontos observados pelo Tech Lead. O desenvolvedor pode complementar/ajustar via PR.

- **What went well?** Composição feita com objeto (não id solto); `toString()` com `@Override` resolveu a duplicação de exibição.
- **What was difficult?** Nomear pelo papel no domínio (`organizer`) em vez do tipo (`user`).
- **What did we learn?** O nome do atributo documenta a relação; o critério de aceite precisa ser seguido à risca ou discutido antes.
- **Where did estimates differ from reality?** #20 (M) exigiu um ciclo extra de review.
- **What process should change?** Criar a branch antes de começar a task (o commit da #20 nasceu na `main` local); marcar "N/A" no checklist quando não houver testes; conferir se o `Closes #N` foi reconhecido pelo GitHub.
- **What technical debt was created?** Id do `User` derivado da posição do evento no array → endereçado pela #23 (RN13).
