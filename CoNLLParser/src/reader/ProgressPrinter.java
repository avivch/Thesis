package reader;

import java.math.*;
import java.util.*;

public class ProgressPrinter {
	private int sentencesNo;
	private long startTime;
	private Timer timer;
	private int finished;
	private int lastLineLength;
	
	public ProgressPrinter(int sentencesNo) {
		this.sentencesNo = sentencesNo;
		this.startTime = 0;
		this.timer = new Timer();
		this.finished = 0;
		this.lastLineLength = 0;
	}
	
	public void reportProgress(int finished) {
		if (finished == 0) {
			startTime = System.currentTimeMillis();
			String line = "0% done. Remaining Time: Unknown.";
			System.out.print(line);
			lastLineLength = line.length();
			
			final ProgressPrinter printer = this;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					long currentTime = System.currentTimeMillis();
					String line = calcPercentage(printer.finished) + "% done. Remaining Time: " + calcRemainingTime(currentTime - startTime, printer.finished) + ".";
					for (int i = 0; i < lastLineLength; i++)
						System.out.print("\b \b");
					System.out.print(line);
					lastLineLength = line.length();
				}
			}, 750, 750);
		}
		else if (finished < sentencesNo)
			this.finished = finished;
		else if (finished == sentencesNo) {
			timer.cancel();
			long endTime = System.currentTimeMillis();
			String line = "100% done. Elapsed Time: " + getPrettyTimeFormat(endTime - startTime) + ".";
			for (int i = 0; i < lastLineLength; i++)
				System.out.print("\b \b");
			System.out.println(line);
			this.finished = finished;
			lastLineLength = line.length();
		}
	}
	
	private double calcPercentage(int finished) {
		return new BigDecimal((double)finished / sentencesNo * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	private String calcRemainingTime(long elapsedTime, int finished) {
		long remainingTime = (long)((double)elapsedTime / finished * (sentencesNo - finished));
		return getPrettyTimeFormat(remainingTime);
	}
	
	private String getPrettyTimeFormat(long timeSpan) {
		long seconds = timeSpan / 1000;
		long minutes = seconds / 60;
		seconds %= 60;
		long hours = minutes / 60;
		minutes %= 60;
		String secondsStr = getUnitFormat(seconds, "Seconds", "Second");
		String minutesStr = getUnitFormat(minutes, "Minutes", "Minute");
		String hoursStr = getUnitFormat(hours, "Hours", "Hour");
		String ret = "";
		if (hoursStr.length() > 0)
			ret += hoursStr;
		if (minutesStr.length() > 0) {
			if (ret.length() > 0)
				ret += ", ";
			ret += minutesStr;
		}
		if (secondsStr.length() > 0) {
			if (ret.length() > 0)
				ret += ", ";
			ret += secondsStr;
		}
		return ret;
	}
	
	private String getUnitFormat(long count, String plural, String singular) {
		if (count == 0)
			return "";
		if (count == 1)
			return "1 " + singular;
		return count + " " + plural;
	}
}
