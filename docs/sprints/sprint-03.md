# Sprint 3

```text
Sprint: 3
Goal: Iniciar a V1.2 — substituir o array primitivo de eventos por uma classe Event real, com encapsulamento.
Version: V1.2 — Orientação a Objetos
Learning focus: classe, objeto, atributo, construtor, encapsulamento, getters, this

Committed Issues:
#15 feat: model Event entity as a class
#16 refactor: replace primitive event array with Event[] in Main

Stretch:
(nenhum — a #16 depende diretamente da #15, não faz sentido puxar mais trabalho antes de fechar essas duas)

Risks:
- Primeiro contato real com classes própria do projeto (não são exercícios soltos) — pode levar mais tempo que o Size sugere.
- #16 é um refactor que toca as 3 features já existentes (criar/listar/buscar); qualquer regressão ali afeta tudo que já funciona.

Expected outcome:
`Event` existe como classe com atributos encapsulados (nome, data, local), construtor e getters. `Main` armazena `Event[]` em vez de `String[][]`, e criar/listar/buscar continuam funcionando exatamente como antes — mas o domínio começa a sair da estrutura primitiva.
```
