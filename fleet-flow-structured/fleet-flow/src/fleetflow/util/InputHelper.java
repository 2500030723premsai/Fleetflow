package fleetflow.util;
import java.util.Scanner;
public final class InputHelper { private InputHelper(){} public static String readNonEmpty(Scanner scanner,String prompt){while(true){System.out.print(prompt);String value=scanner.nextLine().trim();if(!value.isEmpty())return value;System.out.println("Input cannot be empty.");}} public static int readInt(Scanner scanner,String prompt){while(true){System.out.print(prompt);try{return Integer.parseInt(scanner.nextLine().trim());}catch(NumberFormatException ex){System.out.println("Enter a valid whole number.");}}} }
