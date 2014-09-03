//package goes here;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Used to retrieve XML segments from .xml files.
 * @author Ken Prunier
 *
 */
public final class FormDataFactory {

	/**
	 * A static variable in Java is stored once per class (not once per object, 
	 * such as non-static variables are). This means all your objects (and static methods) 
	 * share the same variable.
	 * 
	 * Declaring a variable as volatile (be it static or not) states that the variable 
	 * will be accessed frequently by multiple threads. In Java, this boils down to instructing *
	 * threads that they can not cache the variable's value, but will have to write back 
	 * immediately after mutating so that other threads see the change. (Threads in Java are 
	 * free to cache variables by default).
	 * 
	 * "Changes to a volatile variable are always visible to other threads. 
	 * What's more, it also means that when a thread reads a volatile variable, 
	 * it sees not just the latest change to the volatile, but also the side 
	 * effects of the code that led up the change." [happens before memory relationship]
	 * 
	 * http://docs.oracle.com/javase/tutorial/essential/concurrency/atomic.html
	 */
	private volatile static FormDataFactory singleton;

	//FORM IDS
	public enum FORM_ID {IT201, IT201V, IT214, W2, DEPEXEMPT, HMBR, PAYMENT};
	
	//FILE NAMES
	private static final String IT201_FILE_NAME 		= "IT201.xml";
	private static final String IT201V_FILE_NAME 		= "IT201V.xml";
	private static final String IT214_FILE_NAME 		= "IT214.xml";
	private static final String W2_FILE_NAME 			= "W2.xml";
	private static final String DEP_INFO_FILE_NAME 		= "DEPEXEMPT.xml";
	private static final String HMBR_FILE_NAME 			= "HMBR.xml";
	private static final String PAYMENT_FILE_NAME 		= "Payment.xml";	
	
	private static Map<String, String> dataObjMap 		= null;
	private static final String DEFAULT_APP_ID			= "app id goes here";
	private String appId								= DEFAULT_APP_ID;
    
	private FormDataFactory() {
		//private constructor to thwart instantiation of a static class
			
    		//Logging ("Running FormDataFactory() constructor");

    		loadFormDataFiles(); //Load form data file contents

		//Logging ("End FormDataFactory() constructor");	
	}
	
    /**
     * Returns the instance of the singleton
     * If the singleton has not been instantiated then the method
     * will lock the class and load the result
     * 
     * http://en.wikipedia.org/wiki/Double-checked_locking
     * http://www.ibm.com/developerworks/java/library/j-dcl/
     * @return
     * @author Ken Prunier
     */
	public static FormDataFactory singleton() {
		//Get the current value of the singleton and store in fdf
		FormDataFactory fdf = singleton;
		if (fdf == null) {
			
			//Thread lock the class
			synchronized(FormDataFactory.class) {
				
				//Get the currently locked value of singleton
				fdf = singleton;
				if (fdf == null) {
	            			//Initializes the intermediary "fdf" with the new instance
	            			//Initializes the "singleton" with the value of the intermediary "fdf"
	            			//This allows the singleton to be accessed once so only a complete variable is set
					//"singleton" is declared as volatile so this change is atomic
	            			singleton = fdf = new FormDataFactory();
				}
			}
		}
		//Return the instantiated singleton
		return singleton;
	}	
    
    public String getFormData(FORM_ID formId) {    	
    	return dataObjMap.get(getFormIdEnumValue(formId));
    }
	
	public void setApplicationId(String applicationId) {
		appId = applicationId;
	}
	
	private String getApplicationPath() {		
		if (appId == null || appId.length() == 0) { appId = DEFAULT_APP_ID; };
	
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext(); 
        String appPath = context.getApplicationPath(appId); 
        if (appPath == null) 
        	//application path not found
        else 
            //application path found
        
        return appPath; 
	}

	private void loadFormDataFiles() {
        try {
        	//logging ("loadFormDataFiles -- start");
        	
		dataObjMap = new TreeMap<String, String>();
			
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.IT201), 	readFile(IT201_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.IT201V), 	readFile(IT201V_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.IT214), 	readFile(IT214_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.W2), 		readFile(W2_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.DEPEXEMPT), 	readFile(DEP_INFO_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.HMBR), 	readFile(HMBR_FILE_NAME));
        	dataObjMap.put(getFormIdEnumValue(FORM_ID.PAYMENT), 	readFile(PAYMENT_FILE_NAME));

            //logging ("loadFormDataFiles -- end");
        } 
        catch (Exception e) {
            //Error Handling
        }
    }
	
	private String readFile(String fileName) {			
		StringBuffer fileContent = new StringBuffer(); 
		FileInputStream fstream;
		String filePath = getApplicationPath() + System.getProperty("file.separator") + fileName;
		
		//logging (this, "readFile() Begin loading file " + filePath);
			
		try {					
			fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
		    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String str;
		    
			while ((str = br.readLine()) != null) {
				fileContent.append(str);		     
			}
			in.close();			
		} 
		catch (FileNotFoundException e) {
            //Error Handling
		} 
		catch (IOException e) {
            //Error Handling
		}
		catch (Exception e) {
            //Error Handling
		}
		
		//logging (this, "readFile() loading file " + filePath + " complete.");
		
		return fileContent.toString();
	}	 
	
	public static String getFormIdEnumValue(FORM_ID formId) {
		String value = "";		
		switch (formId) {
	        case IT201 :  
	        	value = Constants.IT201_FORM_VAL; break;
	        case IT201V :  
	        	value = Constants.IT201V_FORM_VAL; break;
	        case IT214 :  
	        	value = Constants.IT214_FORM_VAL; break;
	        case W2 :  
	        	value = Constants.W2_FORM_VAL; break;
	        case DEPEXEMPT :  
	        	value = Constants.DEPEXEMPT_FORM_VAL; break;
	        case HMBR :  
	        	value = Constants.HMBR_FORM_VAL; break;
	        case PAYMENT :  
	        	value = Constants.PAYMENT_FORM_VAL; break;	           
		}
		return value;
	}
    
}
