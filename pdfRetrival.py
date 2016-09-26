#!/usr/bin/env

import requests
import urllib
import os
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
   	page = urllib.request.urlopen("http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=" + id + "&contentType=Conference+Publications")
   	
   	path = find_between(page.read().decode("utf-8"),'"pdfPath":"/iel5/','",')
   	#print(path)
   	os.system("wget 'http://ieeexplore.ieee.org/ielx5/" + path.replace("/" + str(id),"") + "?tp=&arnumber=" + id + "' -O " + id + ".pdf")
   	#f = open(id + '.txt', 'wb')
   	#f.write(page.read())
   	#f.close()
   	print("Completed")


#conn = httplib.HTTPSConnection("api.crossref.org")
#conn.request("GET", "/works?query=santa&filter=has-full-text:true")
#r = conn.getresponse()
#data = r.read().decode('utf-8')
#json_obj = json.loads(data)
#print(json_obj["message"])
#for val in json_obj["message"]["items"]:
#	print(val["URL"] + "\n")

#conn = httplib2.HTTPSConnection("ieeexplore.ieee.org")
r = requests.get("http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?querytext=java&au=Wang&hc=10&rs=11&sortfield=ti&sortorder=asc")

#print("%x" % OpenSSL.SSL.OPENSSL_VERSION_NUMBER)


root = ET.fromstring(r.content)
count = 0
for doc in root.findall("document"):
	print(doc.find("title").text)
	#print(doc.find("doi").text)
	download_file(doc.find("arnumber").text)

#conn.close()



#buffer = StringIO()
#c = pycurl.Curl()
#c.setopt(c.URL, 'http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?querytext=java&au=Wang&hc=10&rs=11&sortfield=ti&sortorder=asc')
#c.setopt(c.WRITEDATA, buffer)
#c.perform()
#c.close()

#body = buffer.getvalue()
# Body is a string in some encoding.
# In Python 2, we can print it without knowing what the encoding is.
#print(body)

