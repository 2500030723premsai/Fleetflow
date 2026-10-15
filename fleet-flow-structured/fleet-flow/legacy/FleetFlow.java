package fleetflow;

import java.util.*;

public class FleetFlow {

    static Scanner sc = new Scanner(System.in);

    // =====================================================
    // CO1 - STRING ALGORITHMS
    // =====================================================

    // 1. KMP
    static void KMP(String text, String pattern) {
        // KMP algorithm
    }

    // 2. Z-Function
    static void ZFunction(String text, String pattern) {
        // Z algorithm
    }

    // 3. Rabin-Karp
    static void RabinKarp(String text, String pattern) {
        // Rabin-Karp algorithm
    }

    // 4. Aho-Corasick
    static void AhoCorasick(String text, String[] patterns) {
        // Aho-Corasick algorithm
    }


    // =====================================================
    // CO2 - SUFFIX STRUCTURES
    // =====================================================

    // 5. Suffix Array
    static void SuffixArray(String text) {
        // Suffix Array algorithm
    }

    // 6. SA-IS
    static void SAIS(String text) {
        // SA-IS algorithm
    }

    // 7. LCP / Kasai
    static void Kasai(String text, int[] suffixArray) {
        // Kasai algorithm
    }

    // 8. Suffix Tree
    static void SuffixTree(String text, String pattern) {
        // Suffix Tree algorithm
    }


    // =====================================================
    // CO3 - ADVANCED DYNAMIC PROGRAMMING
    // =====================================================

    // 9. Levenshtein Distance
    static void Levenshtein(String a, String b) {
        // Levenshtein DP
    }

    // 10. Damerau-Levenshtein
    static void DamerauLevenshtein(String a, String b) {
        // Damerau-Levenshtein DP
    }

    // 11. Bitmask DP
    static void BitmaskDP(int[][] cost) {
        // Bitmask DP
    }

    // 12. Matrix Chain Multiplication
    static void MatrixChain(int[] dimensions) {
        // Matrix Chain DP
    }

    // 13. Optimal Binary Search Tree
    static void OptimalBST(int[] keys, int[] frequency) {
        // OBST DP
    }


    // =====================================================
    // MAIN METHOD
    // =====================================================

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n======================================");
            System.out.println("       FLEETFLOW");
            System.out.println(" Intelligent Logistics Management");
            System.out.println("======================================");

            System.out.println("\n--- CO1 : STRING ALGORITHMS ---");
            System.out.println("1. KMP");
            System.out.println("2. Z-Function");
            System.out.println("3. Rabin-Karp");
            System.out.println("4. Aho-Corasick");

            System.out.println("\n--- CO2 : SUFFIX STRUCTURES ---");
            System.out.println("5. Suffix Array");
            System.out.println("6. SA-IS");
            System.out.println("7. LCP / Kasai");
            System.out.println("8. Suffix Tree");

            System.out.println("\n--- CO3 : DYNAMIC PROGRAMMING ---");
            System.out.println("9. Levenshtein");
            System.out.println("10. Damerau-Levenshtein");
            System.out.println("11. Bitmask DP");
            System.out.println("12. Matrix Chain");
            System.out.println("13. Optimal BST");

            System.out.println("\n0. Exit");

            System.out.print("\nEnter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter shipment text: ");
                    String text1 = sc.nextLine();

                    System.out.print("Enter shipment ID: ");
                    String pattern1 = sc.nextLine();

                    KMP(text1, pattern1);
                    break;


                case 2:
                    System.out.print("Enter shipment text: ");
                    String text2 = sc.nextLine();

                    System.out.print("Enter pattern: ");
                    String pattern2 = sc.nextLine();

                    ZFunction(text2, pattern2);
                    break;


                case 3:
                    System.out.print("Enter package information: ");
                    String text3 = sc.nextLine();

                    System.out.print("Enter package code: ");
                    String pattern3 = sc.nextLine();

                    RabinKarp(text3, pattern3);
                    break;


                case 4:
                    System.out.print("Enter delivery message: ");
                    String message = sc.nextLine();

                    System.out.print("Enter number of statuses: ");
                    int n = sc.nextInt();
                    sc.nextLine();

                    String[] patterns = new String[n];

                    for (int i = 0; i < n; i++) {
                        System.out.print("Enter status " + (i + 1) + ": ");
                        patterns[i] = sc.nextLine();
                    }

                    AhoCorasick(message, patterns);
                    break;


                case 5:
                    System.out.print("Enter shipment/document text: ");
                    String text5 = sc.nextLine();

                    SuffixArray(text5);
                    break;


                case 6:
                    System.out.print("Enter document text: ");
                    String text6 = sc.nextLine();

                    SAIS(text6);
                    break;


                case 7:
                    System.out.print("Enter document text: ");
                    String text7 = sc.nextLine();

                    // First create suffix array
                    // Then pass it to Kasai
                    Kasai(text7, new int[0]);
                    break;


                case 8:
                    System.out.print("Enter document text: ");
                    String text8 = sc.nextLine();

                    System.out.print("Enter substring: ");
                    String pattern8 = sc.nextLine();

                    SuffixTree(text8, pattern8);
                    break;


                case 9:
                    System.out.print("Enter wrong address: ");
                    String wrong1 = sc.nextLine();

                    System.out.print("Enter correct address: ");
                    String correct1 = sc.nextLine();

                    Levenshtein(wrong1, correct1);
                    break;


                case 10:
                    System.out.print("Enter wrong address: ");
                    String wrong2 = sc.nextLine();

                    System.out.print("Enter correct address: ");
                    String correct2 = sc.nextLine();

                    DamerauLevenshtein(wrong2, correct2);
                    break;


                case 11:
                    System.out.print("Enter number of locations: ");
                    int locations = sc.nextInt();

                    int[][] cost =
                            new int[locations][locations];

                    System.out.println(
                            "Enter cost matrix:"
                    );

                    for (int i = 0; i < locations; i++) {
                        for (int j = 0; j < locations; j++) {
                            cost[i][j] = sc.nextInt();
                        }
                    }

                    BitmaskDP(cost);
                    break;


                case 12:
                    System.out.print("Enter number of matrices: ");
                    int matrices = sc.nextInt();

                    int[] dimensions =
                            new int[matrices + 1];

                    System.out.println(
                            "Enter " + (matrices + 1)
                                    + " dimensions:"
                    );

                    for (int i = 0;
                         i <= matrices;
                         i++) {

                        dimensions[i] = sc.nextInt();
                    }

                    MatrixChain(dimensions);
                    break;


                case 13:
                    System.out.print("Enter number of keys: ");
                    int keysCount = sc.nextInt();

                    int[] keys = new int[keysCount];
                    int[] frequency = new int[keysCount];

                    System.out.println("Enter sorted keys:");

                    for (int i = 0; i < keysCount; i++) {
                        keys[i] = sc.nextInt();
                    }

                    System.out.println("Enter frequencies:");

                    for (int i = 0; i < keysCount; i++) {
                        frequency[i] = sc.nextInt();
                    }

                    OptimalBST(keys, frequency);
                    break;


                case 0:
                    System.out.println(
                            "Thank you for using FleetFlow."
                    );
                    return;


                default:
                    System.out.println(
                            "Invalid choice!"
                    );
            }
        }
    }
}