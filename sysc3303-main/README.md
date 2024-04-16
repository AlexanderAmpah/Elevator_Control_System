[Java Style Guide](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf)

Group members: Alexander Ampah #101169097, Christian Green #101200708, Gavin MacNabb #101158976, Philip Wanczycki #101141921

Files Elevator subsystem thread:

    elevatorSim/ElevatorSubsystem.java
    elevatorSim/Elevator.java
    elevatorSim/ElevatorState.java
    elevatorSim/IdleState.java
    elevatorSim/WorkingState.java
    elevatorSim/ArrivalState.java

Floor subsystem thread:

    floorSim/FloorSubsystem.java
    floorSim/Floor.java

Scheduler thread:

    scheduleServer/Scheduler.java

Test files:
  
  elevatorSim/ElevatorSubsystemTest.java
  elevatorSim/ElevatorTest.java
  elevatorSim/ElevatorStateTest.java
  floorSim/FloorSimTest.java
  scheduleServer/SchedulerTest.java

input file:

    resources/testinput1.txt

UML diagrams:

    Class_Diagram.pdf
    SequenceDiagram.pdf
    ElevatorStateMachineDiagram.pdf

Set up instructions: Run SchedulerMain.java, FloorMain.java, and ElevatorMain.java

Breakdown of responsibilities:

    Floor Subsystem: Alexander
    Scheduler: Gavin, Philip and Alexander
    Elevator Subsystem: Philip and Christian
    UML diagrams: Gavin, Philip and Alexander
    
