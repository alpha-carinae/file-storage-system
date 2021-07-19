# Simple File Storage System

## Installation

1. Download the project.
2. Go to the project directory.
3. Run ```docker build -t file-storage-system:latest .```
4. Run ```docker-compose up -d```

Default port is 8085 for the API.

## API Endpoints

The base API endpoint is ```<host-name>/api/file```.

| Action   | Type    |  Payload                    |
| :------- | :------ | :-------------------------- |
| create   | POST    | file(s)                     |
| update   | POST    | file(s)                     |
| delete   | DELETE  | fileName                    |
| list     | GET     |                             |
| download | GET     | fileName, version(optional) |

### ```/api/file/create```

- Type: ```POST```
- ```Content-Type``` must be ```multipart/form-data```.
- One or more file(s) can be sent in the multipart request.

### ```/api/file/update```

- Type: ```POST```
- ```Content-Type``` must be ```multipart/form-data```.
- One or more file(s) can be sent in the multipart request.

### ```/api/file/delete```

- Type: ```DELETE```
- ```name``` of the file must be provided.
- All versions of the given file will be deleted.

*Example:* ```<host-name>/api/file/delete?name=example.dll```

### ```/api/file/list```

- Type: ```GET```
- Result will contain the latest version of each file.

*Example output:*

```json
[
  {
    "name": "file1.exe",
    "version": 1,
    "size": "1.8 MB",
    "dateModified": "2021-07-19T13:34:22.000+00:00"
  },
  {
    "name": "file2.dat",
    "version": 2,
    "size": "319.8 KB",
    "dateModified": "2021-07-19T13:41:30.000+00:00"
  }
]
```

### ```/api/file/download```

- Type: ```GET```
- ```name``` of the file must be provided.
- ```version``` of the file is optional. The latest version will be downloaded if it's not provided.

*Example:*  
```<host-name>/api/file/download?name=file1.docx```  
```<host-name>/api/file/download?name=file1.docx&version=1```

<br/>
<br/>

API definition can also be seen on: ```<host-name>/swagger-ui.html```.


## Notes
- I haven't used any ORM because there is only one entity. ```schema-mysql.sql``` is used to create the table.
- This project utilizes streaming when uploading and downloading files.
- Max file size is set to 65MB as ```max_allowed_packet``` is 67,108,864 bytes for mysql. This value can be changed on
  the server and ```spring.servlet.multipart.max-file-size``` and ```spring.servlet.multipart.max-request-size```
  properties can be set accordingly.
- For simplicity, versioning is done by the name of the file. Hash of the data could(should) also be taken into account.   