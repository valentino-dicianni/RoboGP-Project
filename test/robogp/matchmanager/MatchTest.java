package robogp.matchmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robogp.robodrome.Direction;
import robogp.robodrome.Position;
import robogp.robodrome.Rotation;


import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MatchTest {


    private Match testMatch;
    private MatchRobot r1;
    private MatchRobot r2;
    private MatchRobot r3;
    private ArrayList<MatchRobot> list;
    private static final Position CellPosition = new Position(4,0,Direction.E);
    private static final Position PitCellPosition = new Position(5,0,Direction.E);
    private static final Position WallCellPosition = new Position(6,0,Direction.E);
    private static final Position OutRobodromePosition = new Position(8,0,Direction.W);
    private static final Position PushPosition = new Position(1,0,Direction.E);
    private static final Position PushedPosition = new Position(1,1,Direction.E);
    private static final Position PushedPosition2 = new Position(1,2,Direction.E);

    private static final Position LaserPosition = new Position(0,7,Direction.W);
    private static final Position LaserPosition2 = new Position(1,7,Direction.W);
    private static final Position ExpressBeltPosition = new Position(3,5,Direction.W);
    private static final Position NormalBeltPosition = new Position(3,4,Direction.W);
    private static final Position RotatorPosition = new Position(3,6,Direction.W);

    private static final Position FireLaserOutPosition = new Position(2,0,Direction.W);
    private static final Position FireLaserPosition = new Position(2,0,Direction.E);
    private static final Position FireLaserOppositePosition = new Position(2,5,Direction.W);
    private static final Position FireLaserMiddlePosition = new Position(2,3,Direction.W);





    @Before
    public void setUp() throws Exception {
        testMatch = Match.getInstance("testRobodrome",2,1, Match.EndGame.AllButLast,false);
        r1 = new MatchRobot("robot-red","red",0);
        r2 = new MatchRobot("robot-blue","blue",0);
        r3 = new MatchRobot("robot-yelow","yellow",0);
        r1.setLifePoints(100);
        r2.setLifePoints(100);
        r3.setLifePoints(100);
        list = new ArrayList<>();
        System.out.println("** TEST Setup **");

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


        list.add(r2);
        testMatch.getOwnedRobots().put("playerTest",list);
        r2.getRegistry(1).setLocked(true);
        r1.setPosition(PushPosition.clone());
        r2.setPosition(PushedPosition.clone());
        assertTrue(testMatch.moveSubPhase(1).equals("robot-red:1:E:NO:robot-blue"));
        list.add(r3);
        r3.getRegistry(1).setLocked(true);
        r1.setPosition(PushPosition.clone());
        r2.setPosition(PushedPosition.clone());
        r3.setPosition(PushedPosition2.clone());
        assertTrue(testMatch.moveSubPhase(1).equals("robot-red:1:E:NO:robot-blue§robot-yelow"));
        System.out.println("\t-->TEST PushRobot: PASSED ");

    }

    @Test
    public void robodromeActivationSubPhase() throws Exception {
        list.add(r1);
        testMatch.getOwnedRobots().put("playerTest",list);

        r1.setPosition(ExpressBeltPosition.clone());
        assertTrue(testMatch.robodromeActivationSubPhase().equals("robot-red:1:N:NO,robot-red:1:N:NO"));
        System.out.println("\t-->TEST ExpressBelt: PASSED ");

        r1.setPosition(NormalBeltPosition.clone());
        assertTrue(testMatch.robodromeActivationSubPhase().equals("robot-red:1:N:NO"));
        System.out.println("\t-->TEST NormalBelt: PASSED ");

        r1.setPosition(RotatorPosition.clone());
        assertTrue(testMatch.robodromeActivationSubPhase().equals("robot-red:0:W:CCW90"));
        System.out.println("\t-->TEST Rotator: PASSED ");

        r1.setPosition(LaserPosition.clone());
        assertTrue(testMatch.robodromeActivationSubPhase().equals("robot-red:N:laserhit"));
        list.add(r2);
        testMatch.getOwnedRobots().put("playerTest",list);
        r2.setPosition(LaserPosition2.clone());
        assertTrue(testMatch.robodromeActivationSubPhase().equals("robot-red:N:laserhit,robot-blue:N:laserhit"));
        System.out.println("\t-->TEST LaserCell: PASSED ");
    }

    @Test
    public void lasersAndWeaponsSubPhase() throws Exception {
        list.add(r1);
        testMatch.getOwnedRobots().put("playerTest",list);

        r1.setPosition(WallCellPosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:E:0:1:false:true"));
        r1.setPosition(CellPosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:E:0:10:false:false"));
        r1.setPosition(FireLaserPosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:E:0:10:false:false"));
        r1.setPosition(FireLaserOutPosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:W:0:0:false:false"));

        list.add(r2);
        testMatch.getOwnedRobots().put("playerTest",list);
        r1.setPosition(FireLaserPosition.clone());
        r2.setPosition(FireLaserOppositePosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:E:0:5:robot-blue:false,robot-blue:W:5:0:robot-red:false"));

        list.add(r3);
        testMatch.getOwnedRobots().put("playerTest",list);
        r3.setPosition(FireLaserMiddlePosition.clone());
        assertTrue(testMatch.lasersAndWeaponsSubPhase().equals("robot-red:E:0:3:robot-yelow:false,robot-blue:W:5:3:robot-yelow:false,robot-yelow:W:3:0:robot-red:false"));
        System.out.println("\t-->TEST Laser&Weapons: PASSED ");



    }

    @After
    public void tearDown() throws Exception {
        testMatch.setOwnedRobots(new HashMap<>());
        System.out.println("** TEST TearDown **");

    }



}