package be.ida.jetpack.dictionaryactivation.models;

/**
 * Datasource configuration object.
 */
public class Config {

    private String itemResourceType;

    private Integer offset;

    private Integer limit;

    public Config(String itemResourceType, Integer offset, Integer limit) {
        this.itemResourceType = itemResourceType;
        this.offset = offset;
        this.limit = limit;
    }

    public String getItemResourceType() {
        return itemResourceType;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
