package de.echtzeitraum.openpassword;

public interface ViewMode {
	public int getLayoutId ();
	public int getMenuId ();
	public void getControls ();
	public void setControls ();
	public void saveControls ();
	public void generatePassword ();
}
