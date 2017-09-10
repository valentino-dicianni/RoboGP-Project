package matchTests;

import robogp.matchmanager.MatchInstructionManager;

import java.util.ArrayList;

public class MatchInstructionManagerTests {
    public static void main(String args[]) {
        /*MatchInstructionManager inst = MatchInstructionManager.getInstance();
        System.out.println(inst.toString());
        System.out.println(inst.getRandomInstructionPool(10).toString());
        System.out.println(inst.toString());
        System.out.println(inst.getRandomInstructionPool(40).toString());
        System.out.println(inst.toString());
        inst.resetInstructionPool();
        System.out.println(inst.toString());*/

        ArrayList arr = new ArrayList(84);
        arr.add(45, "st:ee:45");
        arr.add(15, "st:ee:15");
        arr.add(65, "st:ee:65");

        System.out.println(arr.toString());
    }
}
