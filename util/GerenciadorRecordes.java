package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * MELHORIA (persistência de dados): guarda o melhor tempo (recorde) de
 * cada dificuldade em um arquivo de propriedades simples, salvo na pasta
 * pessoal do usuário ({@code user.home}). Assim o recorde continua
 * disponível mesmo depois de fechar e abrir o jogo de novo.
 * <p>
 * Fica no pacote {@code util} (e não em {@code model}) porque não é regra
 * de jogo: é um detalhe de "onde e como salvar no disco", usado pelo
 * Controller. O Model continua sem saber nada sobre arquivos.
 */
public final class GerenciadorRecordes {

    private static final String NOME_ARQUIVO = ".campo_minado_recordes.properties";
    private final File arquivo;

    public GerenciadorRecordes() {
        this.arquivo = new File(System.getProperty("user.home"), NOME_ARQUIVO);
    }

    /** Construtor alternativo, útil para testes (evita mexer no HOME real). */
    public GerenciadorRecordes(File arquivoPersonalizado) {
        this.arquivo = arquivoPersonalizado;
    }

    /**
     * @param chaveDificuldade identifica a dificuldade (ex.: "Iniciante",
     *                         "Intermediário", "Avançado" ou uma chave
     *                         gerada para o modo personalizado)
     * @return o recorde salvo em segundos, ou -1 se ainda não houver
     *         recorde para essa dificuldade.
     */
    public int obterRecordeSegundos(String chaveDificuldade) {
        Properties propriedades = carregar();
        String valor = propriedades.getProperty(chaveDificuldade);
        if (valor == null) {
            return -1;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Registra um novo tempo, se ele for melhor (menor) que o recorde
     * atual dessa dificuldade, ou se ainda não houver recorde salvo.
     *
     * @return {@code true} se um novo recorde foi salvo.
     */
    public boolean registrarTempoSePossivelRecorde(String chaveDificuldade, int tempoSegundos) {
        int recordeAtual = obterRecordeSegundos(chaveDificuldade);
        if (recordeAtual != -1 && tempoSegundos >= recordeAtual) {
            return false;
        }
        Properties propriedades = carregar();
        propriedades.setProperty(chaveDificuldade, String.valueOf(tempoSegundos));
        salvar(propriedades);
        return true;
    }

    private Properties carregar() {
        Properties propriedades = new Properties();
        if (arquivo.exists()) {
            try (FileInputStream entrada = new FileInputStream(arquivo)) {
                propriedades.load(entrada);
            } catch (IOException ignorada) {
                // Se não conseguir ler (arquivo corrompido, permissão etc.),
                // simplesmente segue sem recordes salvos em vez de travar o jogo.
            }
        }
        return propriedades;
    }

    private void salvar(Properties propriedades) {
        try (FileOutputStream saida = new FileOutputStream(arquivo)) {
            propriedades.store(saida, "Recordes do Campo Minado - melhor tempo por dificuldade (em segundos)");
        } catch (IOException ignorada) {
            // Falhar ao salvar não deve derrubar o jogo; o recorde só não
            // fica persistido dessa vez.
        }
    }
}
