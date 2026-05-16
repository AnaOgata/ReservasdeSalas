package com.roombooking.model;

import java.time.LocalDateTime;

public class EntradaHistorico {
    private final Usuario usuario;
    private final Sala sala;
    private final LocalDateTime dataReserva;
    private final String status; // "CONFIRMADA", "CANCELADA"

    public EntradaHistorico(Usuario usuario, Sala sala,
                            LocalDateTime dataReserva, String status) {
        this.usuario = usuario;
        this.sala = sala;
        this.dataReserva = dataReserva;
        this.status = status;
    }

    public Usuario getUsuario() { return usuario; }
    publicAla getSala() { return sala; }
    public LocalDateTime getDataReserva() { return dataReserva; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] Sala: %s | Data: %s | Status: %s",
                usuario.getNome(), sala.getNome(), dataReserva, status);
    }
}