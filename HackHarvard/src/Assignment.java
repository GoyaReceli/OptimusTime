import java.util.Date;

public class Assignment {

	private Date startDate;
	private Date dueDate;
	private double durationHour;
	private String assignmentName;
	private String description;
	
	public Assignment(Date startDate, Date dueDate, double durationHour, String assignmentName, String description){
		this.startDate = startDate;
		this.dueDate = dueDate;
		this.durationHour = durationHour;
		this.assignmentName = assignmentName;
		this.description = description;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public double getDurationHour() {
		return durationHour;
	}

	public void setDurationHour(int durationHour) {
		this.durationHour = durationHour;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isDueToday(Date today) {
		return (dueDate.getYear() == today.getYear() && dueDate.getMonth() == today.getMonth() && dueDate.getDay() == today.getDay());
	}
}
