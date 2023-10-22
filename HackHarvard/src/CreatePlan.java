import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class CreatePlan {
	
	private final int DAILY_MAX_WORK_CONST = 8;
	
	private ArrayList<Assignment> assignments;
	private ArrayList<Assignment> completedAssignments;
	public int dailyMaxWork;
	
	private ArrayList<Date> dateNodes;
	// index [][0]: startDateIndex, index [][1]: dueDateIndex
	private int[][] intervalNodes;
	private ArrayList<Long> intervalProportion;
	
	private ArrayList<ArrayList<Assignment>> parallelAssignments;
	private ArrayList<ArrayList<Double>> parallelWeights;
	
	private ArrayList<Double> HRSleep;
	private ArrayList<Double> HRDay;
	
	public CreatePlan(int dailyMaxWork) {
		assignments = new ArrayList<Assignment>();
		completedAssignments = new ArrayList<Assignment>();
		this.dailyMaxWork = dailyMaxWork;
		dateNodes = new ArrayList<Date>();
		intervalProportion = new ArrayList<Long>();
		HRSleep = new ArrayList<Double>();
		HRDay = new ArrayList<Double>();
		parallelAssignments = new ArrayList<ArrayList<Assignment>>();
		parallelWeights = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < dateNodes.size(); i++) {
			parallelAssignments.add(new ArrayList<Assignment>());
			parallelWeights.add(new ArrayList<Double>());
		}
	}
	
	public CreatePlan() {
		assignments = new ArrayList<Assignment>();
		completedAssignments = new ArrayList<Assignment>();
		this.dailyMaxWork = DAILY_MAX_WORK_CONST;
		dateNodes = new ArrayList<Date>();
		intervalProportion = new ArrayList<Long>();
		HRSleep = new ArrayList<Double>();
		HRDay = new ArrayList<Double>();
		parallelAssignments = new ArrayList<ArrayList<Assignment>>();
		parallelWeights = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < dateNodes.size(); i++) {
			parallelAssignments.add(new ArrayList<Assignment>());
			parallelWeights.add(new ArrayList<Double>());
		}
	}
	
	public int getDailyMaxWork() {
		return dailyMaxWork;
	}

	public void setDailyMaxWork(int dailyMaxWork) {
		this.dailyMaxWork = dailyMaxWork;
	}
	
	public CreatePlan(ArrayList<Assignment> assignment) {
		assignments = assignment;
	}
	
	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}
	
	public boolean addAssignment(Date startDate, Date dueDate, double durationHour, String assignmentName, String description) {
		Assignment newAssignment = new Assignment(startDate, dueDate, durationHour, assignmentName, description);
		return addAssignment(newAssignment);
	}
	
	public boolean addAssignment(Assignment newAssignment) {
		for(Assignment assignment : assignments) {
			if(assignment.getAssignmentName().equals(newAssignment.getAssignmentName())) return false;
		}
		assignments.add(newAssignment);
		return true;
	}
	
	public int totalExpectedWorkTime() {
		int currExpectedWorkTime = 0;
		for(Assignment assignment : assignments) {
			currExpectedWorkTime += assignment.getDurationHour();
		}
		return currExpectedWorkTime;
	}
	
	public boolean finishAssignment(Assignment assignment) {
		if(!assignments.remove(assignment)) return false;
		completedAssignments.add(assignment);
		return true;
	}
	
	public String overwork() {
		return "EMPLOYEE HAS TO WORK MORE THAN " + Integer.toString(dailyMaxWork) + " HOURS!";
	}
	
	public boolean updateNodes() {
		dateNodes = new ArrayList<Date>();
		for(Assignment assignment : assignments) {
			addDateNodes(assignment.getStartDate());
			addDateNodes(assignment.getDueDate());
		}
		intervalNodes = new int[assignments.size()][2];
		for(Assignment assignment : assignments) {
			if(!addIntervalNodes(assignment, assignments.indexOf(assignment))) return false;
		}
		addIntervalProportions();
		return true;
	}
	
	public void addDateNodes(Date newDate){
		if(dateNodes.isEmpty()) {
			dateNodes.add(newDate);
			return;
		}
		
		for(Date date : dateNodes) {
			if(date.compareTo(newDate) == 0) {
				return;
			}
			else if(date.compareTo(newDate) > 0) {
				dateNodes.add(dateNodes.indexOf(date), newDate);
			}
		}
		dateNodes.add(newDate);
	}
	
	public boolean addIntervalNodes(Assignment newAssignment, int index) {
		boolean isStartDateFound = false;
		for(Date date : dateNodes) {
			if(!isStartDateFound) {
				if(date.equals(newAssignment.getStartDate())) {
					isStartDateFound = true;
					intervalNodes[index][0] = dateNodes.indexOf(date);
				}
			}
			else {
				if(date.equals(newAssignment.getDueDate())) {
					intervalNodes[index][1] = dateNodes.indexOf(date);
					return true;
				}
			}
		}
		return false;
	}
	
	public void addIntervalProportions() {
		if(dateNodes.isEmpty()) return;
		
		ArrayList<Long> intervals = new ArrayList<>();
		
		long firstDate = dateNodes.get(0).getTime();
		for(int i = 1; i < dateNodes.size(); i++) {
			long time = dateNodes.get(i).getTime() - dateNodes.get(i-1).getTime();
			intervals.add(time);
		}
		long total = dateNodes.get(dateNodes.size()-1).getTime() - firstDate;
		intervals.add(total);
		
		for(int i = 0; i < intervals.size(); i++) {
			intervalProportion.add(intervals.get(i)/total);
		}
	}
	
	public void optimize(double sleepEfficiency, double avgHRSleep, double avgHRDay) {
		double todayWeight = sleepEfficiency*avgHRSleep*avgHRDay;
		
		Calendar cal = Calendar.getInstance();
		Date todayCal = cal.getTime();
		Date nextDay = dayAfter(todayCal);
		
		boolean isToday = true;
		boolean dayChange = false;
		for(Assignment assignment : assignments) {
			double restWeight = 0;
			long todayProportion = 0;
			for(int i = intervalNodes[assignments.indexOf(assignment)][0]; i < intervalNodes[assignments.indexOf(assignment)][1]; i++) {
				if(!dayChange && dateNodes.get(i).compareTo(nextDay) >= 0) {
					isToday = false;
					dayChange = true;
					restWeight = ((1-todayProportion)*todayWeight)/todayProportion;
				}
				
				double weight = (isToday) ? todayWeight : restWeight;
				long proportion = intervalProportion.get(i);
				todayProportion += proportion;
				parallelAssignments.get(i).add(assignment);
				parallelWeights.get(i).add(proportion*weight);
			}
		}
	}
	
	public void optimize() {
		optimize(1, 1, 1);
	}
	
	public double getHoursforAssignment(Date date, Assignment assignment) {
		Date tomorrow = dayAfter(date);
		double hoursWork = 0;
		for(int i = 0; i < parallelAssignments.size(); i++) {
			if(parallelAssignments.get(i).contains(assignment) && date.compareTo(dateNodes.get(i)) <= 0 && tomorrow.compareTo(dateNodes.get(i)) >= 0) hoursWork += parallelWeights.get(i).get(parallelAssignments.get(i).indexOf(assignment));
		}
		if (hoursWork >= 8) System.out.println(overwork());
		return hoursWork;
	}
	
	// Neglects leap year
	public Date dayAfter(Date date) {
		int day = date.getDate();
		day += 1;
		int month = date.getMonth();
		if(month==0 || month==2 || month==4 || month==6 || month==7 || month==9 || month==11) {
			if(day >= 31) {
				day = 0;
				month += 1;
			}
		}
		else if(month == 1) {
			if(day >= 28) {
				day = 0;
				month += 1;
			}
		}
		else {
			if(day >= 30) {
				day = 0;
				month += 1;
			}
		}
		int year = date.getYear();
		if(month >= 12) {
			month = 0;
			year += 1;
		}
		return new Date(year, month, day);
	}
	
	public double giveMean(ArrayList<Double> hrList) {
		double hrTotal = 0;
		for(double hr : hrList) {
			hrTotal += hr;
		}
		return hrTotal/hrList.size();
	}
	
	public double avgHRSleep(double avgHR) {
		HRSleep.add(avgHR);
		double mean = giveMean(HRSleep);
		avgHR /= mean;
		if(avgHR > 1) return 1;
		return avgHR;
	}
	
	public double avgHRDay(double avgHR) {
		HRDay.add(avgHR);
		double mean = giveMean(HRDay);
		avgHR /= mean;
		if(avgHR > 1) return 1;
		return avgHR;
	}
	
	public void printAllDays() {
		Date curr = new Date(2023, 10, 22);
		for(int i = 0; i < parallelAssignments.size(); i++) {
			if(curr.compareTo(dateNodes.get(i)) <= 0 && dayAfter(curr).compareTo(dateNodes.get(i)) >= 0) {
				for(int j = 0; j < parallelAssignments.get(i).size(); j++) {
					System.out.println(parallelAssignments.get(i).get(j).getAssignmentName() + ": " + parallelWeights.get(i).get(j));
				}
			}
			else {
				curr = dayAfter(curr);
			}
		}
	}
	
	public static void main(String[] args) {
		CreatePlan plan = new CreatePlan();
		File sampleAssignments = null;
		Scanner scanner = null;
		try {
			sampleAssignments = new File("sampleAssignments.txt");
			scanner = new Scanner(sampleAssignments);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		int sizeAssignments = Integer.parseInt(scanner.next());
		for(int i = 0; i < sizeAssignments; i++) {
			Date startDate = new Date(Integer.parseInt(scanner.next()),Integer.parseInt(scanner.next()),Integer.parseInt(scanner.next()));
			Date dueDate = new Date(Integer.parseInt(scanner.next()),Integer.parseInt(scanner.next()),Integer.parseInt(scanner.next()));
			Assignment a = new Assignment(startDate, dueDate, scanner.nextDouble(), scanner.next(), scanner.next());
			plan.addAssignment(a);
			
		}
		scanner.close();
		plan.updateNodes();
		
		File sampleData = null;
		Scanner scanner2 = null;
		try {
			sampleData = new File("sampledata.txt");
			scanner2 = new Scanner(sampleData);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		int sizeHealthData = Integer.parseInt(scanner2.nextLine());
		for(int i = 0; i < sizeHealthData; i++) {
			plan.optimize(scanner2.nextDouble(), (double) scanner2.nextInt(), (double) scanner2.nextInt());
			plan.printAllDays();
		}
	}
}
