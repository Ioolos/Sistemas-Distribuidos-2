public class SimuladorHilos {
    public static void main(String[] args) {
        System.out.println("Simulación de hilos");
        System.out.println("Valor inicial del contador: " + Contador.getValor());
        System.out.println("---------------------------");

        // Sumas
        Thread hilo1 = new Thread(new TareaHilo("SUMAR", 3), "Hilo-Sumador-1");
        Thread hilo2 = new Thread(new TareaHilo("SUMAR", 3), "Hilo-Sumador-2");

        // Restas
        Thread hilo3 = new Thread(new TareaHilo("RESTAR", 2), "Hilo-Restador-1");
        Thread hilo4 = new Thread(new TareaHilo("RESTAR", 2), "Hilo-Restador-2");

        // Inicialización de hilos
        hilo1.start();
        hilo2.start();
        hilo3.start();
        hilo4.start();

        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
            hilo4.join();
        } catch (InterruptedException e) {
            System.out.println("Programa interrumpido");
        }

        System.out.println("---------------------------");
        System.out.println("RESULTADO FINAL: " + Contador.getValor());
        System.out.println("Todos los hilos terminaron");
    }
}

