public class Contador {
    private static int contadorGlobal = 0;

    public static synchronized void incrementar() {
        contadorGlobal++;
        System.out.println(Thread.currentThread().getName() +
                " incrementó el contador a: " + contadorGlobal);
    }

    public static synchronized void decrementar() {
        contadorGlobal--;
        System.out.println(Thread.currentThread().getName() +
                " decrementó el contador a: " + contadorGlobal);
    }

    public static int getValor() {
        return contadorGlobal;
    }
}
