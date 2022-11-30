package com.example.demo1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

public class Helper {

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    public static void writeJSONToFile(String jsonString,String location) throws FileNotFoundException {
        File jsonFile = new File(location, Helper.getJSONFileName());

        PrintStream out = new PrintStream(new FileOutputStream(jsonFile));
        out.print(jsonString);
        logger.info("Complete : Successful in writing data to JSON File. \n JSON FileName:: " +jsonFile.getName());
    }

    private static String getFileName() {
        logger.info("Started: Initializing process to create FileName.");

        String previousMonth = LocalDate.now().minusMonths(1).getMonth().toString();
        String year = String.valueOf(LocalDate.now().minusMonths(1).getYear());
        String timeStamp = String.valueOf(System.currentTimeMillis());

        String fileName = previousMonth.concat("_")
                .concat(year).concat("_")
                .concat(timeStamp);

        logger.info("Complete: Successful in creating name. FileName ::" +fileName);
        return fileName;
    }

    public static String getJSONFileName() { return getFileName().concat(".json"); }

}
