public class TareaHilo implements Runnable {
    private String operacion;
    private int repeticiones;

    public TareaHilo(String operacion, int repeticiones) {
        this.operacion = operacion;
        this.repeticiones = repeticiones;
    }

    @Override
    public void run() {
        for (int i = 0; i < repeticiones; i++) {
            try {
                if (operacion.equals("SUMAR")) {
                    Contador.incrementar();
                } else if (operacion.equals("RESTAR")) {
                    Contador.decrementar();
                }

                Thread.sleep(500);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("*** " + Thread.currentThread().getName() + " TERMINÓ ***");
    }
}
