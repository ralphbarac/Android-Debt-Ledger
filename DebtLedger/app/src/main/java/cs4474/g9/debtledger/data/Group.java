package cs4474.g9.debtledger.data;

public class Group
{
    private String id;
    private String description;
    private String owner;

    public Group(String owner, String desc)
    {
        this.owner = owner;
        description = desc;
    }

    public String getID()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String desc)
    {
        description = desc;
    }
}
