package eu.wieslander.plonk;

/**
 * Simple struct class to hold the data for one PLoNK channel --
 * title, link, Parent Directory, Current Directory and
 * Status message from llink
 */

public class PlonkChannel {
    private CharSequence mTitle;
    private CharSequence mLink;
    private CharSequence mParentDir;
    private CharSequence mCurrentDir;
    private CharSequence mStatus;

	public PlonkChannel() {
        mTitle = "";
        mLink = "";
        mParentDir = "";
        mCurrentDir = "";
        mStatus = "";
	}

    public PlonkChannel(CharSequence title, CharSequence link, CharSequence parentDir, CharSequence currentDir, CharSequence status) {
        mTitle = title;
        mLink = link;
        mParentDir = parentDir;
        mCurrentDir = currentDir;
        mStatus = status;
    }

	public void setTitle(CharSequence title) {
		mTitle = title;
	}

	public CharSequence getTitle() {
		return mTitle;
	}
	
	public void setLink(CharSequence link) {
		mLink = link;
	}

	public CharSequence getLink() {
		return mLink;
	}

	public void setParentDir(CharSequence parentDir) {
		mParentDir = parentDir;
	}

	public CharSequence getParentDir() {
		return mParentDir;
	}

	public void setCurrentDir(CharSequence currentDir) {
		mCurrentDir = currentDir;
	}

	public CharSequence getCurrentDir() {
		return mCurrentDir;
	}
	
	public void setStatus(CharSequence status) {
		mStatus = status;
	}

	public CharSequence getStatus() {
		return mStatus;
	}
}
