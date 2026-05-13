# Funcionalidade Adicional — Histórico de Reservas por Usuário

## Descrição

Permite que cada usuário consulte suas reservas passadas (confirmadas e canceladas).
Administradores podem consultar o histórico de qualquer usuário.

## Padrão Aplicado: Proxy

### Justificativa

O padrão Proxy foi escolhido porque o acesso ao histórico tem duas
responsabilidades extras além da consulta simples:

- **Controle de acesso**: usuários só veem o próprio histórico; admins veem todos.
- **Cache**: evita consultas repetidas ao repositório para o mesmo usuário.

O Proxy encapsula essas responsabilidades sem alterar a interface nem o
objeto real (HistoricoReservasReal), respeitando o Princípio Aberto/Fechado.

## Estrutura

<<interface>>
HistoricoReservas

- buscarHistorico(solicitante, alvo): List<EntradaHistorico>
- registrar(entrada): void
  ▲ ▲
  | |
  HistoricoReservasReal HistoricoReservasProxy
  (acesso direto) (cache + controle de acesso)
  └── delega para Real

## Como Testar

1. Execute Main.java
2. Observe no console:
   - [REAL] → consulta ao repositório (primeira vez)
   - [PROXY] Cache hit → segunda consulta sem bater no repositório
   - [PROXY] Acesso negado → usuário sem permissão
