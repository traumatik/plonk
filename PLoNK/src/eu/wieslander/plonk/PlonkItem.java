package eu.wieslander.plonk;

/**
 * Simple struct class to hold the data for one PLoNK item --
 * title, link, description and more to come
 */
/**
 * @author Johan
 *
 */
/**
 * @author Johan
 *
 */
public class PlonkItem  {
    private CharSequence mTitle;
    private CharSequence mLink;
    private CharSequence mDescription;
    private CharSequence mCategory;
    
    public PlonkItem() {
        mTitle = "";
        mLink = "";
        mDescription = "";
        mCategory = "";
    }
    
    public PlonkItem(CharSequence title, CharSequence link, CharSequence description, CharSequence category) {
        mTitle = title;
        mLink = link;
        mDescription = description;
        mCategory = category;
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public void setDescription(CharSequence description) {
        mDescription = description;
    }

    public CharSequence getLink() {
        return mLink;
    }

    public void setLink(CharSequence link) {
        mLink = link;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }
    
    public CharSequence getCategory() {
    	return mCategory;
    }
    
    public void setCategory(CharSequence category){
    	mCategory = category;
    }
    
    /** 
     * <item>
     * #<title>Serier</title>
     * #<link>http://192.168.0.7:8008/Serier</link>
     * #<description></description> 
     * #<category>directory</category>
     * <pubDate>Fri Mar 27 20:02:00 2009</pubDate>
     * <size>4.0KB</size>
     * *<detail_size>4096</detail_size>
     * <pubDate>Fri Mar 27 20:02:00 2009</pubDate>
     * <tvid>1</tvid>
     * <ext></ext>
     * *<icon>http://192.168.0.7:8008//generic.jpg</icon>
     * <name>Serier</name>
     * *<title_local></title_local>
     * <country></country>
     * <length></length>
     * <tagline></tagline>
     * <plot></plot>
     * *<genres></genres>
     * <date></date>
     * <directors></directors>
     * <cast></cast>
     * *<rating></rating>
     * *<imdb_url></imdb_url>
     * <short_url>Serier</short_url>
     * *<visited>text</visited>
     * <guid>http://192.168.0.7:8008/Serier</guid>
     * </item>
     * */
    
}
