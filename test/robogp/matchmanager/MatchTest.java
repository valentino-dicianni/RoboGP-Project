package robogp.matchmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robogp.robodrome.Direction;
import robogp.robodrome.Position;
import robogp.robodrome.Rotation;


import java.util.ArrayList;

import static org.junit.Assert.*;

public class MatchTest {


    private Match testMatch;
    private MatchRobot r1;
    private MatchRobot r2;
    private ArrayList<MatchRobot> list;
    private static final Position CellPosition = new Position(4,0,Direction.E);
    private static final Position PitCellPosition = new Position(5,0,Direction.E);
    private static final Position WallCellPosition = new Position(6,0,Direction.E);
    private static final Position OutRobodromePosition = new Position(8,0,Direction.W);


    @Before
    public void setUp() throws Exception {
        testMatch = Match.getInstance("testRobodrome",2,1, Match.EndGame.AllButLast,false);
        r1 = new MatchRobot("robot-red","red",0);
        r2 = new MatchRobot("robot-blue","blue",0);
        r1.setLifePoints(100);
        r2.setLifePoints(100);
        list = new ArrayList<>();
        System.out.println("Setting it up!");

    }

    @Test
    public void moveSubPhase() throws Exception {
        r1.setRegistry(1,new MatchInstruction("move1",1, Rotation.NO,200));
        r1.setRegistry(2,new MatchInstruction("move2",2, Rotation.NO,200));
        r1.setRegistry(3,new MatchInstruction("move3",3, Rotation.NO,200));
        r1.setRegistry(4,new MatchInstruction("backup",-1, Rotation.NO,200));
        r1.setRegistry(5,new MatchInstruction("turnL",0, Rotation.CCW90,200));
        list.add(r1);
        testMatch.getOwnedRobots().put("playerTest",list);

        r1.setPosition(CellPosition.clone());
        assertTrue(testMatch.moveSubPhase(1).equals("robot-red:1:E:NO"));
        assertTrue(testMatch.moveSubPhase(2).equals("robot-red:1:E:NO,robot-red:1:E:NO"));
        assertTrue(testMatch.moveSubPhase(3).equals("robot-red:1:E:NO,robot-red:1:E:NO,robot-red:1:E:NO"));
        assertTrue(testMatch.moveSubPhase(4).equals("robot-red:1:W:NO"));
        assertTrue(testMatch.moveSubPhase(5).equals("robot-red:0:E:CCW90"));
        System.out.println("\t-->TEST Move: PASSED ");

        r1.setPosition(PitCellPosition.clone());
        assertTrue(testMatch.moveSubPhase(2).equals("robot-red:1:E:NO,robot-red:1:E:NO,robot-red:pitfall"));
        r1.setPosition(PitCellPosition.clone());
        assertTrue(testMatch.moveSubPhase(3).equals("robot-red:1:E:NO,robot-red:1:E:NO,robot-red:pitfall"));
        System.out.println("\t-->TEST PitCell: PASSED");

        r1.setPosition(WallCellPosition.clone());
        assertTrue(testMatch.moveSubPhase(1).equals("robot-red:1:E:NO"));
        r1.setPosition(WallCellPosition.clone());
        assertTrue(testMatch.moveSubPhase(2).equals("robot-red:1:E:NO"));
        r1.setPosition(WallCellPosition.clone());
        assertTrue(testMatch.moveSubPhase(5).equals("robot-red:0:E:CCW90"));
        System.out.println("\t-->TEST WallCell: PASSED");

        r1.setPosition(OutRobodromePosition.clone());
        assertTrue(testMatch.moveSubPhase(1).equals("robot-red:1:W:NO,robot-red:outofrobodrome"));
        r1.setPosition(OutRobodromePosition.clone());
        assertTrue(testMatch.moveSubPhase(2).equals("robot-red:1:W:NO,robot-red:outofrobodrome"));
        r1.setPosition(CellPosition.clone());
        assertTrue(testMatch.moveSubPhase(4).equals("robot-red:1:W:NO,robot-red:outofrobodrome"));
        System.out.println("\t-->TEST OutOfRobodrome: PASSED ");







    }

    @Test
    public void robodromeActivationSubPhase() throws Exception {
    }

    @Test
    public void lasersAndWeaponsSubPhase() throws Exception {
    }

    @Test
    public void getAvailableRobots() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Running: tearDown");

    }



}