# scheduled-incremental-kline-fetcher
## A Java Spring Boot application which periodically updates database and serves statistics of this data

Scheduling for periodic execution
JPA Derived Query Methods for statistics
Same repository object used to create different (kline interval) collections of same parity
On first execution or if there is no db or collection, there would be a little more wait for fetch procces from earliest kline and complete db.
Even if you interrupt this process in half, it will continue from where it left off in the next run.

![ConsoleCapture](https://user-images.githubusercontent.com/106110139/184238374-e73358e4-5c5c-46c0-ac1a-69f2dee4bd67.PNG)
![RestWebAPIBrowserCapture](https://user-images.githubusercontent.com/106110139/184238384-d1ef6f12-3545-41c2-8b90-353ce8728964.PNG)
![MongoExpressCapture2](https://user-images.githubusercontent.com/106110139/184238410-40676587-8763-478c-9942-b3bf5a7ab262.PNG)
![MongoExpressCapture1](https://user-images.githubusercontent.com/106110139/184238417-c9878039-42c2-4963-a42e-8dafe4db5591.PNG)
