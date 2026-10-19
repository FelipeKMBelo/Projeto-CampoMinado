package util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * MELHORIA (som e feedback): efeitos sonoros simples para clique, marcar
 * bandeira, explosão (derrota) e vitória.
 * <p>
 * Em vez de depender de arquivos .wav externos (que complicariam a
 * distribuição do projeto para a apresentação), os "bipes" são gerados
 * na hora como ondas senoidais puras usando {@code javax.sound.sampled}
 * — é a mesma ideia de um sintetizador bem simples. Cada som toca em
 * uma thread separada para não travar a interface gráfica (Swing) enquanto
 * o áudio é reproduzido.
 * <p>
 * Fica no pacote {@code util} porque é uma ferramenta de apoio usada
 * pelo Controller; não faz parte do Model (não guarda estado de jogo) nem
 * da View (não desenha nada).
 */
public final class SomUtil {

    private static final float TAXA_AMOSTRAGEM = 44100f;
    private static volatile boolean ligado = true;

    private SomUtil() {
    }

    public static void setLigado(boolean valor) {
        ligado = valor;
    }

    public static boolean isLigado() {
        return ligado;
    }

    /** Bipe curto e agudo ao revelar uma célula segura. */
    public static void tocarClique() {
        tocarTomAssincrono(880, 35);
    }

    /** Bipe médio ao marcar/desmarcar uma bandeira. */
    public static void tocarBandeira() {
        tocarTomAssincrono(523, 60);
    }

    /** Som grave e mais longo, simulando uma explosão, ao perder. */
    public static void tocarExplosao() {
        new Thread(() -> {
            tocarTom(220, 90, 0.9);
            tocarTom(140, 160, 0.9);
            tocarTom(90, 220, 0.7);
        }, "SomUtil-Explosao").start();
    }

    /** Pequena sequência ascendente de notas ao vencer a partida. */
    public static void tocarVitoria() {
        new Thread(() -> {
            int[] notas = {523, 659, 784, 1047}; // Dó - Mi - Sol - Dó (oitava acima)
            for (int nota : notas) {
                tocarTom(nota, 110, 0.5);
            }
        }, "SomUtil-Vitoria").start();
    }

    private static void tocarTomAssincrono(int frequenciaHz, int duracaoMs) {
        new Thread(() -> tocarTom(frequenciaHz, duracaoMs, 0.35), "SomUtil-Tom").start();
    }

    /**
     * Gera e reproduz uma onda senoidal simples. Qualquer falha de áudio
     * (ex.: ambiente sem placa de som/mixer disponível) é ignorada
     * silenciosamente — o jogo nunca deve travar ou quebrar por causa de
     * um efeito sonoro opcional.
     */
    private static void tocarTom(int frequenciaHz, int duracaoMs, double volume) {
        if (!ligado) {
            return;
        }
        try {
            int totalAmostras = (int) (TAXA_AMOSTRAGEM * duracaoMs / 1000.0);
            byte[] buffer = new byte[totalAmostras];

            for (int i = 0; i < totalAmostras; i++) {
                double angulo = 2.0 * Math.PI * i * frequenciaHz / TAXA_AMOSTRAGEM;
                // Fade-out simples no final para evitar "estalo" no corte do som.
                double envelope = 1.0 - (double) i / totalAmostras;
                buffer[i] = (byte) (Math.sin(angulo) * volume * envelope * 127);
            }

            AudioFormat formato = new AudioFormat(TAXA_AMOSTRAGEM, 8, 1, true, true);
            try (SourceDataLine linha = AudioSystem.getSourceDataLine(formato)) {
                linha.open(formato);
                linha.start();
                linha.write(buffer, 0, buffer.length);
                linha.drain();
            }
        } catch (Exception ignorada) {
            // Ambiente sem suporte a áudio (comum em alguns laboratórios/VMs):
            // o jogo continua funcionando normalmente, só sem som.
        }
    }
}
