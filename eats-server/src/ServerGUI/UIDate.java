package ServerGUI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UIDate{
	private Date d;
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMMMM dd");
	
	public UIDate(Date d) {
		this.d = d;
	}
	
	public String toString() {
		return sdf.format(d);
	}
	
	public Date getDate() {
		return d;
	}
}
