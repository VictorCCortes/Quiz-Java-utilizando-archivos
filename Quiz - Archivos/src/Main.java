import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        boolean repetirJoc;
        do {
            String[] coleccioPreguntes = new String[20], coleccioRespostes = new String[20], preguntesJoc, respostesJoc;
            int numPreguntes, torn = 0, encerts = 0;
            String resposta, nomUsuari;


            nomUsuari = preguntarNom();
            declararPreguntesRespostes(coleccioPreguntes, coleccioRespostes);
            numPreguntes = demanarNumPreguntes();
            preguntesJoc = mascaraPreguntesJoc(numPreguntes);
            respostesJoc = mascaraRespostesJoc(numPreguntes);
            escollirPreguntesRandom(preguntesJoc, coleccioPreguntes, coleccioRespostes, respostesJoc);
            do {
                resposta = preguntarUsuari(preguntesJoc, torn);
                encerts = comprovarResposta(respostesJoc, torn, encerts, resposta);
                torn++;
            } while(torn < numPreguntes);
            repetirJoc = finalJoc(encerts, numPreguntes, nomUsuari);
        } while (repetirJoc);
    }
    private static String preguntarNom() {
        String nom;
        System.out.print("Introdueix el nom d'usuari per aquesta partida: ");
        nom = Teclat.llegirString();
        return nom;
    }
    private static void declararPreguntesRespostes(String[] coleccioPreguntes, String[] coleccioRespostes) {
        String ruta = "src/resources/preguntes.txt", line;
        BufferedReader br;
        String[] dades;
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(ruta));
            while ((line = br.readLine()) != null) {
                dades = line.split(";");
                coleccioPreguntes[i] = dades[0];
                coleccioRespostes[i] = dades[1];
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No s'ha trobat el fitxer");
        } catch (IOException e) {
            System.out.println("Error en la lectura del fitxer");
        }
    }
    private static boolean finalJoc(int encerts, int numPreguntes, String nomUsuari) {
        int errors = numPreguntes - encerts;
        float percentatgeEncerts = encerts*100/numPreguntes;
        String tornarAjugar, infoFinal;
        boolean exit = false, repetirJoc;
        Date data = new Date();

        System.out.println("Has encertat " + encerts + " preguntes i has fallat " + errors + " preguntes");
        if (percentatgeEncerts == 100) {
            System.out.println("Has encertat el " + percentatgeEncerts + "% de les preguntes, ets un crack!");
        } else if (percentatgeEncerts >= 67) {
            System.out.println("Has encertat el " + ((double)Math.round(percentatgeEncerts * 100d) / 100d) + "% de les preguntes, henorabona!");
        } else if (percentatgeEncerts >= 34) {
            System.out.println("Has encertat el " + ((double)Math.round(percentatgeEncerts * 100d) / 100d) + "% de les preguntes, no està res malament!");
        } else {
            System.out.println("Has encertat el " + ((double)Math.round(percentatgeEncerts * 100d) / 100d) + "% de les preguntes, has de millorar!");
        }

        infoFinal = nomUsuari + ";Encerts:" + encerts + ";Errors:" + errors + ";" + data;
        guardarinfoPartida(infoFinal);

        do {
            System.out.print("Vols tornar a jugar? (yes/no) ");
            tornarAjugar = Teclat.llegirString();
            tornarAjugar = tornarAjugar.toLowerCase();
            if (tornarAjugar.equals("yes") || tornarAjugar.equals("no")) {
                exit = true;
            } else {
                System.out.println("Introdueix un valor de resposta vàlid");
            }
        } while (!exit);
        if (tornarAjugar.equals("yes")) {
            repetirJoc = true;
        } else {
            repetirJoc = false;
        }
        return repetirJoc;
    }

    private static void guardarinfoPartida(String infoFinal) {
        Path ruta = Paths.get("src/resources/registrePartides.txt");

        try {
            Files.writeString(ruta, infoFinal, StandardOpenOption.APPEND);
            Files.writeString(ruta, System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error en l'escriptura del fitxer");
        }
    }

    private static int comprovarResposta(String[] respostesJoc, int torn, int encerts, String resposta) {
        if (respostesJoc[torn].equals(resposta)) {
            encerts++;
            System.out.println("Pregunta encertada!");
        } else {
            System.out.println("Pregunta fallada!");
        }
        return encerts;
    }
    private static String preguntarUsuari(String[] preguntesJoc, int torn) {
        String resposta;
        boolean exit = false;
        do {
            System.out.print(preguntesJoc[torn] + " (yes/no) ");
            resposta = Teclat.llegirString();
            resposta = resposta.toLowerCase();
            if (resposta.equals("yes") || resposta.equals("no")) {
                exit = true;
            } else {
                System.out.println("Introdueix un valor de resposta vàlid");
            }
        } while (!exit);
        return resposta;
    }
    private static void escollirPreguntesRandom(String[] preguntesJoc, String[] coleccioPreguntes, String[] coleccioRespostes, String[] respostesJoc) {
        boolean exit = true;
        int numRandom;
        do {
            boolean salir = false;
            int j = 0;
            numRandom = (int)Math.floor(Math.random()*20);
            do {
                if (preguntesJoc[j].equals("!")){
                    preguntesJoc[j] = coleccioPreguntes[numRandom];
                    guardarRespostesJoc(respostesJoc, coleccioRespostes, numRandom, j);
                    salir = true;
                } else if (preguntesJoc[j].equals(coleccioPreguntes[numRandom])) {
                    salir = true;
                } else {
                    j++;
                }
            } while (!salir);
            for (int i = 0; i < preguntesJoc.length; i++) {
                if (preguntesJoc[i].equals("!")) {
                    exit = false;
                } else {
                    exit = true;
                }
            }
        } while (!exit);
    }
    private static void guardarRespostesJoc(String[] respostesJoc, String[] coleccioRespostes, int numRandom, int j) {
        respostesJoc[j] = coleccioRespostes[numRandom];
    }
    private static String[] mascaraPreguntesJoc(int numPreguntes) {
        String[] preguntesJoc = new String[numPreguntes];
        for (int i = 0; i < preguntesJoc.length; i++) {
            preguntesJoc[i] = "!";
        }
        return preguntesJoc;
    }
    private static String[] mascaraRespostesJoc(int numPreguntes) {
        String[] respostesJoc = new String[numPreguntes];
        for (int i = 0; i < respostesJoc.length; i++) {
            respostesJoc[i] = "!";
        }
        return respostesJoc;
    }
    private static int demanarNumPreguntes() {
        System.out.print("Amb quantes preguntes vols jugar? (entre 5 i 20) ");
        int numPreguntes;
        boolean exit = false;
        do {
            numPreguntes = Teclat.llegirInt();
            if (numPreguntes < 5 || numPreguntes > 20) {
                System.out.println("El número de preguntes ha de ser entre 5 i 20");
            } else {
                exit = true;
            }
        } while (!exit);
        return numPreguntes;
    }
}