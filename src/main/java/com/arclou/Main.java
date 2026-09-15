package com.arclou;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[][] event = new String[10][3];
        int count = 0;
        int option = 0;

        do {
            System.out.println("========================");
            System.out.println("        ARCLOU          ");
            System.out.println("========================");
            System.out.println(" ");
            System.out.println("1 - Criar evento");
            System.out.println("2 - Listar eventos");
            System.out.println("3 - Buscar evento");
            System.out.println("4 - Sair");
            System.out.println(" ");

            System.out.print("Escolha: ");

            if (sc.hasNextInt()) {
                option = sc.nextInt();
                sc.nextLine();
                if (option <= 0 || option > 4) {
                    System.out.println("Erro: As opções são somente entre 1 a 4.");
                } else if (option == 1){
                    System.out.println("Informe os dados do evento:");
                    System.out.print("Nome: ");
                    event[count][0] = sc.nextLine();

                    System.out.print("Data: ");
                    event[count][1] = sc.nextLine();

                    System.out.print("Local: ");
                    event[count][2] = sc.nextLine();

                    System.out.println("Evento registrado com sucesso: " + event[count][0] + " | " + event[count][1] + " | " + event[count][2]);
                    count++;
                } else if (option != 4){
                    System.out.println("Em construção...");
                }
            } else {
                String invalidInput = sc.next();
                System.out.println("Erro: '" + invalidInput + "' não é uma das opções acima.");
            }

        } while (option != 4);

        System.out.println("Obrigado!!!");

        sc.close();
    }
}
