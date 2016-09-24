import httplib
conn = httplib.HTTPSConnection("http://api.crossref.org")
conn.request("GET", "/prefixes/10.1016/works?filter=from-pub-date:2010-01,until-pub-date:2010-01")
r1 = conn.getresponse()
print r1.status, r1.reason
data1 = r1.read()
conn.request("GET", "/prefixes/10.1016/works?filter=from-pub-date:2010-01,until-pub-date:2010-01")
r2 = conn.getresponse()
print r2.status, r2.reason
data2 = r2.read()
conn.close()