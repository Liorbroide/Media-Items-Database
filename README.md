## Media-Items-Database
This repository consists two files:
# Script.sql:
- This sql file consists the script of two table creation querries: MediaItems, and Similarity.
- MediaItems: Holds movies' ID by chronological insertion, it's movie title, it's production year, and it's title length.
- Similarity: Holds two MIDs, and their Similarity in three columns.
- AutoIncrement Trigger: Increments the max MID by one, and assigns it to the inserted tuple. As well as calculating it's title length.
- MaximalDistance Function: Returns the maximal distance between all items. (Distance between two production years).
- SimCalculation Function: Calculates the similarity between two media items, which is defined as: Similarity = 1- (two_items_distance) / (maximal_distance).

# JDBC_Impl.java:
- This file implements a connection to the SQL database using JDBC, and applying different function using the database's data.
- The file has one class, named Assignment, which has three arguments: VPN username, VPN password, and the connection site. 
- FileToDatabase function: The function receives the Media Items' CSV as directory, extracts it's data and inserts it in to the database's MediaItems.
- CalculateSimilarity: The function calculates the similarity between every pair of items in Mediaitems, and inserts the results into Similarity Table.
- printSimilarItems: The function receives one argument: MID. The function prints all the titles and similarity value of the similar items, where the similarity is at least 0.3
