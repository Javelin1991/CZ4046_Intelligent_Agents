package util;

import java.text.DecimalFormat;

import classes.ActionUtilPair;

public class ActionUtilHelper {
	
	public static void displayPolicy(final ActionUtilPair[][] utilArr) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("|");
        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
        	sb.append("---|");
        }
        sb.append("\n");
        
		for (int row = 0; row < Const.NUM_ROWS; row++) {
			sb.append("|");
			for (int col = 0; col < Const.NUM_COLS; col++) {
				
				sb.append(String.format(" %s |", utilArr[col][row].getActionStr()));
			}
	        
	        sb.append("\n|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	sb.append("---|");
	        }
	        sb.append("\n");
	    }
		
		System.out.println(sb.toString());
	}
	
	public static void displayUtilities(final ActionUtilPair[][] utilArr) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("|");
        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
        	sb.append("--------|");
        }
        sb.append("\n");
        
        String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        
		for (int row = 0; row < Const.NUM_ROWS; row++) {
			
			sb.append("|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	sb.append("--------|".replace('-', ' '));
	        }
	        sb.append("\n");
			
			sb.append("|");
			for (int col = 0; col < Const.NUM_COLS; col++) {
				
				sb.append(String.format(" %s |",
						decimalFormat.format(utilArr[col][row].getUtil())));
			}
			
			sb.append("\n|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	sb.append("--------|".replace('-', ' '));
	        }
	        sb.append("\n");
	        
	        sb.append("|");
	        for(int col = 0 ; col < Const.NUM_COLS ; col++) {
	        	sb.append("--------|");
	        }
	        sb.append("\n");
	    }
		
		System.out.println(sb.toString());
	}

}
