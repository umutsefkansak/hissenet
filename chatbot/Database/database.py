import pymongo

client     = pymongo.MongoClient("mongodb uri")
db         = client.RagDocuments
collection = db.steps

items = collection.find()

for item in items:
    print(item)