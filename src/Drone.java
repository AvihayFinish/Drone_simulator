
import javax.imageio.ImageIO;

import java.awt.Graphics;
// import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Drone {
	private double gyroRotation;
	private Point sensorOpticalFlow;

	private Point pointFromStart;
	public Point startPoint;
	public List<Lidar> lidars;
	private String drone_img_path = "C:\\Users\\אביחי\\IdeaProjects\\Drone_Simulator\\Images\\droneImage.png";
	public Map realMap;
	private double rotation;
	private double speed;
	public static double acceleration;  // Variable to store the calculated acceleration
	private double roll;
	private double pitch;
	private double pitchRate;
	private double rollRate;
	private double batteryPercentage; // Battery percentage (0 to 100%)
	private final long startTime; // Start time of the battery
	public List<Point> pointList;
	public List<Long> timeOfPoints;
	private double Vx;
	private double Vy;
	private double accX; // X acceleration component
	private double accY; // Y acceleration component

	private CPU cpu;

	public Drone(Map realMap) {
		startTime = System.currentTimeMillis(); // Record the start time
		this.realMap = realMap;
		this.startPoint = realMap.drone_start_point;
		pointFromStart = new Point();
		Vx = 0.0;
		Vy = 0.0;
		sensorOpticalFlow = new Point();
		lidars = new ArrayList<>();
		speed = 0.2;
		rotation = 0;
		gyroRotation = rotation;
		roll = 0.0;
		pitch = 0.0;
		accX = 0.0;
		accY = 0.0;
		pitchRate = 0.0; // Initialize pitch rate
		rollRate = 0.0;  // Initialize roll rate
		batteryPercentage = 100.0;
		timeOfPoints = new ArrayList<>();
		pointList = new ArrayList<>();
		timeOfPoints.add(startTime);
		timeOfPoints.add(startTime);
		pointList.add(pointFromStart);
		pointList.add(pointFromStart);

		cpu = new CPU(100,"Drone");
	}

	public void play() {
		cpu.play();
	}

	public void stop() {
		cpu.stop();
	}

	public void addLidar(int degrees) {
		Lidar lidar = new Lidar(this,degrees);
		lidars.add(lidar);
		cpu.addFunction(lidar::getSimulationDistance);
	}

	public Point getPointOnMap() {
		double x = startPoint.x + pointFromStart.x;
		double y = startPoint.y + pointFromStart.y;
		return new Point(x,y);
	}

	public void update(int deltaTime) {

		double distancedMoved = (speed*100)*((double)deltaTime/1000);

		Point prev = pointFromStart;
		pointFromStart =  Tools.getPointByDistance(pointFromStart, rotation, distancedMoved);
		timeOfPoints.add(System.currentTimeMillis());
		if (!prev.equals(pointFromStart)) {
			pointList.add(pointFromStart);
			pointList.remove(0);
			timeOfPoints.remove(0);
		}
		updateVxVy();
		updateAccXAccY();

		double noiseToDistance = Tools.noiseBetween(WorldParams.min_motion_accuracy,WorldParams.max_motion_accuracy,false);
		sensorOpticalFlow = Tools.getPointByDistance(sensorOpticalFlow, rotation, distancedMoved*noiseToDistance);

		double noiseToRotation = Tools.noiseBetween(WorldParams.min_rotation_accuracy,WorldParams.max_rotation_accuracy,false);
		double milli_per_minute = 60000;
		gyroRotation += (1-noiseToRotation)*deltaTime/milli_per_minute;
		gyroRotation = formatRotation(gyroRotation);

		// Update pitch and roll
		pitch += pitchRate * deltaTime / 1000.0; // Integrate pitch rate to get pitch
		roll += rollRate * deltaTime / 1000.0;   // Integrate roll rate to get roll

		// Update battery percentage
		updateBatteryPercentage();

	}

	private void updateVxVy () {
		Vx = (pointList.get(1).x - pointList.get(0).x) / (timeOfPoints.get(1) - timeOfPoints.get(0));
		Vy = (pointList.get(1).y - pointList.get(0).y) / (timeOfPoints.get(1) - timeOfPoints.get(0));
	}

	private void updateAccXAccY() {
		double timeDiff = (timeOfPoints.get(1) - timeOfPoints.get(0)) / 1000.0;
		if (timeDiff > 0) {
			accX = (pointList.get(1).x - 2 * pointList.get(0).x + pointList.get(pointList.size() - 2).x) / (timeDiff * timeDiff);
			accY = (pointList.get(1).y - 2 * pointList.get(0).y + pointList.get(pointList.size() - 2).y) / (timeDiff * timeDiff);
		}
	}

	private void updateBatteryPercentage() {
		long elapsedTime = System.currentTimeMillis() - startTime; // Elapsed time in milliseconds
		double elapsedTimeSeconds = elapsedTime / 1000.0; // Convert to seconds
		double totalBatteryDuration = 8 * 60.0; // Total battery duration in seconds (8 minutes)

		// Calculate remaining battery percentage
		batteryPercentage = 100.0 * (1 - elapsedTimeSeconds / totalBatteryDuration);
		if (batteryPercentage < 0) {
			batteryPercentage = 0;
		}
	}

	// Add methods to set the pitch and roll rates
	public void setPitchRate(double pitchRate) {
		this.pitchRate = pitchRate;
	}

	public void setRollRate(double rollRate) {
		this.rollRate = rollRate;
	}

	// Example of how you might call these methods based on gyroscope data
	public void updateGyroscopeData(double newPitchRate, double newRollRate) {
		setPitchRate(newPitchRate);
		setRollRate(newRollRate);
	}

	public static double formatRotation(double rotationValue) {
		rotationValue %= 360;
		if(rotationValue < 0) {
			rotationValue = 360 -rotationValue;
		}
		return rotationValue;
	}

	public double getRotation() {
		return rotation;
	}

	public double getGyroRotation() {
		return gyroRotation;
	}

	public Point getOpticalSensorLocation() {
		return new Point(sensorOpticalFlow);
	}


	public void rotateLeft(int deltaTime) {
		double rotationChanged = WorldParams.rotation_per_second*deltaTime/1000;

		rotation += rotationChanged;
		rotation = formatRotation(rotation);

		gyroRotation += rotationChanged;
		gyroRotation = formatRotation(gyroRotation);
	}

	public void rotateRight(int deltaTime) {
		double rotationChanged = -WorldParams.rotation_per_second*deltaTime/1000;

		rotation += rotationChanged;
		rotation = formatRotation(rotation);

		gyroRotation += rotationChanged;
		gyroRotation = formatRotation(gyroRotation);
	}

	public void speedUp(int deltaTime) {
		acceleration = 1;
		speed += (WorldParams.accelerate_per_second*deltaTime/1000);
		if(speed > WorldParams.max_speed) {
			speed = WorldParams.max_speed;
			acceleration = 0;
		}
	}

	public void slowDown(int deltaTime) {
		speed -= (WorldParams.accelerate_per_second*deltaTime/1000);
		if(speed < 0) {
			speed = 0;
		}
		acceleration = -1;
	}


	boolean initPaint = false;
	BufferedImage mImage;
	int j=0;
	public void paint(Graphics g) {
		if(!initPaint) {
			try {
				File f = new File(drone_img_path);
				mImage = ImageIO.read(f);
				initPaint = true;
			} catch(Exception ex) {

			}
		}
		//Point p = getPointOnMap();
		//g.drawImage(mImage,p.getX(),p.getY(),mImage.getWidth(),mImage.getHeight());

		for(int i=0;i<lidars.size();i++) {
			Lidar lidar = lidars.get(i);
			lidar.paint(g);
		}
	}

	public String getInfoHTML() {
		DecimalFormat df = new DecimalFormat("#.####");

		String info = "<html>";
		info += "gyroRotation: " + df.format(gyroRotation) +"<br>";
		info += "sensorOpticalFlow: " + sensorOpticalFlow +"<br>";
		info += "d0: "  + lidars.get(0).current_distance + "<br>";
		info += "d1: " + lidars.get(1).current_distance + "<br>";
		info += "d2: "  + lidars.get(2).current_distance + "<br>";
		info += "yaw: " + df.format(rotation) +"<br>";
		info += "acceleration: " + df.format(acceleration) + "<br>";
		info += "Vx: " + Vx + "<br>";
		info += "Vy: "  + Vy + "<br>";
		info += "bat: "  + df.format(batteryPercentage) + "%" +"<br>";
		info += "pitch: "  + df.format(pitch) + "<br>";
		info += "roll: " + df.format(roll) + "<br>";
		info += "accX: " + accX + "<br>";
		info += "accY: " + accY + "<br>";
		info += "</html>";

		return info;
	}
}
