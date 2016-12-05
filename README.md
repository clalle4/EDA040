# EDA040-Project User guide
The system contains two seperate programs divided into packages. One client and one server side. The server is to be installed on an AXIS camera to use, sending images captured through an internet conection. The client is to be run on a computer that one wishes to connect to the camera(s) so that it can recieve and display the images. The client can hold up to two cameras.

##Server manual
The server is installed on the cameras according to the guide on the EDA040 homepage. After installation one can run the software through the command "./Main XXXX". "XXXX" should be the portnumber one wishes to run the server on, if an incorrect port is used the program will terminate. Once the server is up and running it can be left alone untill terminated by exiting the camera.

If the camera detects motion it will notify its client so, and the client will switch to "movie mode" (see client description below).

##Client manual 
The client is run by starting the program through the console, inputting the correct ports for the cameras as arguments. Once the camera has connected its recieved images will be displayed in the two windows. The client will automaticly swap modes when movement is detected on a camera.

Movie Mode: Images are displayed as quickly as possible synchronising so that the cameras are run at the same speed

Idle mode: A new image is recieved once every 5 seconds to conserve network. The cameras are not synchronized.

To exit the client program, close the window.
