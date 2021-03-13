# object-storage-flow
connect file server and develop operating specifications

[README](README.md) | [中文文档](README_zh.md)

## Features ( from Google Translate )
1. Standardize your document processing flow. At present, there is a set of my personal processing flow. Of course, you can also define your own
2. Similar to Hibernate, the file system is also treated as an object. You don't have to think about what the underlying file system is, just call the API. Want to switch file systems? Just change the configuration
3. Provide an automatically assembled osfTemplate tool class, which contains your own file processing logic
4. Automatically assemble the Cache, MQ and other middleware you are used to
5. Cooperate with Cache, MQ and other middleware to solve the problem of file redundancy
6. Borrow the TCC idea of [SEATA](http://seata.io/zh-cn/) to solve the problem of file consistency in distributed scenarios
7. Double write two same or different file systems? This can have