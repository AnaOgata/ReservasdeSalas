# 📚 Reserva de Salas de Estudo

Sistema de reserva de salas para campus universitário, desenvolvido em Java com padrões de projeto.

## 👩‍💻 Autores

- Ana Clara Ogata - Noturno
- Davi Gonçalves Friggi - Noturno

## 🧩 Padrões de Projeto Aplicados

- **Factory Method** — criação de diferentes tipos de sala
- **Singleton** — repositório e configuração únicos na memória
- **Strategy** — políticas de detecção de colisão de horários
- **Observer** — notificações automáticas de alterações de reserva
- **Decorator** _(bônus)_ — funcionalidades extras nas reservas

## 📋 Requisitos Funcionais

| Código | Descrição                                                  |
| ------ | ---------------------------------------------------------- |
| RF-01  | Listar salas disponíveis em um intervalo de datas          |
| RF-02  | Criar, modificar ou cancelar uma reserva                   |
| RF-03  | Detectar e impedir colisões de horário                     |
| RF-04  | Notificar envolvidos quando reserva for alterada/cancelada |
| RF-05  | Relatório diário com reservas confirmadas por sala         |

## 🗂️ Estrutura do Projeto

```
room-booking/
├── src/
│   └── com/roombooking/
│       ├── model/          # Entidades do domínio
│       ├── factory/        # Factory Method para tipos de sala
│       ├── strategy/       # Políticas de reserva
│       ├── observer/       # Notificações e relatórios
│       ├── singleton/      # Repositórios e configuração
│       ├── decorator/      # Funcionalidades extras
│       └── Main.java       # Ponto de entrada
├── docs/
│   └── diagrama.png
└── README.md
```

## 🗂️ Diagrama de Classes

### Padrões representados no diagrama

- **Factory Method** — `SalaFactory` (interface) com as três implementações
  concretas (`SalaIndividual`, `SalaGrupo`, `SalaLaboratorio`), herdando de `Sala` abstrata
- **Singleton** — `RepositorioReservas` e `ConfiguracaoSistema` com estereótipo `«singleton»`
- **Strategy** — `PoliticaDeReserva` (interface) com `PrimeiroAReservar` e
  `PrioridadeDocente`, conectada ao `GerenciadorReservas` por dependência tracejada
- **Observer** — interfaces `Observador` e `Notificavel`, com os três observadores
  concretos: `NotificadorEmail`, `NotificadorSistema` e `ServicoRelatorio` (RF-04 e RF-05)
- **Decorator** _(bônus)_ — `ReservaDecorator` abstrata com `ComMultimidia` e
  `ComLimpeza`, com a relação de agregação `wraps → Reserva`

## ▶️ Como executar

```bash
# Compilar
javac -d out src/com/roombooking/**/*.java src/com/roombooking/Main.java

# Executar
java -cp out com.roombooking.Main
```
