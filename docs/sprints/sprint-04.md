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
