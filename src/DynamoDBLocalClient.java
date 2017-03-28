import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This example shows you how to create a client for DynamoDB Local
 *
 * @author Dahai Guo
 */
public class DynamoDBLocalClient {


    /*
    Holds MovieDatabase inside the JSON and will be used to add
    data to DynamoDB database
     */
    private static List<MovieDatabase> movieDatabases;
    private static DynamoDB dynamoDB;
    private final static String tableName = "movie";
    private static AmazonDynamoDBClient client;

    public static void main(String args[]) {

        //Data Extraction from JSON stuff
        movieDatabases = new ArrayList<>();
        createMovieDatabaseObjects(movieDatabases); //add movie data to list


        //DynamoDB Stuff
        try {
            AWSCredentials credentials = new PropertiesCredentials(DynamoDBLocalClient.class.getResourceAsStream("credentials"));

        client = new AmazonDynamoDBClient(credentials);
        client.setEndpoint("http://localhost:8000");
        client.setSignerRegionOverride("local");

        dynamoDB = new DynamoDB(client);
        } catch (IOException e) {e.printStackTrace();}
        //Delete the table if it exists
        //TableUtils.deleteTableIfExists(client, new DeleteTableRequest(tableName));

        //Create table
        createTable(tableName, 50l, 50l, "id", "N", "title", "S");

        //Insert data into table
        insertTableDate(tableName, movieDatabases);

        IteratorSupport<Table, ListTablesResult> tables = dynamoDB.listTables().iterator();
        int numOfTables = 0;
        while (tables.hasNext()) {
            numOfTables++;
            tables.next();
        }
        System.out.printf("%d tables exist in the database.", numOfTables);

        // see what you can do with such a "dynamoDB" object
        // 1. Working with tables
        //    http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaDocumentAPIWorkingWithTables.html
        // 2. Working with indexes
        //	  http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GSIJavaDocumentAPI.html

    }

    //This method copies all the data in the movie JSONs into a MovieDatabase list
    //each MovieDatabase object corresponds to one of the 25 JSON movie files
    static void createMovieDatabaseObjects(List<MovieDatabase> movieDatabase) {

        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        File folder = new File("src/movies");

        if (folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                try {
                    movieDatabase.add(m.readValue(f, MovieDatabase.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("not directory");
        }

    }


    //data type can be either N for number or S for string
    /*

	Formula for Read Capacity (for items up to 4 kb)
	1 read capacity unit per item × 80 reads per second = 80 read capacity units

	Formula for Write Capacity (for items up to 1kb)
	1 write capacity unit per item × 100 writes per second = 100 write capacity units

	Parameter1:table name
	Parameter2: reads per second   (from what I can understand, these numbers (parameters 2 & 3 don't really matter)
	Parameter3: writes per second  (in the sense that they're more used for how amazon will bill you for using )
	Parameter4: partition key      (their database but since we're running it locally just for practice it can )
	Parameter5: data type:		   (really be anything I think. If your r/w requests throughput exceed what's provisioned throughput
	Parameter6: sort key  (by your table, DynamoDB might throttle that request. When this happens, the
	Parameter7: data type          ( request fails with an HTTP 400 code (Bad Request), accompanied by a ProvisionedThroughputExceededException.
	Parameter8: dynamoDB client    (they will obviously affect the speed of your database, but whatever*/
    static void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits, String partitionKeyName,
                            String partitionKeyType, String sortKeyName, String sortKeyType) {

        try {

			/*
			Specifies the attributes that make up the primary key for a table or an index.
			The attributes in KeySchema must also be defined in the AttributeDefinitions array.
			Each KeySchemaElement in the array is composed of:
			 */
            List<KeySchemaElement> keySchema = new ArrayList<>();
            List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();


            //Add the parition key (aka primary key) to the schema
            //For this example, the movie id will be the partition key
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(partitionKeyName)
                    .withKeyType(KeyType.HASH)); //Partition key

            //Define the partition key
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(partitionKeyName)
                    .withAttributeType(partitionKeyType));

            //add the sort key (range key) to the schema
            //For this example, the movie title will be the sort key
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(sortKeyName)
                    .withKeyType(KeyType.RANGE)); //Sort key (range key)

            //Define the sort key
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(sortKeyName)
                    .withAttributeType(sortKeyType));

            //Create table
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName) //Table Name
                    .withKeySchema(keySchema) //Key schema previously definited
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits) //with this read capacity performance
                            .withWriteCapacityUnits(writeCapacityUnits)); //with this write capacity performance

            //Set attribute definitions in the new table
            request.setAttributeDefinitions(attributeDefinitions);

            /*

             */

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }

    }

    static void insertTableDate(String tableName, List<MovieDatabase> movieDatabase){

        Table table = dynamoDB.getTable(tableName);
        List<Movie> movieList = new ArrayList<>();

        //Extract all movies from movie database
        //and add requested data to dynamoDB database
        for(MovieDatabase movieData : movieDatabase){
            for(Movie movie : movieData.getMovies()){
                try{

                    Item item = new Item()
                            .withPrimaryKey("id", movie.getId())
                            .withString("title", movie.getTitle())
                            .withInt("year", movie.getYear())
                            .withString("mpaa_rating", movie.getMpaa_rating())
                            .withInt("audience_score", movie.getRatings().getAudience_score());
                    table.putItem(item);
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }



    }
}



