iCashBook
=============

A cashbook application developed in JAVA. Useful for small scale companies to manage cash flow.

Usage
=============
In main page, Use ALT+LEFT Arrow key to move to previous date (ALT+RIGHT Arrow also works as expected).

Hover the mouse on any transaction to show the date when that transaction was last modified.

Data-access layer changed from Java serialization mechanism to relational database model.


Possible rooms for improvement
=============

Normal statements to prepared statements to enhance performance.

Specify use of different databases. Search for my_db in the db.DatabaseModel class, and modify it to point to the database to operate on.

GUI code can be fine-tuned to pop-up a dialog to change the database as application run-time.

Currently making use of the Apache Derby database (Embedded). Can be modified to instead use another RDBMS such as MySQL.
