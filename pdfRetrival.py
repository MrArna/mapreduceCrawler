#!/usr/bin/env

import requests
import urllib
import os
import sys, getopt
import xml.etree.ElementTree as ET
import re

def find_between( s, first, last ):
    try:
        start = s.index( first ) + len( first )
        end = s.index( last, start )
        return s[start:end]
    except ValueError:
        return ""



def download_file(id):
   	#os.system(' "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=' + id + '&contentType=Conference+Publications" -o ' + id + '.txt')
   	
   	#page = urllib.request.urlopen("http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=5283163")
   	
   	#path = find_between(page.read().decode("utf-8"),'"pdfPath":"/iel5/','",')

   	#print(page.read().decode("utf-8"))
   	os.system("wget -v 'http://ieeexplore.ieee.org/stampPDF/getPDF.jsp?tp=&isnumber=&arnumber=" + id + "' -O " + id + ".pdf")
   	#if(os.path.getsize(id + ".pdf") < 100 *1024):
   		#os.remove(id + ".pdf")
   	print("Completed")


author = "Santambrogio"
year = "2016"
text = "FPGA"


try:
  opts, args = getopt.getopt(sys.argv[1:],"ha:y:t:",["author=","year=","text="])
except getopt.GetoptError:
  print ('pdfRetrival.py [-t <author>] [-s <year>] [-b <text>]')
  sys.exit(2)
for opt, arg in opts:
  if opt == '-h':
     print ("pdfRetrival.py [-t <toaddr>] [-s <subject>] [-b <body>]")
     sys.exit()
  elif opt in ("-t", "--author"):
     author = arg
  elif opt in ("-s", "--year"):
     year = arg
  elif opt in ("-b", "--text"):
     text = arg



r = requests.get("http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?querytext=" + text + "&au=" + author + "&pys=" + year)
root = ET.fromstring(r.content)
count = 0
for doc in root.findall("document"):
	print(doc.find("title").text)
	#print(doc.find("doi").text)
	download_file(doc.find("arnumber").text)
