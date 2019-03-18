package manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import model.ActionUtilPair;

public class FileManager
{
	public static void writeToFile(List<ActionUtilPair[][]> lstActionUtilPairs, String fileName) {

		StringBuilder sb = new StringBuilder();
		String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

		for (int col = 0; col < Const.NUM_COLS; col++) {
			for (int row = 0; row < Const.NUM_ROWS; row++) {

				Iterator<ActionUtilPair[][]> iter = lstActionUtilPairs.iterator();
				while(iter.hasNext()) {

					ActionUtilPair[][] actionUtil = iter.next();
					sb.append(decimalFormat.format(
							actionUtil[col][row].getUtil()).substring(0, 6));

					if(iter.hasNext()) {
						sb.append(",");
					}
				}
				sb.append("\n");
			}
		}

		writeToFile(sb.toString().trim(), "data\\" + fileName + ".csv");
	}

	public static void writeToFile(String content, String fileName)
	{
		try
		{
			FileWriter fw = new FileWriter(new File(fileName), false);

			fw.write(content);
			fw.close();

			//System.out.println("\nSuccessfully saved results to \"" + fileName + "\".");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
