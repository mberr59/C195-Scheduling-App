README

   - Title : C195-Scheduling-App.

   - The purpose of this application is to create a user interface for users to Add/Modify/Delete Customers and
     Appointments from the database and creates reports of the saved data for the users to observe within the application.


   - Author - Micah Berry
   - Phone - 256-861-6648
   - E-mail - mberr59@wgu.edu
   - Application version - 1.0
   - Date - 8/25/2022

   - IDE version number - IntelliJ Community 2021.1.3
   - JDK version - Java SE 17.0.1
   - JavaFX version - 17.0.1 compatible with JDK version 17.0.1

   - User enters their username and password into the login screen and then connects. This will bring them to their
     Appointment screen showing a list of all of their appointments. They can filter the appointments by first select
     either the Monthly radio button or Weekly radio button then clicking the filter button. They can then reload the
     entire list by clicking the refresh button in the top left. Adding appointments is as easy as clicking the Add
     button at the bottom of the screen and entering the necessary data. Modify an appointment by first selecting the
     appointment that you are needing to modify then click the Update button. To Delete an appointment the user must
     first select the appointment they wish to delete then click the delete button.

     Users can also go to a list of all customers by clicking the customer list button in the top left of the screen.
     From this screen they can add, modify, and delete customers. To modify or delete the user must first select the
     customer from the list then click the corresponding button. Note that all appointments tied to a customer must first
     be deleted before the customer can be deleted.

     Reports can be accessed from the Appointments screen. The reports that can be created are: a list of all customer
     appointments by type and month, a report showing the schedule for the selected contact, and the final report
     shows a schedule for all contacts.



   - The report I chose to use shows all the appointments in the system and sorts them for each contact. I thought this
     would be using to see every appointment coming up for each contact. Users are able to click one button to access
     this data instead of having to run a separate report for each contact. As the company's contact list grows this
     could be very time-consuming.

   - MySQL Connector driver version number - mysql-connector-java-8.0.25
