package manual;

import java.io.File;

public class ServerManual {

    public static String template =
                    "				*** Welcome To Sever ***				\n"+
                    "											            \n"+
                    "	   # Available Commands  : 						    \n";


    public static String getManual(String manual, String path, int hope){
        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] directoryList = directory.listFiles();

            for (int i=0; i < directoryList.length; i++) {
                for(int t=0; t < hope; t++){
                    manual += "\t";
                }
                manual += i + ". " + directoryList[i].getName() + "\n";

                if(directoryList[i].isDirectory()) {
                    manual = getManual(manual, path + "/" + directoryList[i].getName(), hope + 1);
                }

            }

        }

        manual += "\n\n";

        return manual;
    }

}
