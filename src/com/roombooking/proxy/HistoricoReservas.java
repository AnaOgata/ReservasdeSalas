package com.roombooking.proxy;

import com.roombooking.model.EntradaHistorico;
import com.roombooking.model.Usuario;
import java.util.List;

public interface HistoricoReservas {
    List<EntradaHistorico> buscarHistorico(Usuario solicitante, Usuario alvo);
    void registrar(EntradaHistorico entrada);
}