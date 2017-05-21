package org.pos.exception;
import org.pos.ui.DriverGUI;


public class EntryAlreadyExistsException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

    public EntryAlreadyExistsException(String msg) {
        super(msg);
    }
}
