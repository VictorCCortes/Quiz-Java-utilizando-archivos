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
            // Inicialización de variables y arrays que contendrán las preguntas y respuestas
            String[] coleccioPreguntes = new String[20], coleccioRespostes = new String[20], preguntesJoc, respostesJoc;
            int numPreguntes, torn = 0, encerts = 0;
            String resposta, nomUsuari;

            // Pedir al usuario que introduzca su nombre
            nomUsuari = preguntarNom();

            // Cargar las preguntas y respuestas desde el archivo de texto
            declararPreguntesRespostes(coleccioPreguntes, coleccioRespostes);

            // Solicitar el número de preguntas para el juego
            numPreguntes = demanarNumPreguntes();

            // Crear arrays que almacenarán las preguntas y respuestas seleccionadas para este juego
            preguntesJoc = mascaraPreguntesJoc(numPreguntes);
            respostesJoc = mascaraRespostesJoc(numPreguntes);

            // Seleccionar preguntas aleatorias de la colección cargada
            escollirPreguntesRandom(preguntesJoc, coleccioPreguntes, coleccioRespostes, respostesJoc);

            // Iniciar el ciclo de preguntas
            do {
                // Preguntar al usuario y comprobar si su respuesta es correcta
                resposta = preguntarUsuari(preguntesJoc, torn);
                encerts = comprovarResposta(respostesJoc, torn, encerts, resposta);
                torn++;
            } while(torn < numPreguntes);

            // Finalizar el juego y preguntar si quiere repetir
            repetirJoc = finalJoc(encerts, numPreguntes, nomUsuari);
        } while (repetirJoc);
    }

    // Función para pedir el nombre del usuario
    private static String preguntarNom() {
        String nom;
        System.out.print("Introdueix el nom d'usuari per aquesta partida: ");
        nom = Teclat.llegirString();
        return nom;
    }

    // Función para leer las preguntas y respuestas desde un archivo de texto
    private static void declararPreguntesRespostes(String[] coleccioPreguntes, String[] coleccioRespostes) {
        String ruta = "src/resources/preguntes.txt", line; // Ruta del archivo
        BufferedReader br;
        String[] dades; // Array temporal para dividir cada línea en pregunta y respuesta
        int i = 0;
        try {
            // Leer el archivo línea por línea
            br = new BufferedReader(new FileReader(ruta));
            while ((line = br.readLine()) != null) {
                dades = line.split(";"); // Separar pregunta y respuesta con el delimitador ';'
                coleccioPreguntes[i] = dades[0];
                coleccioRespostes[i] = dades[1];
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No s'ha trobat el fitxer"); // Error si no se encuentra el archivo
        } catch (IOException e) {
            System.out.println("Error en la lectura del fitxer"); // Error de lectura
        }
    }

    // Función que maneja el final del juego
    private static boolean finalJoc(int encerts, int numPreguntes, String nomUsuari) {
        int errors = numPreguntes - encerts; // Calcular errores
        float percentatgeEncerts = encerts * 100 / numPreguntes; // Calcular porcentaje de aciertos
        String tornarAjugar, infoFinal;
        boolean exit = false, repetirJoc;
        Date data = new Date(); // Fecha actual

        // Mostrar estadísticas finales del jugador
        System.out.println("Has encertat " + encerts + " preguntes i has fallat " + errors + " preguntes");
        if (percentatgeEncerts == 100) {
            System.out.println("Ets un crack!");
        } else if (percentatgeEncerts >= 67) {
            System.out.println("Henorabona!");
        } else if (percentatgeEncerts >= 34) {
            System.out.println("No està res malament!");
        } else {
            System.out.println("Has de millorar!");
        }

        // Guardar la información de la partida en un archivo
        infoFinal = nomUsuari + ";Encerts:" + encerts + ";Errors:" + errors + ";" + data;
        guardarinfoPartida(infoFinal);

        // Preguntar al usuario si quiere volver a jugar
        do {
            System.out.print("Vols tornar a jugar? (yes/no) ");
            tornarAjugar = Teclat.llegirString().toLowerCase();
            if (tornarAjugar.equals("yes") || tornarAjugar.equals("no")) {
                exit = true; // Salir si la respuesta es válida
            } else {
                System.out.println("Introdueix un valor de resposta vàlid");
            }
        } while (!exit);

        repetirJoc = tornarAjugar.equals("yes");
        return repetirJoc;
    }

    // Función para guardar la información de la partida en un archivo
    private static void guardarinfoPartida(String infoFinal) {
        Path ruta = Paths.get("src/resources/registrePartides.txt");
        try {
            Files.writeString(ruta, infoFinal, StandardOpenOption.APPEND); // Guardar la información de la partida
            Files.writeString(ruta, System.lineSeparator(), StandardOpenOption.APPEND); // Añadir nueva línea
        } catch (IOException e) {
            System.out.println("Error en l'escriptura del fitxer");
        }
    }

    // Comprobar si la respuesta del usuario es correcta
    private static int comprovarResposta(String[] respostesJoc, int torn, int encerts, String resposta) {
        if (respostesJoc[torn].equals(resposta)) {
            encerts++;
            System.out.println("Pregunta encertada!");
        } else {
            System.out.println("Pregunta fallada!");
        }
        return encerts;
    }

    // Preguntar al usuario una pregunta específica
    private static String preguntarUsuari(String[] preguntesJoc, int torn) {
        String resposta;
        boolean exit = false;
        do {
            System.out.print(preguntesJoc[torn] + " (yes/no) ");
            resposta = Teclat.llegirString().toLowerCase(); // Leer la respuesta del usuario
            if (resposta.equals("yes") || resposta.equals("no")) {
                exit = true; // Validar la respuesta
            } else {
                System.out.println("Introdueix un valor de resposta vàlid");
            }
        } while (!exit);
        return resposta;
    }

    // Función para seleccionar preguntas aleatorias
    private static void escollirPreguntesRandom(String[] preguntesJoc, String[] coleccioPreguntes, String[] coleccioRespostes, String[] respostesJoc) {
        boolean exit = true;
        int numRandom;
        do {
            boolean salir = false;
            int j = 0;
            numRandom = (int)Math.floor(Math.random() * 20); // Generar un número aleatorio
            do {
                if (preguntesJoc[j].equals("!")) {
                    preguntesJoc[j] = coleccioPreguntes[numRandom]; // Asignar pregunta aleatoria
                    guardarRespostesJoc(respostesJoc, coleccioRespostes, numRandom, j); // Guardar la respuesta correspondiente
                    salir = true;
                } else if (preguntesJoc[j].equals(coleccioPreguntes[numRandom])) {
                    salir = true; // Evitar preguntas repetidas
                } else {
                    j++;
                }
            } while (!salir);

            for (int i = 0; i < preguntesJoc.length; i++) {
                if (preguntesJoc[i].equals("!")) {
                    exit = false; // Asegurar que todas las preguntas estén asignadas
                } else {
                    exit = true;
                }
            }
        } while (!exit);
    }

    // Guardar las respuestas correspondientes a las preguntas seleccionadas
    private static void guardarRespostesJoc(String[] respostesJoc, String[] coleccioRespostes, int numRandom, int j) {
        respostesJoc[j] = coleccioRespostes[numRandom];
    }

    // Crear una máscara de preguntas no asignadas
    private static String[] mascaraPreguntesJoc(int numPreguntes) {
        String[] preguntesJoc = new String[numPreguntes];
        for (int i = 0; i < preguntesJoc.length; i++) {
            preguntesJoc[i] = "!"; // Inicializar con "!" para identificar preguntas no asignadas
        }
        return preguntesJoc;
    }

    // Crear una máscara de respuestas no asignadas
    private static String[] mascaraRespostesJoc(int numPreguntes) {
        String[] respostesJoc = new String[numPreguntes];
        for (int i = 0; i < respostesJoc.length; i++) {
            respostesJoc[i] = "!"; // Inicializar con "!" para identificar respuestas no asignadas
        }
        return respostesJoc;
    }

    // Solicitar el número de preguntas con validación
    private static int demanarNumPreguntes() {
        System.out.print("Amb quantes preguntes vols jugar? (entre 5 i 20) ");
        int numPreguntes;
        boolean exit = false;
        do {
            numPreguntes = Teclat.llegirInt(); // Leer número de preguntas
            if (numPreguntes < 5 || numPreguntes > 20) {
                System.out.println("El número de preguntes ha de ser entre 5 i 20");
            } else {
                exit = true; // Validar el número de preguntas
            }
        } while (!exit);
        return numPreguntes;
    }
}
