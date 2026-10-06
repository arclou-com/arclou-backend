# ADR-0001 — Superfícies do produto: app mobile do organizador e web responsivo do participante

## Status
Accepted — 2026-10-06

## Context
O Arclou tem dois públicos com necessidades diferentes:

- **Organizadores** criam e acompanham eventos, pedidos, repasses e equipe. Usam o produto com frequência, muitas vezes longe do computador (no local do evento, no dia do check-in).
- **Participantes** chegam por um link compartilhado (`arclou.com/e/<slug>`) — em redes sociais, WhatsApp ou e-mail — e compram ingressos uma vez por evento, a partir de qualquer aparelho, sem querer instalar nada.

O design inicial previa Desktop, Tablet e Mobile para todas as telas do organizador. Manter três layouts para um público que trabalha majoritariamente no celular aumentava o custo de design e de implementação sem ganho real. Para o participante, a situação é a oposta: a página do evento precisa abrir bem em qualquer tela, inclusive para quem nunca usou o Arclou.

Alternativas consideradas:

1. Tudo responsivo na web (organizador e participante).
2. Um único app mobile com "modo organizador" e "modo participante".
3. App mobile para o organizador e site web responsivo para o participante.

## Decision
Adotar a alternativa 3:

- **App do organizador:** mobile-only, tema escuro, navegação por Tab bar (Painel, Eventos, Novo evento, Pedidos, Mais).
- **Site do participante:** web responsivo (Desktop, Tablet, Mobile), acessível sem instalação; conta necessária apenas para comprar e ver ingressos.
- **Administração da plataforma (`SUPER_ADMIN`):** fora do escopo de design por enquanto.

O mapeamento completo de telas está em [`docs/produto-e-design.md`](../produto-e-design.md).

## Consequences

Positivas:
- Um layout por tela no app do organizador: menos telas para desenhar, implementar e testar.
- O link público funciona em qualquer aparelho, que é como o participante realmente chega ao evento.
- As decisões de UX ficam mais simples: no celular, ações como Pix copia-e-cola e "abrir app autenticador" vêm antes do QR code, porque o aparelho não escaneia a própria tela.

Negativas / pontos de atenção por versão:
- **V2 (API):** a mesma API REST atende dois clientes diferentes (app e web). Os contratos precisam ser pensados para ambos desde o início.
- **V4 (Security):** dois fluxos de autenticação — sessão/token no app mobile e sessão na web; sessões ativas passam a ser listadas por aparelho.
- **V5 (Payment):** o checkout acontece na web; o app do organizador só consome o resultado (pedidos, repasses).
- **V6 (Kafka/notificações):** notificações push existem só para o app; o participante recebe e-mail.
- Tarefas pesadas do organizador (exportar relatórios, integrações) precisam caber no celular — por isso aparecem como menus e bottom sheets, nunca como telas exclusivas de desktop.
