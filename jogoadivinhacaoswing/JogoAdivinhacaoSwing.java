package jogoadivinhacaoswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.ArrayList;

public class JogoAdivinhacaoSwing {
    // Cores modernas
    private final Color COR_PRIMARIA = new Color(70, 130, 180);
    private final Color COR_SECUNDARIA = new Color(255, 215, 0);
    private final Color COR_FUNDO = new Color(240, 248, 255);
    private final Color COR_TEXTO = new Color(50, 50, 50);
    private final Color COR_ACERTO = new Color(46, 204, 113);
    private final Color COR_ERRO = new Color(231, 76, 60);
    
    // Componentes UI
    private JFrame frame;
    private JLabel mensagem, resultado, tentativasLabel, pontuacaoLabel;
    private JTextField entradaPalpite;
    private JButton botaoEnviar, botaoNovoJogo;
    private JComboBox<String> comboDificuldade;
    private JProgressBar progressoTentativas;
    private JList<String> listaHistorico;
    private DefaultListModel<String> modeloHistorico;
    
    // Variáveis do jogo
    private int numeroAleatorio;
    private int tentativas;
    private int maxTentativas = 8;
    private int dificuldade = 2; // 1=fácil, 2=médio, 3=difícil
    private int pontuacao = 0;
    private boolean jogoAtivo = false;

    public JogoAdivinhacaoSwing() {
        criarInterface();
        iniciarJogo();
    }

    private void iniciarJogo() {
        switch(dificuldade) {
            case 1: numeroAleatorio = (int)(Math.random() * 50) + 1; maxTentativas = 10; break;
            case 2: numeroAleatorio = (int)(Math.random() * 100) + 1; maxTentativas = 8; break;
            case 3: numeroAleatorio = (int)(Math.random() * 200) + 1; maxTentativas = 5; break;
        }
        tentativas = 0;
        jogoAtivo = true;
        modeloHistorico.clear();
        atualizarUI();
        entradaPalpite.requestFocus();
    }

    private void criarInterface() {
        // Configuração da janela principal
        frame = new JFrame("Jogo de Adivinhação Premium");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Painel principal com gradiente
        JPanel painelFundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COR_FUNDO, getWidth(), getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        painelFundo.setLayout(new BorderLayout(20, 20));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Cabeçalho
        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setOpaque(false);
        mensagem = new JLabel("ADIVINHE O NÚMERO SECRETO", SwingConstants.CENTER);
        mensagem.setFont(new Font("Segoe UI", Font.BOLD, 28));
        mensagem.setForeground(COR_PRIMARIA);
        painelCabecalho.add(mensagem);
        
        // Seletor de dificuldade
        JPanel painelDificuldade = new JPanel();
        painelDificuldade.setOpaque(false);
        comboDificuldade = new JComboBox<>(new String[]{"Fácil (1-50)", "Médio (1-100)", "Difícil (1-200)"});
        comboDificuldade.setSelectedIndex(1);
        comboDificuldade.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboDificuldade.addActionListener(e -> {
            dificuldade = comboDificuldade.getSelectedIndex() + 1;
            iniciarJogo();
        });
        painelDificuldade.add(comboDificuldade);
        
        // Barra de progresso
        progressoTentativas = new JProgressBar(0, maxTentativas);
        progressoTentativas.setStringPainted(true);
        progressoTentativas.setForeground(COR_PRIMARIA);
        progressoTentativas.setBackground(Color.WHITE);
        progressoTentativas.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        // Campo de entrada
        entradaPalpite = new JTextField();
        entradaPalpite.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        entradaPalpite.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_PRIMARIA, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        entradaPalpite.setHorizontalAlignment(JTextField.CENTER);
        entradaPalpite.addActionListener(this::processarPalpite);
        
        // Botão de envio
        botaoEnviar = criarBotao("ENVIAR PALPITE", COR_PRIMARIA);
        botaoEnviar.addActionListener(this::processarPalpite);
        
        // Botão de novo jogo
        botaoNovoJogo = criarBotao("NOVO JOGO", COR_SECUNDARIA);
        botaoNovoJogo.addActionListener(e -> iniciarJogo());
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 15, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoEnviar);
        painelBotoes.add(botaoNovoJogo);
        
        // Painel de interação
        JPanel painelInteracao = new JPanel(new BorderLayout(0, 15));
        painelInteracao.setOpaque(false);
        painelInteracao.add(entradaPalpite, BorderLayout.NORTH);
        painelInteracao.add(painelBotoes, BorderLayout.CENTER);
        
        // Painel de estatísticas
        JPanel painelStats = new JPanel(new GridLayout(1, 2, 15, 0));
        painelStats.setOpaque(false);
        
        tentativasLabel = criarLabelEstatistica("Tentativas", "0/" + maxTentativas);
        pontuacaoLabel = criarLabelEstatistica("Pontuação", "0");
        
        painelStats.add(tentativasLabel);
        painelStats.add(pontuacaoLabel);
        
        // Resultado
        resultado = new JLabel("", SwingConstants.CENTER);
        resultado.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultado.setForeground(COR_PRIMARIA);
        
        // Histórico
        modeloHistorico = new DefaultListModel<>();
        listaHistorico = new JList<>(modeloHistorico);
        listaHistorico.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaHistorico.setBackground(new Color(255, 255, 255, 150));
        listaHistorico.setSelectionBackground(COR_PRIMARIA);
        
        JScrollPane scrollHistorico = new JScrollPane(listaHistorico);
        scrollHistorico.setBorder(BorderFactory.createTitledBorder("Histórico de Palpites"));
        scrollHistorico.setOpaque(false);
        scrollHistorico.getViewport().setOpaque(false);
        
        // Montagem do layout
        JPanel painelCentral = new JPanel(new BorderLayout(0, 20));
        painelCentral.setOpaque(false);
        painelCentral.add(painelInteracao, BorderLayout.NORTH);
        painelCentral.add(resultado, BorderLayout.CENTER);
        painelCentral.add(scrollHistorico, BorderLayout.SOUTH);
        
        painelFundo.add(painelCabecalho, BorderLayout.NORTH);
        painelFundo.add(painelDificuldade, BorderLayout.CENTER);
        painelFundo.add(progressoTentativas, BorderLayout.AFTER_LINE_ENDS);
        painelFundo.add(painelStats, BorderLayout.AFTER_LAST_LINE);
        painelFundo.add(painelCentral, BorderLayout.SOUTH);

        frame.add(painelFundo);
        frame.setVisible(true);
    }
    
    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(cor.darker());
            }
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(cor);
            }
        });
        
        return botao;
    }
    
    private JLabel criarLabelEstatistica(String titulo, String valor) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelTitulo.setForeground(COR_TEXTO);
        
        JLabel labelValor = new JLabel(valor, SwingConstants.CENTER);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelValor.setForeground(COR_PRIMARIA);
        
        painel.add(labelTitulo, BorderLayout.NORTH);
        painel.add(labelValor, BorderLayout.CENTER);
        
        JLabel container = new JLabel();
        container.setLayout(new BorderLayout());
        container.add(painel, BorderLayout.CENTER);
        container.setBorder(BorderFactory.createLineBorder(COR_PRIMARIA, 1, true));
        return container;
    }
    
    private void processarPalpite(ActionEvent e) {
        if (!jogoAtivo) return;
        
        try {
            int palpite = Integer.parseInt(entradaPalpite.getText());
            tentativas++;
            
            // Validação do palpite
            int max = (dificuldade == 1) ? 50 : (dificuldade == 2) ? 100 : 200;
            if (palpite < 1 || palpite > max) {
                resultado.setText("Número deve estar entre 1 e " + max + "!");
                resultado.setForeground(COR_ERRO);
                return;
            }
            
            // Adiciona ao histórico
            String itemHistorico = String.format("Tentativa %d: %d", tentativas, palpite);
            modeloHistorico.add(0, itemHistorico);
            
            if (palpite < numeroAleatorio) {
                resultado.setText("Muito baixo! Tente um número maior.");
                resultado.setForeground(COR_ERRO);
            } else if (palpite > numeroAleatorio) {
                resultado.setText("Muito alto! Tente um número menor.");
                resultado.setForeground(COR_ERRO);
            } else {
                resultado.setText("PARABÉNS! Você acertou em " + tentativas + " tentativa(s)!");
                resultado.setForeground(COR_ACERTO);
                pontuacao += (maxTentativas - tentativas + 1) * dificuldade * 10;
                jogoAtivo = false;
            }
            
            if (tentativas >= maxTentativas && palpite != numeroAleatorio) {
                resultado.setText("FIM DE JOGO! O número era: " + numeroAleatorio);
                resultado.setForeground(COR_ERRO);
                jogoAtivo = false;
            }
            
            atualizarUI();
            entradaPalpite.setText("");
        } catch (NumberFormatException ex) {
            resultado.setText("Por favor, insira um número válido!");
            resultado.setForeground(COR_ERRO);
        }
    }
    
    private void atualizarUI() {
        progressoTentativas.setMaximum(maxTentativas);
        progressoTentativas.setValue(tentativas);
        progressoTentativas.setString(tentativas + "/" + maxTentativas);
        
        tentativasLabel.setText("Tentativas: " + tentativas + "/" + maxTentativas);
        pontuacaoLabel.setText("Pontuação: " + pontuacao);
        
        entradaPalpite.setEnabled(jogoAtivo);
        botaoEnviar.setEnabled(jogoAtivo);
        
        // Atualiza cores da barra de progresso
        double porcentagem = (double)tentativas / maxTentativas;
        if (porcentagem > 0.75) {
            progressoTentativas.setForeground(COR_ERRO);
        } else if (porcentagem > 0.5) {
            progressoTentativas.setForeground(Color.ORANGE);
        } else {
            progressoTentativas.setForeground(COR_PRIMARIA);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new JogoAdivinhacaoSwing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}