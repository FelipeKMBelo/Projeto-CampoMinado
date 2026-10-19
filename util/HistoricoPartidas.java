package util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Histórico local em Properties, sem banco de dados ou bibliotecas externas. */
public class HistoricoPartidas {
    private final Path arquivo;
    private final List<Object[]> partidas = new ArrayList<>();

    public HistoricoPartidas() throws IOException {
        this(Paths.get("dados", "partidas.properties"));
    }

    public HistoricoPartidas(Path arquivo) throws IOException {
        this.arquivo = arquivo;
        if (!Files.exists(arquivo)) return;
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(arquivo)) { p.load(reader); }
        try {
            int total = Integer.parseInt(p.getProperty("total", "0"));
            if (total < 0) throw new IllegalArgumentException();
            for (int i = 0; i < total; i++) {
                Object[] linha = new Object[8];
                for (int j = 0; j < 8; j++) {
                    String valor = p.getProperty(i + "." + j);
                    if (valor == null) throw new IllegalArgumentException();
                    linha[j] = j >= 4 ? Integer.valueOf(valor) : valor;
                }
                partidas.add(linha);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("O arquivo de histórico está inválido. Ele foi preservado.", e);
        }
    }

    public static int calcularPontos(int reveladas, boolean venceu, int segundos, int dicas) {
        return Math.max(0, reveladas * 10 + (venceu ? 500 + Math.max(0, 300 - segundos) : 0) - dicas * 50);
    }

    public void registrar(String jogador, String dificuldade, String resultado,
                          int segundos, int pontos, int dicas, int limite) throws IOException {
        Object[] linha = {LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                jogador, dificuldade, resultado, segundos, pontos, dicas, limite};
        partidas.add(linha);
        Properties p = new Properties();
        p.setProperty("total", String.valueOf(partidas.size()));
        for (int i = 0; i < partidas.size(); i++)
            for (int j = 0; j < 8; j++) p.setProperty(i + "." + j, partidas.get(i)[j].toString());
        try {
            Path destino = arquivo.toAbsolutePath();
            Files.createDirectories(destino.getParent());
            Path temporario = Files.createTempFile(destino.getParent(), "partidas-", ".tmp");
            try {
                try (Writer writer = Files.newBufferedWriter(temporario)) { p.store(writer, "Historico do Campo Minado"); }
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            } finally { Files.deleteIfExists(temporario); }
        } catch (IOException e) {
            partidas.remove(partidas.size() - 1);
            throw e;
        }
    }

    public Object[][] partidas() {
        List<Object[]> copia = new ArrayList<>();
        for (int i = partidas.size() - 1; i >= 0; i--) copia.add(partidas.get(i).clone());
        return copia.toArray(new Object[0][]);
    }

    public Object[][] ranking() {
        Map<String, Object[]> jogadores = new LinkedHashMap<>();
        for (Object[] p : partidas) {
            String nome = (String) p[1];
            Object[] r = jogadores.computeIfAbsent(nome.toLowerCase(Locale.ROOT), k -> new Object[]{nome, 0, 0, 0, 0});
            r[1] = (int) r[1] + 1;
            r[2] = (int) r[2] + ("Vitória".equals(p[3]) ? 1 : 0);
            r[3] = (int) r[3] + (int) p[5];
            r[4] = (int) r[4] + (int) p[4];
        }
        List<Object[]> lista = new ArrayList<>(jogadores.values());
        lista.sort(Comparator.<Object[]>comparingInt(r -> (int) r[1]).reversed()
                .thenComparing(Comparator.<Object[]>comparingInt(r -> (int) r[3]).reversed()));
        return lista.toArray(new Object[0][]);
    }
}
