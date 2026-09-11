# Contributing

Este repositório é um ambiente de aprendizado de engenharia backend (ver `CLAUDE.md`). O fluxo abaixo vale mesmo sendo um projeto solo — é assim que times reais trabalham.

## Fluxo de trabalho

1. Toda mudança começa em uma **Issue** no GitHub Project `Arclou Engineering`.
2. Crie uma branch curta a partir de `main`:

   ```text
   feature/<issue>-<descricao>
   fix/<issue>-<descricao>
   refactor/<issue>-<descricao>
   test/<issue>-<descricao>
   docs/<issue>-<descricao>
   chore/<issue>-<descricao>
   ```

   Exemplo: `feature/12-create-event-menu-option`

3. Implemente, seguindo os critérios de aceitação da Issue.
4. Escreva/atualize os testes exigidos pela Issue.
5. Commits no estilo [Conventional Commits](https://www.conventionalcommits.org/):

   ```text
   feat: add event creation menu option
   fix: prevent negative ticket quantity
   refactor: extract menu loop into method
   test: cover event creation
   docs: document V1.2 rules
   chore: configure gitignore
   ```

6. Abra um Pull Request para `main` usando o template. Preencha `Closes #<issue>`.
7. Aguarde o code review do Tech Lead (Claude Code). Responda a categorias `[BLOCKER]` e `[MAJOR]` antes de pedir novo review.
8. Após aprovação e CI verde, faça squash merge.

## Regra central do projeto

> Não introduzir tecnologia antes de existir um problema concreto que a justifique.

Se uma Issue não pede Spring, banco de dados, Kafka, Redis ou Docker, não os introduza "só para adiantar".

## Rodando o projeto localmente

```bash
mvn compile
mvn test
```

(Comandos evoluem conforme o projeto ganha testes e, futuramente, um `exec` configurado.)
