package matchTests;

import robogp.matchmanager.MatchInstructionManager;

public class MatchInstructionManagerTests {
    public static void main(String args[]) {
        MatchInstructionManager inst = MatchInstructionManager.getInstance();
        System.out.println(inst.toString());
        System.out.println(inst.getRandomInstructionPool(10).toString());
        System.out.println(inst.toString());
        System.out.println(inst.getRandomInstructionPool(40).toString());
        System.out.println(inst.toString());
        inst.resetInstructionPool();
        System.out.println(inst.toString());
    }
}
