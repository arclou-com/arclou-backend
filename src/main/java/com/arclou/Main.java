package com.arclou;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

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
                if (option <= 0 || option > 4) {
                    System.out.println("Erro: As opções são somente entre 1 a 4.");
                } else if (option != 4) {
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
