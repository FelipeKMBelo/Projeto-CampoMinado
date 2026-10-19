# Campo Minado — melhorias

Projeto Java Swing, mantendo a organização MVC e sem novas dependências de execução.

## Como executar

No IntelliJ, abra o projeto, configure um JDK 17 ou superior, marque `src` como pasta de código e execute `main.JogoCampoMinadoGUI`.

Alternativamente, execute `iniciar.ps1` pelo PowerShell. Ele compila apenas o jogo e abre a interface. Requer `java` e `javac` no PATH.

## O que mudou

- Cores das casas e marcações respeitam o tema, inclusive após passar o mouse.
- Bombas e bandeiras desenhadas em Java, sem depender de fontes de emojis.
- Tutorial com rolagem, significado dos números e regras da pontuação.
- Janela redimensionável; menu e tabuleiros grandes têm rolagem.
- Nome do jogador, histórico de partidas e ranking por quantidade de jogos. Clique nos cabeçalhos para ordenar por pontos, vitórias ou tempo.
- Histórico inclui data, dificuldade, resultado, tempo, pontos, dicas e limite de tempo.
- Fim do tempo encerra a partida e revela as minas sem trocar o tabuleiro.
- Atualizações imediatas substituem animações encadeadas para impedir que uma partida antiga altere a seguinte.
- Seleções de tema, tempo e som são mantidas ao voltar ao menu durante a sessão.

## Dados e pontuação

O histórico fica em `dados/partidas.properties`, relativo à pasta de execução. Use sempre a pasta do projeto como diretório de trabalho (o script já faz isso). Copie a pasta `dados` para preservar os resultados ao mover o projeto.

O mesmo nome, ignorando maiúsculas/minúsculas, reúne as partidas do jogador. Não há senha, conta online ou sincronização. O ranking reúne dificuldades diferentes; a tabela de partidas permite consultar a dificuldade de cada resultado.

Pontuação: 10 por casa segura revelada; vitória acrescenta 500 e um bônus de rapidez de `máximo(0, 300 - segundos)`. Cada dica desconta 50; o total mínimo é zero.

Vitória, derrota, tempo esgotado e abandono pelo botão Novo Jogo são registrados. Partidas sem nenhuma revelação não contam como abandono. Fechar a janela durante uma partida não salva essa partida nem permite retomá-la. Os resultados já registrados permanecem.

O primeiro clique continua sujeito a mina, conforme a regra original. Uma dica permite começar em uma casa segura. Os melhores tempos antigos continuam usando o arquivo original de recordes na pasta pessoal.

## Código para estudar

- `view/CampoMinadoView.java`: temas, tutorial e tabelas.
- `view/IconeJogo.java`: desenho das bombas e bandeiras.
- `controller/CampoMinadoController.java`: encerramento e registro das partidas.
- `util/HistoricoPartidas.java`: arquivo local, ranking e pontos.

## Verificação

Compilação do jogo realizada com JDK 26. Os 15 testes JUnit existentes passaram. Corrigidas duas expectativas incorretas no teste de vizinhança com minas em (0,0) e (0,1): a casa (0,2) tem uma mina vizinha e a casa (2,2) tem zero.

`test/MelhoriasTest.java` verifica persistência, nomes com acentos, ranking, pontuação, arquivo inválido, 16 combinações de temas, ícones, rolagem do tutorial, encerramento por tempo sem troca do tabuleiro, ausência de registro duplicado e abandono. É um teste executável sem JUnit. Os testes originais exigem JUnit 5, como no projeto inicial.

O ambiente de validação apresentou uma restrição de acesso do JDK ao fechar os arquivos JAR de teste; as classes geradas foram executadas e as verificações passaram. A compilação do jogo sem dependências externas concluiu normalmente.
