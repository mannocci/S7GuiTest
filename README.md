<<<<<<< HEAD
# JRivitScreen
##

Progetto pannello di controllo per il device RivitControl
Data inizio del progetto 23/05/2023
##
Data primo rilascio ver. 1.0.0 08/02/2025
=======
# rivit-control
![Image](https://user-images.githubusercontent.com/35871303/227468749-b1190343-1498-477a-92b6-160e16d4fa10.png)


New generation RivitControl ver. 1.0 Aprile 2023


It is implemented for quality control for placing rivets and inserts.

## Architecture

List of sources and their functionality

### Pressure and throw control

It has the task of controlling the shot, management of the shot specimen 
(creation, modification, deletion, choice based on the job). 
Setup: shot speed setting, number of samples, other parameters ...
It has access to the database where it writes/reads the setup table
and the array of shots in string format with numbers divided by a comma, 
in order to be able to easily create an array

### Main process, created via socket in localhost and listening on the other IP

It's listening for instructions to write/read the database
Responds to external requests about the status of the tool and the associated job.
If it's Server also records the operations of other devices.
The Server can accept multi connections

### Program dedicated to the ModBus protocol

Using the historic library https://libmodbus.org


### Program dedicated to the Fidonet Siemes protocol ( like Moka7 )

Project overview
Moka7 is the Java port of Snap7 Client. It’s not a wrapper, i.e. you don’t have an 
interface code that loads snap7.dll (or .so) but it’s a pure Java implementation 
of the S7Protocol.
Moka7 is deployed as a set of source code classes that you can use in your Java
project to communicate with S7 PLCs.
Not all functions are ported but the list of the PLC managed is the same, 
it’s designed to work with small hardware java-based, Android phones or even for
large projects which don’t needs of extended control functions.

Main features
1. Fully standard Java code without any dependencies.
2. Fully multiplatform, virtually every hardware with an Ethernet adapter able to run a
   JVM can be connected to an S7 PLC.
3. Packed protocol headers to improve performances.
4. Helper class to access to all S7 types without worrying about Little-Big endian
   convention.

### Program dedicated to display control

One task listens for the keystrokes and another shows the outputs and inputs on the display
Can access the database directly


### Generic notes

The application user is adminsb in his home some setup and information files are recorded. Applications are
stored in the /usr/bin folder: jar files, bash sh files, Python files, executable files written in C and more
A file version.txt contains the current version number of the system and individual application processes




   
>>>>>>> origin/main
