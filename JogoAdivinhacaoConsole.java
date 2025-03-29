import java.util.Scanner;
import java.util.Random;

public class JogoAdivinhacaoConsole {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        
        System.out.println("=== JOGO DE ADIVINHAÇÃO ===");
        System.out.println("Escolha a dificuldade:");
        System.out.println("1 - Fácil (1-50)");
        System.out.println("2 - Médio (1-100)");
        System.out.println("3 - Difícil (1-200)");
        System.out.print("Sua escolha: ");
        
        int dificuldade = scanner.nextInt();
        int maxNumero = 0;
        int maxTentativas = 0;
        int multiplicador = 0;
        
        switch(dificuldade) {
            case 1:
                maxNumero = 50;
                maxTentativas = 10;
                multiplicador = 1;
                break;
            case 2:
                maxNumero = 100;
                maxTentativas = 8;
                multiplicador = 2;
                break;
            case 3:
                maxNumero = 200;
                maxTentativas = 5;
                multiplicador = 3;
                break;
            default:
                System.out.println("Opção inválida! Usando dificuldade média.");
                maxNumero = 100;
                maxTentativas = 8;
                multiplicador = 2;
        }
        
        int numeroSecreto = random.nextInt(maxNumero) + 1;
        int tentativas = 0;
        int pontuacao = 0;
        boolean acertou = false;
        
        System.out.println("\nTente adivinhar o número entre 1 e " + maxNumero);
        System.out.println("Você tem " + maxTentativas + " tentativas!");
        
        while(tentativas < maxTentativas && !acertou) {
            System.out.print("\nTentativa " + (tentativas + 1) + ": ");
            int palpite = scanner.nextInt();
            tentativas++;
            
            if(palpite < 1 || palpite > maxNumero) {
                System.out.println("Número inválido! Digite entre 1 e " + maxNumero);
                continue;
            }
            
            if(palpite == numeroSecreto) {
                acertou = true;
                pontuacao = (maxTentativas - tentativas + 1) * multiplicador * 10;
                System.out.println("\nPARABÉNS! Você acertou em " + tentativas + " tentativa(s)!");
                System.out.println("Pontuação: " + pontuacao);
            } else if(palpite < numeroSecreto) {
                System.out.println("Muito baixo!");
            } else {
                System.out.println("Muito alto!");
            }
        }
        
        if(!acertou) {
            System.out.println("\nFIM DE JOGO! O número era: " + numeroSecreto);
        }
        
        scanner.close();
    }
}