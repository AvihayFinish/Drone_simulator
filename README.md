# Drone Simulation Project

This project simulates the movement of a drone in a 2D environment using Java. It includes classes to represent the drone, the map it navigates, GUI components for visualization, and utility classes for calculations.

## Project Structure

- **Map.java**: Represents the map on which the drone moves.
- **Painter.java**: Responsible for painting the simulation components.
- **Point.java**: Represents a point in 2D space.
- **SimulationWindow.java**: Sets up the GUI for the simulation window and handles user actions.
- **Tools.java**: Contains utility methods for calculations.
- **WorldParams.java**: Holds parameters for the simulation.

## Classes Overview

1. **Map.java**
   - Reads a map image file and converts it into a boolean array representing obstacles.
   - Provides methods to check collisions and paint the map.

2. **Painter.java**
   - Extends `JComponent` and paints the simulation components.

3. **Point.java**
   - Represents a point in 2D space.
   - Provides methods for equality comparison and string representation.

4. **SimulationWindow.java**
   - Sets up the GUI for the simulation window.
   - Manages user interactions and updates simulation parameters.
   - Controls the flow of the simulation.

5. **Tools.java**
   - Contains utility methods for calculations such as distance, rotation, noise generation, etc.

6. **WorldParams.java**
   - Holds parameters for the simulation like lidar limits, noise, speed, and accuracy.

## Information Displayed on Screen

The following information is displayed on the screen during the simulation:

- `gyroRotation`: Gyroscopic rotation of the drone.
- `sensorOpticalFlow`: Optical flow sensor data.
- `d0`, `d1`, `d2`: Distance readings from Lidar sensors.
- `yaw`: Yaw rotation of the drone.
- `acceleration`: Acceleration of the drone.
- `Vx`, `Vy`: Velocity components of the drone.
- `bat`: Battery percentage.
- `pitch`, `roll`: Pitch and roll angles of the drone.
- `accX`, `accY`: Acceleration components in X and Y directions.

## Running the Simulation

- Run the `SimulationWindow.java` class to start the simulation.
- The window will display the drone's movement and various parameters in real-time.
- The drone's behavior can be controlled based on predefined conditions like returning home after a certain time.

## Customization

- You can customize parameters in `WorldParams.java` to change simulation behavior.
- Modify the map image file in `Map.java` constructor to simulate different environments.

## Note

- This simulation provides a basic framework for drone movement and visualization.
- Feel free to extend and modify the code according to your requirements.

## Dependencies

- Java Development Kit (JDK)
- Java Swing (usually included in JDK)

## Author

- Amit Rovshiz
- Shoval Zohar
- Bar Alayof
- Avihai Finish

