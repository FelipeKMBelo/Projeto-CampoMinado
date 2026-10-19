import view.*;
import model.*;
import controller.*;
import util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.lang.reflect.*;

/** Executar com java -ea; não exige JUnit. Usa somente arquivos temporários. */
public class MelhoriasTest {
    static Object get(Object o, String n) throws Exception { Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); return f.get(o); }
    static void set(Object o, String n, Object v) throws Exception { Field f=o.getClass().getDeclaredField(n); f.setAccessible(true); f.set(o,v); }
    static void check(boolean v, String msg) { if(!v) throw new AssertionError(msg); }
    static void hover(JButton b, int tipo) { MouseEvent e=new MouseEvent(b,tipo,0,0,1,1,0,false); for(MouseListener l:b.getMouseListeners()) { if(tipo==MouseEvent.MOUSE_ENTERED)l.mouseEntered(e);else l.mouseExited(e); } }
    static void layout(Container c) { c.doLayout(); for(Component x:c.getComponents())if(x instanceof Container)layout((Container)x); }
    static JTextArea area(Container c) { for(Component x:c.getComponents()) { if(x instanceof JTextArea)return (JTextArea)x; if(x instanceof Container){ JTextArea r=area((Container)x);if(r!=null)return r;} }return null; }
    public static void main(String[] args) throws Exception {
        Path arquivo=Files.createTempDirectory("campo-teste").resolve("partidas.properties");
        HistoricoPartidas h=new HistoricoPartidas(arquivo);
        h.registrar("João = ADS", "Iniciante", "Vitória", 42, 1000, 1, 0);
        h.registrar("joão = ads", "Avançado", "Derrota", 12, 20, 0, 60);
        h=new HistoricoPartidas(arquivo);
        check(h.partidas().length==2,"Persistência");
        check(h.ranking().length==1 && (int)h.ranking()[0][1]==2,"Ranking por jogador");
        check((int)h.ranking()[0][4]==54,"Tempo acumulado");
        check(HistoricoPartidas.calcularPontos(71,true,100,1)==1360,"Pontuação");
        check(HistoricoPartidas.calcularPontos(1,false,100,3)==0,"Piso da pontuação");
        Files.writeString(arquivo,"total=1\n");
        try { new HistoricoPartidas(arquivo);throw new AssertionError("Corrupção não detectada"); }catch(java.io.IOException esperado){}
        Files.delete(arquivo);
        final HistoricoPartidas historico=new HistoricoPartidas(arquivo);
        SwingUtilities.invokeAndWait(() -> {
            CampoMinadoView v=new CampoMinadoView();
            try {
                v.mostrarTelaInicial();
                for(int fundo=0;fundo<4;fundo++) {
                    ((JComboBox<?>)get(v,"comboTemaFundo")).setSelectedIndex(fundo);
                    for(int tema=0;tema<4;tema++) {
                        ((JComboBox<?>)get(v,"comboTemaTabuleiro")).setSelectedIndex(tema);
                        v.aplicarTemaSelecionado();v.iniciarTelaDeJogo(9,9,10,71,0);
                        JButton b=((JButton[][])get(v,"botoes"))[0][0];Color cor=b.getBackground();
                        hover(b,MouseEvent.MOUSE_ENTERED);hover(b,MouseEvent.MOUSE_EXITED);
                        check(cor.equals(b.getBackground()),"Tema mudou no hover");
                        Tabuleiro t=new Tabuleiro(9,9,new int[][]{{0,0}});
                        t.alternarMarcacao(0,0);v.atualizarCelula(0,0,t);
                        check(b.getIcon()!=null && b.getBackground().equals(cor),"Bandeira/tema");
                        v.marcarMinaExplodida(0,0);Color mina=b.getBackground();
                        hover(b,MouseEvent.MOUSE_ENTERED);hover(b,MouseEvent.MOUSE_EXITED);
                        check(b.getIcon()!=null && mina.equals(b.getBackground()),"Bomba/hover");
                        v.mostrarTelaInicial();
                    }
                }
                Method tutorial=v.getClass().getDeclaredMethod("mostrarTutorial");tutorial.setAccessible(true);tutorial.invoke(v);
                v.validate();JTextArea texto=area(v.getContentPane());
                check(texto!=null && SwingUtilities.getAncestorOfClass(JScrollPane.class,texto)!=null,"Tutorial sem rolagem");
                check(texto.getWidth()>100 && texto.getHeight()>100,"Tutorial sem espaço");
                BufferedImage img=new BufferedImage(v.getWidth(),v.getHeight(),BufferedImage.TYPE_INT_RGB);
                layout(v.getContentPane());v.getContentPane().printAll(img.getGraphics());javax.imageio.ImageIO.write(img,"png",Path.of(System.getProperty("java.io.tmpdir"), "campo-tutorial.png").toFile());
                v.mostrarTelaInicial();
                CampoMinadoController c=new CampoMinadoController(v);set(c,"historico",historico);
                c.aoEscolherDificuldade(9,9,10);set(c,"jogoIniciado",true);set(c,"tempoInicio",System.currentTimeMillis()-61000);set(c,"limiteSegundos",60);
                Object tab=get(c,"tabuleiro");c.aoPedirDica();
                check((boolean)get(c,"partidaEncerrada"),"Tempo não encerrou");
                check(get(c,"tabuleiro")==tab,"Tempo trocou tabuleiro");
                c.aoRevelarCelula(0,0);c.aoPedirDica();c.aoPedirNovoJogo();
                check(historico.partidas().length==1,"Resultado duplicado");
                c.aoEscolherDificuldade(9,9,10);set(c,"jogoIniciado",true);set(c,"tempoInicio",System.currentTimeMillis());c.aoPedirNovoJogo();
                check(historico.partidas().length==2,"Abandono não salvo");
            }catch(Exception e){throw new RuntimeException(e);}finally{v.dispose();}
        });
        Files.deleteIfExists(arquivo);Files.delete(arquivo.getParent());
        System.out.println("OK: persistência, ranking, pontuação, 16 temas, ícones, tutorial, fim do tempo e abandono.");
    }
}
