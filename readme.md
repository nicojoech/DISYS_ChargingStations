DISYS_ChargingStation 
Documentation, Lessons learned, Unit test decisions, GIT link, tracked time. 


Folder Structure: 
	DISYS_ChargingStation
		- DataCollectionDispatcher
		- DataCollectionReceiver
		- JavaFX
		- PDFGenerator
		- SpringBoot
		- StationDataCollector



To Run the code: 
	Start the Database in Docker, with "docker compose up" in cmd
	
	Download JavaFX on https://openjfx.io/ with JavaFX SDK 17 or later

	Open IntelliJ Ultimate and have Java JDK 17 or later installed
	
	Open DISYS_ChargingStation folder in IntelliJ 
		- Start main functions of following projects: 
			- DataCollectionDispatcher
			- DataCollectionReceiver
			- PDFGenerator
			- StationDataCollector
		- Start HelloApplication Class of JavaFX project
		- Start Application Class of Springboot project
		
		
		
		
Lessons learned
	Clemens: 
		- Learning to send Json throught the message Queue
		- How to create Json in Java
		- Creative thinking of how to pack and unpack data which is sent throught the Queue
		- How the distributed parts of the System work as one
		- Using SQL in Java


Unit test decisions



GIT link: https://github.com/nicojoech/DISYS_ChargingStations




Tracked time/rolls in project: 
	Clemens: Documentaion, DataCollectionDispatcher, StationDataCollector
		- Time: hard to exactly track without knowing we had to do it at the start of this course. Only for project coding: ~10 Hours
