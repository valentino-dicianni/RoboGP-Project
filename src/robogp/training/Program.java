package robogp.training;

import java.util.ArrayList;
import java.util.Collections;

public class Program {
    private ArrayList<TrainingInstruction> instructions;
    private TrainingInstruction currentInstruction;
    private boolean running;

    public Program() {
        this.running = false;
        this.instructions = new ArrayList<>();
        this.currentInstruction = null;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * set the program running if is not running
     * and has at least one instruction to execute and there is no currentInstruction.
     * @return true if the program was set to running correctly, false otherwhise
     */
    public boolean setRunning() {
        if (this.running || this.instructions.size() == 0 || this.currentInstruction != null)
            return false;
        this.running = true;
        this.currentInstruction = instructions.get(0);
        return true;
    }

    /**
     * stops the execution of the program if is running
     * the currentInstruction is set to null
     * at this point the user can edit the program and start again from the beginning
     * or exit the training mode
     */
    public void stopRunning() {
        if (!this.running)
            return;
        this.running = false;
        this.currentInstruction = null;
    }

    /**
     * set the current instruction if is present in the instruction list
     * @param instruction
     * @return true if the currentInstruction has been set correctly, false otherwise
     */
    public boolean setCurrentInstruction(TrainingInstruction instruction) {
        if (this.instructions.contains(instruction)) {
            this.currentInstruction = instruction;
            return true;
        }
        return false;
    }

    /**
     * sets the current instruction to the next one in the list
     * if initially the currentInstruction is the last instruction in the list
     * then Program is set to not running and false is returned.
     * @return true if the instruction was set correctly, false otherwise.
     */
    public boolean goToNextInstruction() {
        if (!this.running)
            return false;
        int newindex = this.instructions.indexOf(this.currentInstruction) + 1;
        try {
            this.currentInstruction = this.instructions.get(newindex);
        } catch (IndexOutOfBoundsException e) {
            this.stopRunning();
            return false;
        }
        return true;
    }

    public TrainingInstruction getCurrentInstruction() {
        return currentInstruction;
    }

    /**
     * appends the given instruction to the end of the list of instructions
     * @param instruction
     * @return true if add is successful, false otherwise
     */
    public boolean loadInstruction(TrainingInstruction instruction) {
        if (this.running)
            return false;
        // TODO: check if instruction is already in list?
        return this.instructions.add(instruction);
    }

    public void removeAll() {
        if (this.running)
            return;
        this.instructions.clear();
    }

    public boolean remove(TrainingInstruction instruction) {
        if (this.running)
            return false;
        return this.instructions.remove(instruction);
    }

    /**
     * swaps the two given instructions
     * @param instruction1
     * @param instruction2
     * @return true if the swap was successful, false if one of the given instructions was not found
     */
    public boolean swap(TrainingInstruction instruction1, TrainingInstruction instruction2) {
        if (this.running)
            return false;
        int instruction1index = this.instructions.indexOf(instruction1);
        int instruction2index = this.instructions.indexOf(instruction2);
        if (instruction1index < 0 || instruction2index < 0)
            return false;
        Collections.swap(this.instructions, instruction1index, instruction2index);
        return true;
    }
}
