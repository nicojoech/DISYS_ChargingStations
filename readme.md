## DISYS_ChargingStation

Documentation, Lessons learned, Unit test decisions, GIT link, tracked time. 


Folder Structure: 

DISYS_ChargingStation (root)
- DataCollectionDispatcher
- DataCollectionReceiver
- JavaFX
- PDFGenerator
- SpringBoot
- StationDataCollector



To run the code: 
	Start the database in Docker, with "docker compose up" in cmd
	
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
		
		
		
		
### Lessons learned
Clemens: 
- Learning to send Json through the message Queue
- How to create Json in Java
- Creative thinking of how to pack and unpack data which is sent through the Queue
- How the distributed parts of the System work as one
- Using SQL in Java

Nico:
- Access databases with Java (JDBC)
- Create artifacts from Java code (PDF)
- Connect different services through implementing a MQ
- Develop "loosely" coupled services
- Test code through Unit Tests


### Unit test decisions

#### shouldReturnSpecificCustomer - PDFGenerator

This unit test was chosen/created to verify the correctness of the getCustomerFromDB method in the GeneratorService class.
By setting up an expected customer with known values and comparing it to the actual customer returned by the method, we can verify the accuracy of the database connection and query execution.

#### shouldReturnTotalAmount - PDFGenerator

This test aims to verify the accuracy of the getTotalAmount method in the GeneratorService class.
The test helps us to see whether the method correctly calculates the total amount based on the charge information provided. This ensures that the correct amount is written into the PDF.

#### shouldConvertJsonProperly - StationDataCollector

At last, the advantage of implementing this test is to ensure that the convertToJson method in the ServiceSDC class correctly converts a list of ChargeInfoSDC objects into the expected JSON format.
It helps us to see if the data is correctly sent to the subsequent services, which is crucial for their further processing.



### GIT link:
https://github.com/nicojoech/DISYS_ChargingStations




### Tracked time/roles in project: 
#### Clemens:

Documentation, DataCollectionDispatcher, StationDataCollector

Time: hard to exactly track without knowing we had to do it at the start of this course. Only for project coding: ~10 hours
	
#### Nico: 

Unit Tests, DataCollectionReceiver, PDFGenerator

Time: coding + creating unit tests + debugging ~20 hours

#### Florian:

...

Time: ... ~ hours