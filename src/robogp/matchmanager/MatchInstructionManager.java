package robogp.matchmanager;

import java.util.ArrayList;

public class MatchInstructionManager {

    private static MatchInstructionManager singleInstance;

    private ArrayList<MatchInstruction> pool;

    //trimToSize()
    //Trims the capacity of this ArrayList instance to be the list's current size.

    private MatchInstructionManager(String path) {
        // legge da file csv la lista di istruzioni possibili, ogni riga è divisa da virgole, i campi sono: instruction-name,startpriority,endpriority,step
        // crea match instruction con il metodo getInstructionByName di matchinstr e aggiunge alla pool
        // quando si prendono oggetti dalla pool, si fa una copia di quesi nella pool di riserva
        // quando si vuole resettare la pool, si prendono quelli della pool di riserva e si mettono
        // nella pool principale (svutando la pool di riserva) così da non leggere dal file ogni volta
    }

    public static MatchInstructionManager getInstance() {
        if (MatchInstructionManager.singleInstance == null) {
            MatchInstructionManager.singleInstance = new MatchInstructionManager("schede_instruzione1.csv");
        }
        return MatchInstructionManager.singleInstance;
    }



}
