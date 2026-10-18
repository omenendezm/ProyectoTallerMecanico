package TallerMecanico;

public class InventarioInsuficienteException extends TallerException {

    private final int disponible;
    private final int solicitado;

    public InventarioInsuficienteException(String repuesto, int disponible, int solicitado) {
        super("Inventario insuficiente de " + repuesto + ". Disponible: " + disponible
            + ", solicitado: " + solicitado + ". No se modificó el inventario.");
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public int getDisponible() { return disponible; }
    public int getSolicitado() { return solicitado; }
}