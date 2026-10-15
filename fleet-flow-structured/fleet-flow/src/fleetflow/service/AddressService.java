package fleetflow.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.engine.dp.LevenshteinDistance;
import fleetflow.model.PackageItem;
import fleetflow.util.InputHelper;

/**
 * Feature 10 – Address Correction [Levenshtein Distance]
 * Suggests the closest stored address to a potentially misspelled input.
 * Does NOT automatically replace – user must confirm.
 */
public class AddressService {

    private final DataStore data;

    public AddressService(DataStore data) {
        this.data = data;
    }

    /** Simple single-best suggestion (used by legacy code). */
    public void suggest(String address) {
        if (data.packages.isEmpty()) {
            System.out.println("No stored addresses to compare.");
            return;
        }
        PackageItem best = null;
        int bestScore = Integer.MAX_VALUE;
        for (PackageItem p : data.packages) {
            int d = LevenshteinDistance.distance(address.toLowerCase(), p.getAddress().toLowerCase());
            if (d < bestScore) { bestScore = d; best = p; }
        }
        if (best != null)
            System.out.println("Closest match: " + best.getAddress() + " (edit distance " + bestScore + ")");
    }

    /** Interactive address correction sub-menu. */
    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Address Correction [Levenshtein Distance] ---");
            System.out.println("1. View all known addresses");
            System.out.println("2. Search / correct a misspelled address");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> listKnownAddresses();
                case 2 -> correctInteractive(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /** Prints every distinct address stored in packages. */
    private void listKnownAddresses() {
        if (data.packages.isEmpty()) {
            System.out.println("No packages in the system. Use option 16 (Load Sample Data) first.");
            return;
        }
        System.out.println("\n--- Known Addresses (from Package records) ---");
        int i = 1;
        for (PackageItem p : data.packages) {
            System.out.println("  " + i + ". [" + p.getId() + "] " + p.getAddress());
            i++;
        }
        System.out.println("Total: " + data.packages.size() + " address(es)");
    }

    private void correctInteractive(Scanner scanner) {
        if (data.packages.isEmpty()) {
            System.out.println("No known addresses in the system.");
            System.out.println("Go back to the main menu and choose option 16 to load sample data,");
            System.out.println("or add packages via option 3 (Package Management).");
            return;
        }

        // Always show known addresses first so the user knows what to type
        listKnownAddresses();

        System.out.println("\nType an address (exactly or with typos) and the system will");
        System.out.println("calculate the Levenshtein edit distance to every known address");
        System.out.println("and suggest the closest matches.");

        String input = InputHelper.readNonEmpty(scanner, "\nEnter address to correct: ");

        // Compute Levenshtein distance to every stored address
        List<Candidate> candidates = new ArrayList<>();
        for (PackageItem p : data.packages) {
            int dist = LevenshteinDistance.distance(
                    input.toLowerCase(), p.getAddress().toLowerCase());
            candidates.add(new Candidate(p.getAddress(), p.getId(), dist));
        }

        // Sort by distance ascending (closest match first)
        candidates.sort(Comparator.comparingInt(c -> c.dist));

        // Show top 3 suggestions
        int showCount = Math.min(3, candidates.size());
        System.out.println("\nTop " + showCount + " suggestions (Levenshtein edit distance from '"
                + input + "'):");
        System.out.println("  (Lower distance = closer match)");
        for (int i = 0; i < showCount; i++) {
            Candidate c = candidates.get(i);
            System.out.printf("  %d. %-30s [Package: %s]  distance: %d%n",
                    i + 1, c.address, c.packageId, c.dist);
        }

        if (showCount == 0) return;

        System.out.print("\nUse suggestion #1 ('" + candidates.get(0).address + "')? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            System.out.println("Confirmed address: " + candidates.get(0).address);
            System.out.println("To apply this to a package go to: Package Management -> Update Package.");
        } else {
            System.out.println("No address selected.");
        }
    }

    private static class Candidate {
        final String address;
        final String packageId;
        final int dist;
        Candidate(String a, String pid, int d) { address = a; packageId = pid; dist = d; }
    }
}
