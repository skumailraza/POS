package org.pos.model;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.log4j.Logger;

public class Config {
  private HashMap<String, String> config;
  private static Config currentConfig = null;

  private static Logger logger = Logger.getLogger(Config.class);

  private Config(HashMap<String, String> config) {
    this.config = config;
  }

  protected void finalize() {
    this.config.clear();
    this.config = null;
  }

  public String getValue(String key) {
    return config.get(key);
  }

  public static synchronized Config getConfig() {
    //If you dont load a config, all you get is the defaults
    if(Config.currentConfig == null) {
      Config.currentConfig = new Config(Config.loadDefaults());
    }

    return currentConfig;
  }

  protected static HashMap<String, String> loadDefaults() {
    HashMap<String, String> defaults = new HashMap<String, String>();

    //database info
    defaults.put("db_type", "mysql");
    defaults.put("db_host", "localhost");

    //printer
    defaults.put("print_device", "/dev/printer");

    //setup file
    defaults.put("pos_pathto_setup", "pos_setup_$db_type$.sql");

    //backup info
    defaults.put("db_backup_method", "dump");
    defaults.put("db_backup_dir", "./backup");
    defaults.put("db_backup_filename", "backup_$_input$_$currentdatetime$.sql");

    //mysql backup commands
    defaults.put("db_backup_cmd", "mysqldump --quick --host=$db_host$ --user=$db_user$ --password=$db_pass$ $db_name$ $_input$");
    defaults.put("db_restore_cmd", "mysql --host=$db_host$ --user=$db_user$ --password=$db_pass$ $db_name$");

    return defaults;
  }

  public String renderItem(String item, String input) {
    String result = new String(item);
    String sRegex = "\\$(.+?)\\$";
    String variable = null;
    String backRef = null;
    Pattern pRegex = Pattern.compile(sRegex);
    Matcher m = pRegex.matcher(result);

    while(m.find()) {
      backRef = m.group(1);
      variable = getValue(backRef);
      if(variable != null) {
        result = m.replaceFirst(variable);
      }
      else if(backRef.equals("currentdatetime")) {
        Date now = new Date();
        result = m.replaceFirst(now.toString().replaceAll(" ","_"));
      }
      else if(backRef.equals("_input")) {
        result = m.replaceFirst(input);
      }

      m = pRegex.matcher(result); //match against the new string
    }

    return result;
  }

  public static void loadConfig(InputStream fileStream) {
    HashMap<String, String> configMap = Config.loadDefaults();
    BufferedReader bin;

    String cfgRegex = "^\\$(.+?)\\s+?=\\s+?(.+?)$";
    String whitespaceRegex = "^\\s*$";
    Pattern pRegex = Pattern.compile(cfgRegex);
    Pattern wsRegex = Pattern.compile(whitespaceRegex);
    Matcher m;

    try {
      bin = new BufferedReader(new InputStreamReader(fileStream));

      String line = null;
      while((line = bin.readLine()) != null) {
        //check for comment
        if(line.startsWith("//") || line.startsWith("#")) {
          continue;
        }

        //check for blankline
        m = wsRegex.matcher(line);
        if(m.find()) {
          continue;
        }

        //look for config vars
        m = pRegex.matcher(line);
        m.find();

        //if there is a variable and a value; and the variable is valid
        if(m.groupCount() == 2) {
          if(configMap.put(m.group(1), m.group(2)) != null) {
            logger.debug("Replacing default value for variable '" + m.group(1) + "' with value '" + m.group(2) + "'");
          }
        }
        else {
          logger.error("Config error on line: " + line);
        }
      }

      bin.close();
      fileStream.close();
    }
    catch (IllegalStateException e) {
      logger.error("Error parsing config file. All config variables will use defaults.", e);
    }
    catch (IOException e) {
      logger.error("Error loading config file. All config variables will use defaults.", e);
    }

    Config.currentConfig  = new Config(configMap);
  }
}
