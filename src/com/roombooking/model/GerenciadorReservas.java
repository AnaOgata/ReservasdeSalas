package com.roombooking.model;

import com.roombooking.observer.Notificavel;
import com.roombooking.observer.Observador;
import com.roombooking.singleton.RepositorioReservas;
import com.roombooking.strategy.PoliticaDeReserva;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
private final HistoricoReservas historico = new HistoricoReservasProxy();

public class GerenciadorReservas implements Notificavel {

    private PoliticaDeReserva politica;
    private final List<Observador> observadores = new ArrayList<>();
    private final RepositorioReservas repositorio = RepositorioReservas.getInstance();

    public GerenciadorReservas(PoliticaDeReserva politica) {
        this.politica = politica;
        System.out.println("📌 Política ativa: " + politica.getDescricao());
    }

    // Permite trocar a política em tempo de execução (Strategy)
    public void setPolitica(PoliticaDeReserva politica) {
        this.politica = politica;
        System.out.println("🔄 Política alterada para: " + politica.getDescricao());
    }

    // ── Observer ──────────────────────────────────────────
    @Override
    public void adicionarObservador(Observador obs) { observadores.add(obs); }

    @Override
    public void removerObservador(Observador obs) { observadores.remove(obs); }

    @Override
    public void notificarObservadores(Reserva reserva, String evento) {
        for (Observador obs : observadores) {
            obs.atualizar(reserva, evento);        // push
        }
    }

    @Override
    public List<Reserva> getReservasAtuais() {
        return repositorio.todas();
    }

    // ── Operações de Reserva (RF-02) ──────────────────────

    public Reserva criarReserva(Sala sala, Usuario usuario, LocalDateTime inicio, LocalDateTime fim) {
        List<Reserva> existentes = repositorio.todas().stream()
                .filter(r -> r.getSala().getId().equals(sala.getId()))
                .collect(Collectors.toList());

        Reserva novaReserva = new Reserva(sala, usuario, inicio, fim);

        if (!politica.permitirReserva(novaReserva, existentes)) {
            System.out.println("🚫 Reserva recusada pela política: " + politica.getDescricao());
            return null;
        }

        repositorio.adicionar(novaReserva);
        notificarObservadores(novaReserva, "RESERVA_CRIADA");
        System.out.println("✅ Reserva criada: " + novaReserva);
        return novaReserva;

        historico.registrar(new EntradaHistorico(
                reserva.getUsuario(),
                reserva.getSala(),
                reserva.getDataHora(),
                "CONFIRMADA"
        ));
    }

    public boolean modificarReserva(Reserva reserva, LocalDateTime novoInicio, LocalDateTime novoFim) {
        List<Reserva> existentes = repositorio.todas().stream()
                .filter(r -> r.getSala().getId().equals(reserva.getSala().getId()))
                .filter(r -> !r.getId().equals(reserva.getId()))
                .collect(Collectors.toList());

        Reserva temp = new Reserva(reserva.getSala(), reserva.getUsuario(), novoInicio, novoFim);

        if (!politica.permitirReserva(temp, existentes)) {
            System.out.println("🚫 Modificação recusada — conflito de horário.");
            return false;
        }

        reserva.setInicio(novoInicio);
        reserva.setFim(novoFim);
        reserva.setStatus(Reserva.Status.MODIFICADA);
        notificarObservadores(reserva, "RESERVA_MODIFICADA");
        System.out.println("✏️  Reserva modificada: " + reserva);
        return true;
    }

    public void cancelarReserva(Reserva reserva) {
        reserva.setStatus(Reserva.Status.CANCELADA);
        notificarObservadores(reserva, "RESERVA_CANCELADA");
        System.out.println("🗑️  Reserva cancelada: " + reserva);

        historico.registrar(new EntradaHistorico(
                reserva.getUsuario(),
                reserva.getSala(),
                reserva.getDataHora(),
                "CANCELADA"
        ));
    }

    // RF-01: listar salas disponíveis no intervalo
    public List<Sala> salasDisponiveis(List<Sala> todasSalas, LocalDateTime inicio, LocalDateTime fim) {
        return todasSalas.stream()
                .filter(sala -> repositorio.todas().stream()
                        .filter(r -> r.getSala().getId().equals(sala.getId()))
                        .filter(r -> r.getStatus() == Reserva.Status.CONFIRMADA)
                        .noneMatch(r -> r.getInicio().isBefore(fim) && r.getFim().isAfter(inicio)))
                .collect(Collectors.toList());
    }

    // RF-05: relatório diário via pull
    public void gerarRelatorioDiario() {
        for (Observador obs : observadores) {
            obs.atualizarPull(this);  // pull
        }
    }
}