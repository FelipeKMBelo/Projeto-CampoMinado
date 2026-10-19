package model;

/**
 * Representa uma única célula do tabuleiro de Campo Minado.
 * <p>
 * Parte do MODEL (na arquitetura MVC): não conhece Swing, não conhece a
 * View nem o Controller. Todo o estado (minada, revelada, marcação,
 * minasVizinhas) é privado; nenhuma classe externa altera esses valores
 * diretamente, apenas através dos métodos públicos abaixo.
 */
public class Celula {

    /**
     * Estado de marcação de uma célula ainda não revelada.
     * <p>
     * MELHORIA: antes a célula só tinha um {@code boolean marcada}
     * (bandeira ligada/desligada). Agora ela cicla entre três estados,
     * como no Campo Minado clássico do Windows: nenhuma marcação,
     * bandeira e "?" (interrogação, usada pelo jogador para anotar uma
     * célula suspeita sem ter certeza que é mina).
     */
    public enum EstadoMarcacao {
        NENHUMA, BANDEIRA, INTERROGACAO
    }

    private boolean minada;
    private boolean revelada;
    private EstadoMarcacao estadoMarcacao;
    private int minasVizinhas;

    public Celula() {
        this.minada = false;
        this.revelada = false;
        this.estadoMarcacao = EstadoMarcacao.NENHUMA;
        this.minasVizinhas = 0;
    }

    // ----- minada -----

    public boolean isMinada() {
        return minada;
    }

    public void setMinada(boolean minada) {
        this.minada = minada;
    }

    // ----- revelada -----

    public boolean isRevelada() {
        return revelada;
    }

    /**
     * Revela a célula. Uma célula com bandeira não pode ser revelada por
     * engano; é preciso desmarcá-la primeiro. Uma célula com "?" pode ser
     * revelada normalmente (a interrogação é só uma anotação do jogador).
     */
    public void revelar() {
        if (estadoMarcacao != EstadoMarcacao.BANDEIRA) {
            this.revelada = true;
            this.estadoMarcacao = EstadoMarcacao.NENHUMA;
        }
    }

    // ----- marcação (bandeira / interrogação) -----

    /** {@code true} apenas quando a célula está marcada com BANDEIRA. */
    public boolean isMarcada() {
        return estadoMarcacao == EstadoMarcacao.BANDEIRA;
    }

    /** {@code true} apenas quando a célula está marcada com "?". */
    public boolean isInterrogacao() {
        return estadoMarcacao == EstadoMarcacao.INTERROGACAO;
    }

    public EstadoMarcacao getEstadoMarcacao() {
        return estadoMarcacao;
    }

    /**
     * Alterna o estado de marcação em ciclo: nenhuma → bandeira →
     * interrogação → nenhuma. Só é possível marcar uma célula que ainda
     * não foi revelada. Cada instância de Celula guarda o próprio
     * estado — não há nada compartilhado entre células.
     */
    public void alternarMarcacao() {
        if (revelada) {
            return;
        }
        switch (estadoMarcacao) {
            case NENHUMA:
                estadoMarcacao = EstadoMarcacao.BANDEIRA;
                break;
            case BANDEIRA:
                estadoMarcacao = EstadoMarcacao.INTERROGACAO;
                break;
            case INTERROGACAO:
            default:
                estadoMarcacao = EstadoMarcacao.NENHUMA;
                break;
        }
    }

    // ----- minasVizinhas -----

    public int getMinasVizinhas() {
        return minasVizinhas;
    }

    public void setMinasVizinhas(int minasVizinhas) {
        this.minasVizinhas = minasVizinhas;
    }

    /**
     * Representação usada para exibir a célula no console.
     */
    @Override
    public String toString() {
        if (estadoMarcacao == EstadoMarcacao.BANDEIRA) {
            return "F";
        }
        if (estadoMarcacao == EstadoMarcacao.INTERROGACAO) {
            return "?";
        }
        if (!revelada) {
            return ".";
        }
        if (minada) {
            return "*";
        }
        if (minasVizinhas == 0) {
            return " ";
        }
        return String.valueOf(minasVizinhas);
    }
}
