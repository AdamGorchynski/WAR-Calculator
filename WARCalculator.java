import java.io.*;
import java.util.*;

public class WARCalculator {									//Calculate offensive WAR for qualified batters in the 2017 season
	public static void main (String args[]) throws FileNotFoundException {
		
		Scanner statsFile = new Scanner(new File("2017Stats.dat")); //file containing stats
		
		do {
		String Name = statsFile.next();				//name of player
		String Team = statsFile.next();				//team they played for
		String Position = statsFile.next();			//primary position
		double Average = statsFile.nextDouble();			//batting average
		double Games = statsFile.nextDouble();			//# of games at position
		double AB = statsFile.nextDouble();				//At Bats
		double runsScored = statsFile.nextDouble();
		double hits = statsFile.nextDouble();
		double doubles = statsFile.nextDouble();
		double triples = statsFile.nextDouble();
		double HR = statsFile.nextDouble();
		double RBI = statsFile.nextDouble();
		double BB = statsFile.nextDouble();
		double HBP = statsFile.nextDouble();
		double Strikeouts = statsFile.nextDouble();
		double SF = statsFile.nextDouble();
		double SH = statsFile.nextDouble();
		double SB = statsFile.nextDouble();
		double CS = statsFile.nextDouble();
		double GIDP = statsFile.nextDouble();
		double Errors = statsFile.nextDouble();
		double singles = (hits - doubles - triples - HR);
		double PA = AB + BB + SF + SH + HBP;
		
		double wOBA = wOBACalculator(AB, singles, doubles, triples, HR, BB, HBP, SF);
		
		
	
		double wRAA = wRAACalculator(wOBA, PA);
		double wSB = wSBCalculator(SB, CS, singles, BB, HBP);
		double wGIDP = wGIDPCalculator(AB, GIDP);
		
		
		double battingRuns = BattingRunsCalculator(wRAA);
		double baseRunningRuns = BaseRunningRunsCalculator(wGIDP, wSB);
		//double fieldingRuns = FieldingRunsCalculator(Errors, Games);
		double positionAdjustment = PositionalAdjustment(Position, Games);
		double runsPerWin = RunsPerWin();
		double replacementLevel = ReplacementLevelRuns(Games, PA, runsPerWin);
		
		double babeRuthWAR = (battingRuns + baseRunningRuns + positionAdjustment + replacementLevel)/runsPerWin;
		double MLBWAR = (babeRuthWAR/24)*162;
		System.out.println(Name);
	/*	System.out.println(battingRuns);
		System.out.println(baseRunningRuns);
		//System.out.println(fieldingRuns);
		System.out.println(positionAdjustment);
		System.out.println(runsPerWin);
		System.out.println(replacementLevel);*/
		System.out.println("Babe ruth WAR: " +babeRuthWAR);
		System.out.println("MLB WAR: " +MLBWAR);
		
		} while (statsFile.hasNextLine());
		
	}
	
	public static double wOBACalculator (double AB, double singles, double doubles, double triples, double HR, double BB, double HBP, double SF) {
		
		
		
		//use constants from 2017 season
		
		double wOBA = ((0.693*BB) + (0.723*HBP) +(0.877*singles) + (1.232*doubles) + (1.552*triples) + (1.980*HR))/(AB+BB+SF+HBP);
		return wOBA;
	}
	
	public static double wRAACalculator(double wOBA, double PA) {
	
		double lgwOBA = 0.379; 		//calculated from league stats
		double wOBAScale = 1.185;	//for the 2017 season provided by Fangraphs
		double wRAA = ((wOBA-lgwOBA)/wOBAScale)*PA;
		return wRAA;
	}
	
	public static double BattingRunsCalculator(double wRAA) {		//no park adjustments, wRAA = batting runs
		double battingRuns = wRAA;
		return battingRuns;
	}
	
	public static double wSBCalculator(double SB, double CS, double singles, double BB, double HBP) {
		
		double lgwSB = 0.0341;
		double wSB = (SB*0.200) + (CS*(-0.423)) - (lgwSB*(singles + BB + HBP));
		return wSB;
		
	}
	
	public static double wGIDPCalculator(double AB, double GIDP) {
	
		double wGIDP = GIDP/(AB*40);
		return wGIDP;
		
	}
	
	public static double BaseRunningRunsCalculator(double wGIDP, double wSB) {		//Assume 0 UBR for all players - not enough data to calculate
			
			double UBR = 0;
			double baseRunningRuns = UBR + wSB - wGIDP;
			return baseRunningRuns;
		
	}
	
	/*public static double FieldingRunsCalculator(double Errors, double Games) {			//Assume 0 UZR for all players - not enough data to calculate
													//use errors compared to league average
		double lgErrorsPerGame = 4.780;
		double errorsPerGame = Errors/(Games);
		double wErrorsPerGame = (lgErrorsPerGame - errorsPerGame);
		
		return wErrorsPerGame;
		
	} */
	
	public static double PositionalAdjustment(String Position, double Games) {	//Assume only 1 position played throughout season
	
		double adjust = 0;
		
		if (Position.equals("C")) {
			adjust = 12.5;
			}
		else if (Position.equals("1B")) {
			adjust = -12.5;
			}
		else if (Position.equals("2B")) {
			adjust = 2.5;
			}
		else if (Position.equals("3B")) {
			adjust = 2.5;
			}
		else if (Position.equals("SS")) {
			adjust = 7.5;
			}
		else if (Position.equals("LF")) {
			adjust = -7.5;
			}
		else if (Position.equals("CF")) {
			adjust = 2.5;
			}
		else if (Position.equals("RF")) {
			adjust = -7.5;
			}
		else if (Position.equals("DH")) {
			adjust = -17.5;
			}
			
		double positionAdjustment = ((Games/7)/24)*adjust;
		
		return positionAdjustment;
	}
	
	public static void LeagueAdjustment() {		//will be 0, only one league
	
	}
	
	public static double ReplacementLevelRuns(double Games, double PA, double runsPerWin) {
	
	
		double lgPA = 5547;
		double MLBGames = (Games/24)*162;
		double RLR = (570*(MLBGames/2430))*(runsPerWin/lgPA)*PA;
		
		return RLR;
		
	}
	
	public static double RunsPerWin() {
			
			double runsScored = 1333;
			double inningsPitched = 1041;
			
			double runsPerWin = (7*(runsScored/inningsPitched)*1.5) +3;
			
			return runsPerWin;
			
	} 
	
}