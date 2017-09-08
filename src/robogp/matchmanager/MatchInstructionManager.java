package robogp.matchmanager;

import robogp.robodrome.Robodrome;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MatchInstructionManager {

    private static MatchInstructionManager singleInstance;

    private ArrayList<MatchInstruction> pool;
    private ArrayList<MatchInstruction> usedInstructionsPool;

    //trimToSize()
    //Trims the capacity of this ArrayList instance to be the list's current size.

    private MatchInstructionManager(String path) {
        // legge da file csv la lista di istruzioni possibili, ogni riga è divisa da virgole, i campi sono: instruction-name,startpriority,endpriority,step
        // crea match instruction con il metodo getInstructionByName di matchinstr e aggiunge alla pool
        // quando si prendono oggetti dalla pool, si fa una copia di quesi nella pool di riserva
        // quando si vuole resettare la pool, si prendono quelli della pool di riserva e si mettono
        // nella pool principale (svutando la pool di riserva) così da non leggere dal file ogni volta
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException ex) {
            System.out.println("UNABLE TO FIND INSTRUCTIONS FILE");
            return;
        }
        String[] all = stringBuilder.toString().split("\\n");

        usedInstructionsPool = new ArrayList<MatchInstruction>();
        pool = new ArrayList<MatchInstruction>();

        for (String line : all) {
            // data[0] è il nome della scheda istruzione
            String[] data = line.split(",");
            int start = Integer.parseInt(data[1]);
            int end = Integer.parseInt(data[2]);
            int step = Integer.parseInt(data[3]);
            for (int i = start; i <= end; i = i + step) {
                this.pool.add(MatchInstruction.getInstructionByName(data[0], i));
            }
        }
    }

    public static MatchInstructionManager getInstance() {
        if (MatchInstructionManager.singleInstance == null) {
            MatchInstructionManager.singleInstance = new MatchInstructionManager("tiles/schede_instruzione.csv");
        }
        return MatchInstructionManager.singleInstance;
    }

    public ArrayList<MatchInstruction> getRandomInstructionPool(int poolSize) {
        if (poolSize > this.pool.size())
            throw new ArrayIndexOutOfBoundsException("not enough instructions available");
        Collections.shuffle(this.pool);
        ArrayList<MatchInstruction> randomPool = new ArrayList<MatchInstruction>(this.pool.subList(0, poolSize));
        usedInstructionsPool.addAll(randomPool);
        //this.pool.removeRange(0, poolSize);
        this.pool.removeAll(randomPool);
        this.pool.trimToSize(); // necessary??
        return randomPool;
    }

    public void resetInstructionPool() {
        pool.addAll(usedInstructionsPool);
        usedInstructionsPool.clear();
    }

    @Override
    public String toString() {
        return "Current pool size: "+this.pool.size()+"\nInstructions given: "+this.usedInstructionsPool.size();
    }

}
