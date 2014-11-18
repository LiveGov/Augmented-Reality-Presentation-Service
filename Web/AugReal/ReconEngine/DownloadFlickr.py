#!/usr/bin/python2.7
# This script is used to Download pictures from Flickr with flickr module
import sys
import os
import flickr

# Get what to download
concept = sys.argv[1];
# No of images to download
NofImgs = sys.argv[2];
#API key
flickr.API_KEY = '2ccaeb5a12596b064d4e88ddfae9e07b'


numPagesToGet = int(NofImgs)//101 + 1;
#Gid = '418618@N20'
#concept = 'Door'
# Make folder to store the photos
parentfolder = os.path.dirname(os.path.realpath(__file__)); #Get this file folder
os.mkdir(parentfolder + '/content/' + concept,0777)
abspath = parentfolder + '/content/' + concept
os.system('chmod 0777 %s' % abspath)
# Add predefined tags (not any for time being)

concept = concept.replace('+',','); 

#litter,garbage
init_tags = [concept]
tags      = []
tags      = tags + init_tags
photos    = []
concept = concept.replace(',','+');
i = 1

for tag in tags:
    print "Searching for tags: %s" %tag
    phototags = []
    
    for p in range (1, numPagesToGet+1):
	    #group = flickr.Group(Gid)
        #photos = group.getPhotos(per_page = 100, page = p)
        photos = flickr.photos_search(tags = tag, per_page = 100, page = p, sort='relevance')
    
    #print "Got page %d" %p

    	for photo in photos:
            try:
                url = photo.getURL(urlType='source')
                print url
            except:
                pass
            
            phototags =photo.__getattr__('tags')

            if phototags and url != None:
                try:
                    print 'ggg'
                    os.system('wget -c -O %stest%s.jpg %s' % (parentfolder + '/content/' + concept + '/', i,url))
                    os.system('chown jimver %stest%s.jpg' % (parentfolder + '/content/' + concept + '/', i))
                    os.system('chmod 6777 %stest%s.jpg' % (parentfolder + '/content/' + concept + '/', i))
                except:
                    print "Unexpected error"
                
                i = i+1
                
                if i > int(NofImgs):
                    break

