
AR Web server and portal
v. 1.0.1


#INSTALLATION AND QUICK START
----------------------------
Before you start it is supposed that you have set up the service center for Live+Gov correctly. See at https://github.com/LiveGov/.


1. Download and setup XAMPP for linux or windows
2. Copy the AugReal folder to your http root folder
3. Change in config.inc.php the mysql username and password to your xampp setup. Change _sc_url to the service center url. 
4. run installer.php  
5. run index.php and put the credentials that you have obtained from the Service Center.

Further information can be found in the deliverables of Live+Gov [http://liveandgov.eu]



#VISUAL RECOGNITION (only for linux servers)
-------------------------------------------

### ReconEngine

Contains all the libraries needed for training and testing a model

recognizing:  /ReconEngine$ ./recognize -r 0.jpg 

output: STRING

training: /ReconEngine$ ./recognize -t -p folderofmodelimages -n folderofnegativeimages1 -n folderofnegativeimages2 

output: /ReconEngine$ modelFile.vrmod

### PHP

recognizer.php calls recogition





