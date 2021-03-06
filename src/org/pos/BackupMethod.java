package org.pos;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import org.pos.model.Base;
import org.pos.model.Config;

/*
 * Created on May 21, 2017
 *
 */

/**
 * @author Kumail
 *
 */
abstract public class BackupMethod {

	private static Logger logger = Logger.getLogger(BackupMethod.class);

	public BackupMethod() {
	}

	abstract public ArrayList<String> getBackupPackageNames();
	abstract protected boolean loadPackage(String packageName);
	abstract public String createPackage(String tableName);

	public String createPackage() {
		return this.createPackage("");
	}

	public boolean restorePackage(String packageName) throws Exception {
		Base.clearDB();

		return this.loadPackage(packageName);
	}

	public static BackupMethod getCurrentBackupMethod() {
    BackupMethod method = null;
    String methodType = Config.getConfig().getValue("db_backup_method");

    if(methodType.equals("dump")) {
      method = new DBDumpBackup();
    }
    else if(methodType.equals("internal")) { //TODO: this is just a working name

    }
    else {
      logger.warn("Config value db_backup_method is not set to a valid type. Defaulting to 'dump'");
      method = new DBDumpBackup();
    }

    return method;
  }
}
