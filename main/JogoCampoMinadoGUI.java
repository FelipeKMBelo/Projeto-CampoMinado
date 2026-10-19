package main;

import controller.CampoMinadoController;
import view.CampoMinadoView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class JogoCampoMinadoGUI {

    public static void main(String[] args) {
        try {
            // Usar o Look and Feel do SISTEMA (ex.: Aqua no macOS) faz o
            // próprio SO pintar os JButtons, ignorando as cores que
            // definimos via setBackground/setForeground — é por isso que
            // as células apareciam com fundo branco e texto cinza.
            // O Metal (cross-platform) sempre respeita as cores que a
            // gente define, então o visual fica igual em qualquer SO.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se por algum motivo não estiver disponível, segue com o padrão.
        }

        SwingUtilities.invokeLater(() -> {
            CampoMinadoView view = new CampoMinadoView();
            CampoMinadoController controller = new CampoMinadoController(view);
            controller.iniciar();
        });
    }
}
