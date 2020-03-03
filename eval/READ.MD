Project has 4 modules
	dataloader - Load data files on request from storage service
		( port 9092 )
		basePath in application.properties must be changed to the folder where the data files are
		linesToRead defines how many lines will be processed each run

	storage - stores data and serves on request to staorage service
		( port 9091 )

	warehouse - distributes a product in n partitions
		( port 9090 )
		acceptableDifference is the criteria to trigger a redistribution , in cents
		Usage is locahost:9090/distribute/product/number

	discoevalserver - discovery server among all services
		( port 8761 )

project builds with mvn clean package on this folder.
To run the project open a dos window for each module and use mvn spring-boot:run
Start with discovery server and allow some time for all the services to register and come up.

 