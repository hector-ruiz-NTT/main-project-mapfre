package mapfre;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ProjectFileControl {
	
	//Private constructor is necessary (Sonar).
	public ProjectFileControl() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Check if a file exist in that path.
	 * 
	 * @param directoryPath {String} The path of the directory.
	 * @param fileName      {String} the name of the file.
	 * @return {boolean} If the file exist or not.
	 * @throws Exception
	 * @author CEX
	 */
	public static boolean checkFile(String directoryPath, String fileName) {
		//Initialize vars.
		String path = directoryPath + fileName;
		File file = new File(path);

		//Check if it exist.
		return file.exists();
	}

	/**
	 * Count the number of files in the specified folder.
	 * 
	 * @param directoryPath {String} the folder path.
	 * @return
	 * @author CEX
	 */
	public static int countFiles(String directoryPath) {
		//Count the number of files.
		return new File(directoryPath).listFiles().length;
	}

	/**
	 * Count the number of files in the specified folder.
	 * 
	 * @return
	 * @author CEX
	 */
	public static int countFilesDownloaded() {
		//Secure folderPath.
		String folderPath = ProjectPaths.getDownloadPath();
		
		//Count the number of files.
		return new File(folderPath).listFiles().length;
	}

	/**
	 * Delete a file if it exist in the downloads directory.
	 * 
	 * @param fileName {String} The name of the file.
	 * @throws Exception
	 * @throws IOException
	 * @author CEX
	 */
	public static void deleteFile(String directoryPath, String fileName) throws IOException {
		//Initialize vars.
		Path filePath = Paths.get(directoryPath + fileName);

		//Delete if it exist.
		Files.deleteIfExists(filePath);
	}
	
	/**
	 * Delete all files that contains the expecified text in the name.
	 * @param directoryPath {String} the folder.
	 * @param fileName 		{String} The text that file should contains in the name.
	 * @throws IOException
	 * @author CEX
	 */
	public static void deleteAllFiles(String directoryPath, String fileName) throws IOException {
		File folder = new File(directoryPath);
		File[] files = folder.listFiles();
		
		for(File file : files) {
			
			if(file.getName().contains(fileName)) {
				file.delete();
			}
		}
	}
	
	/**
	 * Count th number of files who begin for the espcifed text.
	 * @param directoryPath
	 * @param fileName
	 * @return
	 * @author CEX
	 */
    public static int countNamedFiles(String directoryPath, String fileName) {
        //Get files.
        File[] files = new File(directoryPath).listFiles();
        
        //Number of files.
        int count = 0;
        
        //Count the number of files.
        for(File file : files) {
            if(file.getName().contains(fileName)) {
                count++;
            }
        }
        //Return the files with the name.
        return count;
    }
    
    /**
     * Prepare download folders.
     * 
     * @throws IOException
     * @author CEX
     */
    public static void initializeDownloadFolder() throws IOException {
    	//Intialize vars.
    	String folderPath = ProjectPaths.getDownloadPath();
    	File downloadDirectory = new File(folderPath);
    	
    	//Create folder.
    	if (! downloadDirectory.exists()){
    		downloadDirectory.mkdir();
        }
    	//Clean folder.
    	ProjectFileControl.deleteAllFiles(folderPath, "");
    }

	public static void initializeDownloadFolder(String downloadFolder) throws IOException {
		//Intialize vars.
		String folderPath = downloadFolder;
		File downloadDirectory = new File(folderPath);

		//Create folder.
		if (! downloadDirectory.exists()){
			downloadDirectory.mkdir();
		}
		//Clean folder.
		ProjectFileControl.deleteAllFiles(folderPath, "");
	}
    
    /**
     * Delete the download directory if no one files are been downloaded in the test.
     * 
     * @throws IOException
     * @author CEX
     */
    public static void cleanDownloadFolder() throws IOException {
    	//Intialize vars.
    	String folderPath = ProjectPaths.getDownloadPath();
    	File directory = new File(folderPath);
    	File[] contents = directory.listFiles();
    	
    	//If the folder is empty.
    	if (contents.length == 0) {
        	//Clean folder.
        	ProjectFileControl.deleteAllFiles(folderPath, "");
    	}
    }

}


