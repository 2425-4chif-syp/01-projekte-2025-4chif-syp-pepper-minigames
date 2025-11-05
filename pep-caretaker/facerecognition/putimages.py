import boto3

s3 = boto3.resource('s3')

# Get list of objects for indexing
images=[('emil.jpeg','Emil Avdic'),
      ]

# Iterate through list to upload objects to S3   
for image in images:
    file = open(image[0],'rb')
    object = s3.Object('pepper-user-images','index/'+ image[0])
    ret = object.put(Body=file,
                    Metadata={'FullName':image[1]})
    
print("Uploaded: " + image[0] + " for " + image[1])
